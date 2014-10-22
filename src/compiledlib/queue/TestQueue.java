package compiledlib.queue;

//import gc.Signal;
import static org.junit.Assert.assertEquals;

import java.security.SecureRandom;
import java.util.Arrays;

import oram.CircuitOram;

import org.junit.Test;

import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;
import gc.GCSignal;

public class TestQueue {
	public Mode m;
	java.util.Queue<Integer> cstack = new java.util.LinkedList<Integer>();
	SecureRandom rnd = new SecureRandom();
	int[] op;

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
	
	public void compute(CompEnv<GCSignal> env, Queue<BoolArray> ostack,
			IntegerLib<GCSignal> lib) throws Exception {

		double[] time = new double[op.length];

		for (int i = 0; i < op.length; ++i) {
			if (op[i] == 1 && m == Mode.VERIFY) {
				int res = 0;
				if (env.getParty() == Party.Alice) {
					res = cstack.poll();
				}
				BoolArray scres = ostack.queue_op(new BoolArray(env, lib), lib.SIGNAL_ONE);
				int srintres = Utils.toInt(env.outputToAlice(scres.data));
				if (env.getParty() == Party.Alice && m != Mode.COUNT) {
					System.out.println(env.getParty() + "pop " + res + " "
							+ srintres);// cstack.size());
					assertEquals(res, srintres);
				}

			} else {
				int rand = rnd.nextInt(100);
				if (env.getParty() == Party.Alice && m == Mode.VERIFY) {
					cstack.add(rand);
					System.out.println(env.getParty() + "push " + rand);
				}
				BoolArray tmp = new BoolArray(env, lib);
				tmp.data = env.inputOfAlice(Utils.fromInt(rand, 32));
				if(m == Mode.REAL && env.getParty() == Party.Alice) {
					System.gc();
					double a = System.nanoTime();
					ostack.queue_op(tmp, lib.SIGNAL_ZERO);
					time[i] = (System.nanoTime()-a)/1000000000.0;
				} else 
					ostack.queue_op(tmp, lib.SIGNAL_ZERO);
			}
			env.flush();
		}
		if(m == Mode.REAL && env.getParty() == Party.Alice) {

			Arrays.sort(time);
//			System.out.println(Arrays.toString(time));
			System.out.print(time[op.length/2]);
		}
	}

	class GenRunnable extends network.Server implements Runnable {
		long andGate;
		Statistics sta;
		int logN;

		GenRunnable(int logN) {
			this.logN = logN;
		}

		public void run() {
			try {
				listen(54321);
				CompEnv env = CompEnv.getEnv(m, Party.Alice, is, os);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
				Queue<BoolArray> ostack = new Queue<BoolArray>(env, lib,
						logN, new BoolArray(env, lib), new CircuitOram<GCSignal>(env,
								1 << logN, new QueueNode<>(env, lib, logN, new BoolArray(env, lib)).numBits()));

				if (m == Mode.COUNT) {
					sta = ((PMCompEnv) (env)).statistic;
					sta.flush();
				}
				compute(env, ostack, lib);
				disconnect();
				if (m == Mode.COUNT) {
					sta.finalize();
					System.out.print(sta.andGate + " " + sta.NumEncAlice);
				}

			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		int logN;
		EvaRunnable(int logN) {
			this.logN = logN;
		}

		public void run() {
			try {
				connect("localhost", 54321);
				CompEnv<GCSignal> env = CompEnv.getEnv(m, Party.Bob, is, os);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
				Queue<BoolArray> ostack = new Queue<BoolArray>(env, lib,
						logN, new BoolArray(env, lib), new CircuitOram<GCSignal>(env,
								1 << logN, new QueueNode<>(env, lib, logN, new BoolArray(env, lib)).numBits()));
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
		m = Mode.VERIFY;
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
		m = Mode.COUNT;
		GenRunnable gen = new GenRunnable(logN);
		EvaRunnable eva = new EvaRunnable(logN);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start();
		Thread.sleep(50);
		tEva.start();
		tGen.join();
		return gen.sta;
	}
	
	public static void main(String[] args) throws InterruptedException {
		int logN = new Integer(args[0]);
		TestQueue a = new TestQueue();
		a.getInput(20);
		a.m = Mode.REAL;
		GenRunnable gen = a.new GenRunnable(logN);
		EvaRunnable eva = a.new EvaRunnable(logN);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
	}
}
