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

public class TestPriorityQueue {

	public Mode m;
	java.util.PriorityQueue<Integer> cstack = new java.util.PriorityQueue<Integer>();
	// SecureRandom rnd = new SecureRandom();
	Random rnd = new Random(12345);
//	Random rnd = new Random();
	int[] op;
	int[] next;

	public void getInput(int length) {
		op = new int[length];
		for (int i = 0; i < op.length; ++i)
			op[i] = rnd.nextInt(2);
		int size = 0;
		for (int i = 0; i < length/2; ++i) {
			if (size == 0) {
				op[i] = 0;
			}
			if (op[i] == 0)
				size++;
			else {
				size--;
			}
		}
		//
		for (int i = 0; i < length/2; ++i) {
				op[i] = 0;
		}
		for (int i = 0; i < length/2; ++i) {
			op[length/2+i] = 1;
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
//		next[0] = 3;
//		next[1] = 0;
//		op[0] = 1;
	}

	boolean debug = false;

	public void compute(CompEnv<Boolean> env, PriorityQueue<BoolArray> ostack,
			IntegerLib<Boolean> lib) throws Exception {
		double[] time = new double[op.length];
		BoolArray tmp1 = new BoolArray(env);
		tmp1.data = env.inputOfAlice(Utils.fromInt(15, 32));
		ostack.init();
//		ostack.pqueue_op(env.inputOfAlice(Utils.fromInt(15, 32)), 
//				tmp1, lib.SIGNAL_ZERO);
//		ostack.pqueue_op(env.inputOfAlice(Utils.fromInt(15, 32)), 
//				tmp1, lib.SIGNAL_ONE);

		
		for (int i = 0; i < op.length; ++i) {
			if (op[i] == 1 && m == Mode.VERIFY) {
				int res = 0;
				if (env.getParty() == Party.Alice) {
//					res = cstack.poll();
				}
				BoolArray tmp = new BoolArray(env);
				KeyValue<BoolArray> scres = ostack.pqueue_op(env.inputOfAlice(Utils.fromInt(0, 32)), tmp,lib.SIGNAL_ONE);
//				KeyValue<BoolArray> scres = ostack.pop(lib.SIGNAL_ONE);
				
				int srintres = Utils.toInt(env.outputToAlice(scres.key));
				int ssize = Utils.toInt(env.outputToAlice(ostack.size));
				if (env.getParty() == Party.Alice && m == Mode.VERIFY) {
					System.out.println(env.getParty() + "pop " + (100 - res)
							+ " " + srintres);
					// Assert.assertEquals(100-res, srintres);
				}
			} else {
				int rand = next[i];
				BoolArray tmp = new BoolArray(env);
				Boolean[] in = env.inputOfAlice(Utils.fromInt(rand, 32));
				
				if(m == Mode.REAL && env.getParty() == Party.Alice) {
					System.gc();
					double a = System.nanoTime();
					ostack.pqueue_op(in, tmp, lib.SIGNAL_ZERO);
//					ostack.push(in, tmp, lib.SIGNAL_ONE);
					time[i] = (System.nanoTime()-a)/1000000000.0;
				} else 
					ostack.pqueue_op(in, tmp, lib.SIGNAL_ZERO);
//					ostack.push(in, tmp, lib.SIGNAL_ONE);
				int ssize = Utils.toInt(env.outputToAlice(ostack.size));
				if (env.getParty() == Party.Alice && m == Mode.VERIFY) {
					cstack.add(100 - rand);
					System.out.println(env.getParty() + "push " + rand);
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
				IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
				PriorityQueueNode<BoolArray> node = new PriorityQueueNode<BoolArray>(
						env, logN, new BoolArray(env));
				PriorityQueue<BoolArray> ostack = new PriorityQueue<BoolArray>(
						env,
						logN, new BoolArray(env),
						new CircuitOram<Boolean>(env, 1 << logN, node.numBits()));
				
				if (m == Mode.COUNT) {
					sta = ((PMCompEnv) (env)).statistic;
					sta.flush();
				}
				compute(env, ostack, lib);
				disconnect();
				if (m == Mode.COUNT) {
					sta = ((PMCompEnv) (env)).statistic;
					sta.finalize();
//					System.out.print(sta.andGate + "\t" + sta.NumEncAlice
//							+ "\t");
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
				CompEnv<Boolean> env = CompEnv.getEnv(m, Party.Bob, is, os);
				IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
				PriorityQueueNode<BoolArray> node = new PriorityQueueNode<BoolArray>(
						env, logN, new BoolArray(env));
				PriorityQueue<BoolArray> ostack = new PriorityQueue<BoolArray>(
						env,
						logN, new BoolArray(env),
						new CircuitOram<Boolean>(env, 1 << logN, node.numBits()));

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
		getInput(11);
		m = Mode.VERIFY;
		GenRunnable gen = new GenRunnable(10);
		EvaRunnable eva = new EvaRunnable(10);
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