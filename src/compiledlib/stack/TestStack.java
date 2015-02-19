package compiledlib.stack;

import static org.junit.Assert.assertEquals;

import java.security.SecureRandom;
import java.util.Arrays;

import oram.CircuitOram;

import org.junit.Test;

import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;
import gc.GCSignal;

public class TestStack {

	static java.util.Stack<Integer> cstack = new java.util.Stack<Integer>();
	static SecureRandom rnd = new SecureRandom();
	static int[] op;
	static{
		op = new int[10];
		for (int i = 0; i < op.length; ++i)
			op[i] = 0;//rnd.nextInt(2);
		int size = 0;
		for (int i = 0; i < 10; ++i) {
			if (size == 0) {
				op[i] = 0;
			}
			if (op[i] == 0)
				size++;
			else {
				size--;
			}
		}

	}

	public TestStack(){
		//		getInput(10);
	}

	public void getInput(int length) {
		op = new int[length];
		for (int i = 0; i < op.length; ++i)
			op[i] = rnd.nextInt(2);
		int size = 0;
		for (int i = 0; i < length; ++i) {
			if (size == 0) {
				op[i] = 0;
			}
			if (op[i] == 0)
				size++;
			else {
				size--;
			}
		}
	}

	public static void compute(CompEnv<GCSignal> env, Stack<BoolArray> ostack,
			IntegerLib<GCSignal> lib) throws Exception {

		double[] time = new double[op.length];

		for (int i = 0; i < op.length; ++i) {
			System.out.println(i);
			if (op[i] == 1 && Flag.mode == Mode.VERIFY) {
				int res = 0;
				if (env.getParty() == Party.Alice) {
					res = cstack.pop();
				}
				BoolArray scres = ostack.stack_op(new BoolArray(env), lib.SIGNAL_ONE);
				int srintres = Utils.toInt(env.outputToAlice(scres.data));
				if (env.getParty() == Party.Alice && Flag.mode != Mode.COUNT) {
					System.out.println(env.getParty() + "pop " + res + " "
							+ srintres);// cstack.size());
					assertEquals(res, srintres);
				}

			} else {
				int rand = rnd.nextInt(100);
				if (env.getParty() == Party.Alice && Flag.mode == Mode.VERIFY) {
					cstack.push(rand);
					System.out.println(env.getParty() + "push " + rand);
				}
				BoolArray tmp = new BoolArray(env);
				tmp.data = env.inputOfAlice(Utils.fromInt(rand, 32));
				if(env.getParty() == Party.Alice) {
					System.gc();
					double a = System.nanoTime();
					ostack.stack_op(tmp, lib.SIGNAL_ZERO);
					time[i] = (System.nanoTime()-a)/1000000000.0;
				} else 
					ostack.stack_op(tmp, lib.SIGNAL_ZERO);
			}
			env.flush();
		}
		if(Flag.mode == Mode.REAL && env.getParty() == Party.Alice) {

			Arrays.sort(time);
			//			System.out.println(Arrays.toString(time));
			System.out.println(op.length+" operations on a stack with one million capacity is performed.\n Each stack operation takes "+time[op.length/2]+" seconds on average");
			//			System.out.print();
		}
	}

	public static class GenRunnable extends network.Server implements Runnable {
		GCSignal[] z;
		long andGate;
		Statistics sta;
		int logN = -1;

		String host; int port;
		GenRunnable(int logN, String host, int port) {
			this.logN = logN;
			this.host = host;
			this.port = port;
		}

		GenRunnable(int logN) {
			this.logN = logN;
			host = "localhost";
			port = 54321;
		}


		public void run() {
			try {
				listen(port);
				CompEnv env = CompEnv.getEnv(Flag.mode, Party.Alice, is, os);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
				Stack<BoolArray> ostack = new Stack<BoolArray>(env,
						logN, new BoolArray(env), new CircuitOram<GCSignal>(env,
								1 << logN, 32 + logN));
				if (Flag.mode == Mode.COUNT) {
					sta = ((PMCompEnv) (env)).statistic;
					sta.flush();
				}

				compute(env, ostack, lib);
				disconnect();
				if (Flag.mode == Mode.COUNT) {
					sta = ((PMCompEnv) (env)).statistic;
					sta.finalize();
					System.out.print(sta.andGate + "\t" + sta.NumEncAlice
							+ "\t");
				}

			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static class EvaRunnable extends network.Client implements Runnable {
		int logN;
		String host; int port;
		EvaRunnable(int logN, String host, int port) {
			this.logN = logN;
			this.host = host;
			this.port = port;
		}

		EvaRunnable(int logN) {
			this.logN = logN;
			host = "localhost";
			port = 54321;
		}


		public void run() {
			try {
				connect(host, port);
				CompEnv env = CompEnv.getEnv(Party.Bob, is, os);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
				System.out.println("..");
				Stack<BoolArray> ostack = new Stack<BoolArray>(env,
						logN, new BoolArray(env), new CircuitOram<GCSignal>(env,
								1 << logN, 32 + logN));
				System.out.println("..");
				compute(env, ostack, lib);
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	@Test
	public void runThreads() throws Exception {
		getInput(100);

		GenRunnable gen = new GenRunnable(20);
		EvaRunnable eva = new EvaRunnable(20);

		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
	}

	public Statistics getCount(int logN) throws InterruptedException {
		getInput(1);
		GenRunnable gen = new GenRunnable(logN);
		EvaRunnable eva = new EvaRunnable(logN);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		return gen.sta;
	}

	public static void main(String[] args) throws InterruptedException {
		int logN = new Integer(args[1]);
		if(new Integer(args[0]) == 1) {
			TestStack a = new TestStack();
			a.getInput(5);
			GenRunnable gen = new GenRunnable(logN, args[2], new Integer(args[3]));
			Thread tGen = new Thread(gen);
			tGen.run();		
		}
		else {
			TestStack a = new TestStack();
			a.getInput(5);
			EvaRunnable eva = new EvaRunnable(logN, args[2], new Integer(args[3]));
			Thread tEva = new Thread(eva);
			tEva.run();
		}
	}
}
