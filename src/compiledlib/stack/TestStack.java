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

	public static<T> void compute(CompEnv<T> env, Stack<T, BoolArray<T>> ostack,
			IntegerLib<T> lib) throws Exception {

		double[] time = new double[op.length];

		
		for (int i = 0; i < op.length; ++i) {
			System.out.println(i);

			if (op[i] == 1 && Flag.mode == Mode.VERIFY) {
				int res = 0;
				if (env.getParty() == Party.Alice) {
					res = cstack.pop();
				}
				BoolArray<T> scres = ostack.stack_op(new BoolArray<T>(env), lib.SIGNAL_ONE);
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
				tmp.data = env.inputOfAlice(Utils.fromInt(rand, 1024));
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

	public static class GenRunnable<T> extends network.Server implements Runnable {
		T[] z;
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
				CompEnv env = CompEnv.getEnv(Party.Alice, is, os);
				IntegerLib<T> lib = new IntegerLib<T>(env);
				Stack<T, BoolArray<T>> ostack = new Stack<T, BoolArray<T>>(env,
						logN, new BoolArray<T>(env), new CircuitOram<T>(env,
								1 << logN, 1024 + logN));
				if (Flag.mode == Mode.COUNT) {
					sta = ((PMCompEnv) (env)).statistic;
					sta.flush();
				}

				compute(env, ostack, lib);
				disconnect();
				if (Flag.mode == Mode.COUNT) {
					sta = ((PMCompEnv) (env)).statistic;
					sta.finalize();
					System.out.print(sta.andGate + "\t");
				}

			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static class EvaRunnable<T> extends network.Client implements Runnable {
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
				IntegerLib<T> lib = new IntegerLib<T>(env);
				Stack<T, BoolArray<T>> ostack = new Stack<T, BoolArray<T>>(env,
						logN, new BoolArray<T>(env), new CircuitOram<T>(env,
								1 << logN, 1024 + logN));
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
		int logN = 23;
		if(new Integer(args[0]) == 1) {
			TestStack a = new TestStack();
			a.getInput(5);
			GenRunnable gen = new GenRunnable(logN, "localhost", 54321);
			Thread tGen = new Thread(gen);
			tGen.run();		
		}
		else {
			TestStack a = new TestStack();
			a.getInput(5);
			EvaRunnable eva = new EvaRunnable(logN, "localhost", 54321);
			Thread tEva = new Thread(eva);
			tEva.run();
		}
	}
}
