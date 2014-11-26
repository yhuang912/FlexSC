//package test;
import compiledlib.dov.*;

import oram.SecureArray;
import util.Utils;
import circuits.arithmetic.IntegerLib;

import com.appcomsci.mips.binary.DataSegment;
import com.appcomsci.mips.binary.Reader;
import com.appcomsci.mips.binary.SymbolTableEntry;
import com.appcomsci.sfe.common.Configuration;

import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;

import jargs.gnu.CmdLineParser;

import java.io.File;
import java.math.BigInteger;
//import gc.Boolean;

public class TestCPU {

	static final int REGISTER_SIZE = 64;
	static final int MEM_SIZE = 2048;// 2K words
	static final int WORD_SIZE = 32;
	static final int NUMBER_OF_STEPS = 1;
	static final Mode m = Mode.VERIFY;
	int[] mem;
	Configuration config;
	 
	
	public TestCPU(Configuration config) {
		this.config = config;
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
		// reg[3]=5 reg[4] = 6;
		oram.write(env.inputOfAlice(Utils.fromInt(3, oram.lengthOfIden)),
				env.inputOfAlice(Utils.fromInt(5, WORD_SIZE)));
		oram.write(env.inputOfAlice(Utils.fromInt(4, oram.lengthOfIden)),
				env.inputOfAlice(Utils.fromInt(6, WORD_SIZE)));
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
	public SecureArray<Boolean> getInstructionsGen(CompEnv<Boolean> env, DataSegment instData)
			throws Exception {
		boolean[][] instructions = null; 
		int numInst = instData.getDataLength();
		instructions = instData.getDataAsBoolean(); 
		
		SecureArray<Boolean> inst = new SecureArray<Boolean>(env, numInst, WORD_SIZE);
		IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
		Boolean[] data; 
		Boolean[] index;

		for (int i = 0; i < numInst; i++){
			index = lib.toSignals(i, inst.lengthOfIden);
			data = env.inputOfAlice(instructions[i]);
			inst.write(index, data);
		}				
		return inst;
	}							

	public SecureArray<Boolean> getInstructionsEva(CompEnv<Boolean> env, int numInst)
			throws Exception {
		boolean[][] instructions = null; 
		
		SecureArray<Boolean> inst = new SecureArray<Boolean>(env, numInst, WORD_SIZE);
		IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
		Boolean[] data; 
		Boolean[] index;

		for (int i = 0; i < numInst; i++){
			index = lib.toSignals(i, inst.lengthOfIden);
			data = env.inputOfAlice(new boolean[WORD_SIZE]);
			inst.write(index, data);
		}			
		return inst;
	}		
	
	class GenRunnable extends network.Server implements Runnable {
		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv<Boolean> env = CompEnv.getEnv(m, Party.Alice, is, os);
				IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
				CPU cpu = new CPU(env, lib);
				MEM mem = new MEM(env, lib);
				//TestCPU cpu = new TestCPU(config);
				Reader rdr = new Reader(new File(config.getBinaryFileName()), config);
				SymbolTableEntry ent = rdr.getSymbolTableEntry(config.getEntryPoint());
				DataSegment inst = rdr.getInstructions(config.getFunctionLoadList());
				SecureArray<Boolean> instructionBank = getInstructionsGen(env, inst);
				
				//Xiao's two lines
				SecureArray<Boolean> reg = getRegister(env);
				//SecureArray<Boolean> memory = getMemory(env);
				
				//could this cast cause problems when msb is 1?
				Boolean[] pc = lib.toSignals((int)ent.getAddress(), WORD_SIZE);
				for (int i = 0; i < inst.getDataLength(); ++i) {
					Boolean[] instruction = instructionBank.read(lib.toSignals(i, WORD_SIZE));
					pc = cpu.function(reg, instruction, pc);
					
				}

				//Xiao's reading of register value after computation. 
				//Boolean[] reg2 = reg.read(lib.toSignals(2, reg.lengthOfIden));
				os.flush();
				//System.out.println(Utils.toInt(env.outputToAlice(reg2)));
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
				
				//might be better to have bob send the number of instructions to alice.  That's the only reason 
				//we currently read the file at all. 
				Reader rdr = new Reader(new File(config.getBinaryFileName()), config);
				SymbolTableEntry ent = rdr.getSymbolTableEntry(config.getEntryPoint());
				DataSegment inst = rdr.getInstructions(config.getFunctionLoadList());
				int numInst = inst.getDataLength();
				
				SecureArray<Boolean> instructionBank = getInstructionsEva(env, numInst);
				
				SecureArray<Boolean> reg = getRegister(env);
				//SecureArray<Boolean> memory = getMemory(env);
				
				Boolean[] pc = lib.toSignals(0, WORD_SIZE);
				if (m == Mode.COUNT) {
					Statistics sta = ((PMCompEnv) env).statistic;
					sta.flush();
				}
				for (int i = 0; i < numInst; ++i) {
					Boolean[] instruction = instructionBank.read(lib.toSignals(i, WORD_SIZE));
					pc = cpu.function(reg, instruction, pc);
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
	private static void process_cmdline_args(String[] args, Configuration config) {
		CmdLineParser parser = new CmdLineParser();

		try {
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			printUsage();
			System.exit(2);
		}
		
		// Pick off file name, which should be remaining arg
		// (and currently only arg)
		// If no file name, will get from properties file.
		// This is probably an error, but allow it in the interest
		// of backwards compatibility.

		String rest[] = parser.getRemainingArgs();
		if(rest.length > 1) {
			printUsage();
			System.exit(2);
		}
		if(rest.length > 0) {
			config.setBinaryFileName(rest[0]);
		}
	}
	private static void printUsage() {
		System.out.println("Usage: java RunACSEmulatorServer [binary file]");
	}
	static public void main(String args[]) throws Exception {
		System.out.println("got here!");
		Configuration config = new Configuration();
		TestCPU test = new TestCPU(config);
		process_cmdline_args(args, config);
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