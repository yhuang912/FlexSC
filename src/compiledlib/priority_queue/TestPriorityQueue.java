package compiledlib.priority_queue;

import java.awt.Toolkit;
import java.util.Arrays;
import java.util.Random;

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

public class TestPriorityQueue {

	public Mode m;
	java.util.PriorityQueue<Integer> cstack = new java.util.PriorityQueue<Integer>();
	// SecureRandom rnd = new SecureRandom();
	Random rnd = new Random(12345);
	int[] op;
	int[] next;

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
		next = new int[length];
		for (int i = 0; i < next.length; ++i)
			next[i] = i + 1;
		for (int i = 0; i < 3 * length; ++i) {
			int a = rnd.nextInt(length);
			int b = rnd.nextInt(length);
			int tmp = next[a];
			next[a] = next[b];
			next[b] = tmp;
		}
	}

	boolean debug = false;

	public void compute(CompEnv<GCSignal> env, PriorityQueue<BoolArray> ostack,
			IntegerLib<GCSignal> lib) throws Exception {
		double[] time = new double[op.length];

		for (int i = 0; i < op.length; ++i) {
			if (op[i] == 1 && m == Mode.VERIFY) {
				int res = 0;
				if (env.getParty() == Party.Alice) {
					res = cstack.poll();
				}
				BoolArray tmp = new BoolArray(env, lib);
				KeyValue<BoolArray> scres = ostack.pqueue_op(
						env.inputOfAlice(Utils.fromInt(0, 32)), tmp,
						lib.toSignals(1, 2));
				int srintres = Utils.toInt(env.outputToAlice(scres.key));
				int ssize = Utils.toInt(env.outputToAlice(ostack.size));
				if (env.getParty() == Party.Alice && m == Mode.VERIFY) {
					System.out.println(env.getParty() + "pop " + (100 - res)
							+ " " + srintres + " " + i + ", " + cstack.size()
							+ " " + ssize);// cstack.size());
					// Assert.assertEquals(100-res, srintres);
				}
			} else {
				int rand = next[i];// rnd.nextInt(100);
				BoolArray tmp = new BoolArray(env, lib);
				GCSignal[] in = env.inputOfAlice(Utils.fromInt(rand, 32));
				if(m == Mode.REAL && env.getParty() == Party.Alice) {
					System.gc();
					double a = System.nanoTime();
					ostack.pqueue_op(in, tmp, lib.toSignals(0, 2));
					time[i] = (System.nanoTime()-a)/1000000000.0;
				} else 
					ostack.pqueue_op(in, tmp, lib.toSignals(0, 2));
				
				int ssize = Utils.toInt(env.outputToAlice(ostack.size));
				if (env.getParty() == Party.Alice && m == Mode.VERIFY) {
					cstack.add(100 - rand);
					System.out.println(env.getParty() + "push " + rand + "    "
							+ i + ", " + cstack.size() + " " + ssize);
				}
			}
			env.flush();
		}
		if(m == Mode.REAL && env.getParty() == Party.Alice) {

			Arrays.sort(time);
//			System.out.println(Arrays.toString(time));
			System.out.print(time[op.length/2]);
		}
		Toolkit.getDefaultToolkit().beep();
	}

	class GenRunnable extends network.Server implements Runnable {
		boolean[] z;
		long andGate;
		Statistics sta;
		int logN = -1;

		GenRunnable(int logN) {
			this.logN = logN;
		}

		public void run() {
			try {
				listen(54321);
				CompEnv env = CompEnv.getEnv(m, Party.Alice, is, os);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
				PriorityQueueNode<BoolArray> node = new PriorityQueueNode<BoolArray>(
						env, lib, logN, new BoolArray(env, lib));
				PriorityQueue<BoolArray> ostack = new PriorityQueue<BoolArray>(
						env,
						lib,
						logN, new BoolArray(env, lib),
						new CircuitOram<GCSignal>(env, 1 << logN, node.numBits()));
				if (m == Mode.COUNT) {
					sta = ((PMCompEnv) (env)).statistic;
					sta.flush();
				}
				compute(env, ostack, lib);
				disconnect();
				if (m == Mode.COUNT) {
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
				PriorityQueueNode<BoolArray> node = new PriorityQueueNode<BoolArray>(
						env, lib, logN, new BoolArray(env, lib));
				PriorityQueue<BoolArray> ostack = new PriorityQueue<BoolArray>(
						env,
						lib,
						logN, new BoolArray(env, lib),
						new CircuitOram<GCSignal>(env, 1 << logN, node.numBits()));

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
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		return gen.sta;
	}

	public static void main(String[] args) throws InterruptedException {
		int logN = new Integer(args[0]);
		TestPriorityQueue a = new TestPriorityQueue();
		a.getInput(10);
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