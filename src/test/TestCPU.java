package test;

import oram.SecureArray;
import util.Utils;
import circuits.IntegerLib;

import compiledlib.CPU;

import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;

//import gc.Boolean;

public class TestCPU {

	static final int REGISTER_SIZE = 64;
	static final int MEM_SIZE = 2048;// 2K words
	static final int WORD_SIZE = 32;
	static final int NUMBER_OF_STEPS = 1;
	static final Mode m = Mode.VERIFY;
	int[] mem;

	public TestCPU() {
		mem = new int[MEM_SIZE];
		// http://www.mrc.uidaho.edu/mrc/people/jff/digital/MIPSir.html
		// 001001ssssstttttddddd00000100001 for ADDU s t -> d
		mem[0] = 0b00000000001000000001000000100001;
	}

	public SecureArray<Boolean> getRegister(CompEnv<Boolean> env)
			throws Exception {

		// inital registers are all 0's. no need to set value.
		SecureArray<Boolean> oram = new SecureArray<Boolean>(env,
				REGISTER_SIZE, WORD_SIZE);

		// for testing purpose.
		// reg[0]=3 reg[1] = 4;
		oram.write(env.inputOfAlice(Utils.fromInt(0, oram.lengthOfIden)),
				env.inputOfAlice(Utils.fromInt(3, WORD_SIZE)));
		oram.write(env.inputOfAlice(Utils.fromInt(1, oram.lengthOfIden)),
				env.inputOfAlice(Utils.fromInt(4, WORD_SIZE)));
		env.flush();
		return oram;
	}

	public SecureArray<Boolean> getMemory(CompEnv<Boolean> env)
			throws Exception {
		IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
		// inital registers are all 0's. no need to set value.
		SecureArray<Boolean> oram = new SecureArray<Boolean>(env, MEM_SIZE,
				WORD_SIZE);
		for (int i = 0; i < 2; ++i) {
			// index can be evaluated publicly.
			Boolean[] index = lib.toSignals(i, oram.lengthOfIden);

			Boolean[] data;
			if (env.getParty() == Party.Alice) {
				data = env.inputOfAlice(Utils.fromInt(mem[i], WORD_SIZE));
			} else {
				data = env.inputOfAlice(new boolean[WORD_SIZE]);
			}
			oram.write(index, data);
		}
		return oram;
	}

	class GenRunnable extends network.Server implements Runnable {
		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv<Boolean> env = CompEnv.getEnv(m, Party.Alice, is, os);
				IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
				CPU cpu = new CPU(env, lib);
				SecureArray<Boolean> reg = getRegister(env);
				SecureArray<Boolean> memory = getMemory(env);

				Boolean[] pc = lib.toSignals(0, WORD_SIZE);
				for (int i = 0; i < NUMBER_OF_STEPS; ++i) {
					Boolean[] inst = memory.read(pc);
					pc = cpu.function(reg, inst, pc);
				}

				Boolean[] reg2 = reg.read(lib.toSignals(2, reg.lengthOfIden));
				os.flush();
				System.out.println(Utils.toInt(env.outputToAlice(reg2)));
				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		public double andgates;
		public double encs;

		public void run() {
			try {
				connect("localhost", 54321);
				@SuppressWarnings("unchecked")
				CompEnv<Boolean> env = CompEnv.getEnv(m, Party.Bob, is, os);
				IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
				CPU cpu = new CPU(env, lib);
				SecureArray<Boolean> reg = getRegister(env);
				SecureArray<Boolean> memory = getMemory(env);
				Boolean[] pc = lib.toSignals(0, WORD_SIZE);
				if (m == Mode.COUNT) {
					Statistics sta = ((PMCompEnv) env).statistic;
					sta.flush();
				}
				for (int i = 0; i < NUMBER_OF_STEPS; ++i) {
					Boolean[] inst = memory.read(pc);
					pc = cpu.function(reg, inst, pc);
				}
				Boolean[] reg2 = reg.read(lib.toSignals(2, reg.lengthOfIden));
				os.flush();

				System.out.println(Utils.toInt(env.outputToAlice(reg2)));
				if (m == Mode.COUNT) {
					Statistics sta = ((PMCompEnv) env).statistic;
					sta.finalize();
					andgates = sta.andGate;
					encs = sta.NumEncAlice;
				}
				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	static public void main(String args[]) throws Exception {
		TestCPU test = new TestCPU();
		GenRunnable gen = test.new GenRunnable();
		EvaRunnable env = test.new EvaRunnable();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(env);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();

		if (m == Mode.COUNT) {
			System.out.println(env.andgates + " " + env.encs);
		} else {
		}

	}
}