package compiledlib.priority_queue;

import java.awt.Toolkit;
import java.util.Random;

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

public class TestPriorityQueue {

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

	public void compute(CompEnv<GCSignal> env, PriorityQueue<GCSignal, BoolArray<GCSignal>> ostack,
			IntegerLib<GCSignal> lib) throws Exception {
		double[] time = new double[op.length];
		BoolArray tmp1 = new BoolArray(env);
		tmp1.data = env.inputOfAlice(Utils.fromInt(15, 32));
		ostack.init();
//		ostack.pqueue_op(env.inputOfAlice(Utils.fromInt(15, 32)), 
//				tmp1, lib.SIGNAL_ZERO);
//		ostack.pqueue_op(env.inputOfAlice(Utils.fromInt(15, 32)), 
//				tmp1, lib.SIGNAL_ONE);

		
		for (int i = 0; i < op.length; ++i) {
			if (op[i] == 1 && Flag.mode == Mode.VERIFY) {
				int res = 0;
				if (env.getParty() == Party.Alice) {
//					res = cstack.poll();
				}
				BoolArray tmp = new BoolArray(env);
				KeyValue<GCSignal, BoolArray<GCSignal>> scres = ostack.pqueue_op(env.inputOfAlice(Utils.fromInt(0, 32)), tmp,lib.SIGNAL_ONE);
//				KeyValue<BoolArray> scres = ostack.pop(lib.SIGNAL_ONE);
				
				int srintres = Utils.toInt(env.outputToAlice(scres.key));
				int ssize = Utils.toInt(env.outputToAlice(ostack.size));
				if (env.getParty() == Party.Alice && Flag.mode == Mode.VERIFY) {
					System.out.println(env.getParty() + "pop " + (100 - res)
							+ " " + srintres);
					// Assert.assertEquals(100-res, srintres);
				}
			} else {
				int rand = next[i];
				BoolArray tmp = new BoolArray(env);
				GCSignal[] in = env.inputOfAlice(Utils.fromInt(rand, 32));
				
				if(env.getParty() == Party.Alice) {
					System.gc();
					double a = System.nanoTime();
					ostack.pqueue_op(in, tmp, lib.SIGNAL_ZERO);
//					ostack.push(in, tmp, lib.SIGNAL_ONE);
					time[i] = (System.nanoTime()-a)/1000000000.0;
				} else 
					ostack.pqueue_op(in, tmp, lib.SIGNAL_ZERO);
//					ostack.push(in, tmp, lib.SIGNAL_ONE);
				int ssize = Utils.toInt(env.outputToAlice(ostack.size));
				if (env.getParty() == Party.Alice && Flag.mode == Mode.VERIFY) {
					cstack.add(100 - rand);
					System.out.println(env.getParty() + "push " + rand);
				}
			}

			env.flush();
		}
		if(env.getParty() == Party.Alice) {
			double res = 0;for(int i = 0; i < time.length; ++i)
				res +=time[i];
			System.out.println(res/time.length);
//			Arrays.sort(time);
//			System.out.println(Arrays.toString(time));
//			System.out.print(time[op.length/2]);
		}
		Toolkit.getDefaultToolkit().beep();
	}

	class GenRunnable extends network.Server implements Runnable {
		boolean[] z;
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
				listen(54321);
				CompEnv env = CompEnv.getEnv(Party.Alice, is, os);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
				PriorityQueueNode<GCSignal, BoolArray<GCSignal>> node = new PriorityQueueNode<GCSignal, BoolArray<GCSignal>>(
						env, logN, new BoolArray(env));
				PriorityQueue<GCSignal, BoolArray<GCSignal>> ostack = new PriorityQueue<GCSignal, BoolArray<GCSignal>>(
						env,
						logN, new BoolArray<GCSignal>(env),
						new CircuitOram<GCSignal>(env, 1 << logN, node.numBits()));
				
				if (Flag.mode== Mode.COUNT) {
					sta = ((PMCompEnv) (env)).statistic;
					sta.flush();
				}
				compute(env, ostack, lib);
				disconnect();
				if (Flag.mode == Mode.COUNT) {
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
				connect("localhost", 54321);
				CompEnv env = CompEnv.getEnv(Party.Bob, is, os);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(env);
				PriorityQueueNode<GCSignal, BoolArray<GCSignal>> node = new PriorityQueueNode<GCSignal, BoolArray<GCSignal>>(
						env, logN, new BoolArray(env));
				PriorityQueue<GCSignal, BoolArray<GCSignal>> ostack = new PriorityQueue<GCSignal, BoolArray<GCSignal>>(
						env,
						logN, new BoolArray<GCSignal>(env),
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
		getInput(11);
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

	public static void main2(String[] args) throws InterruptedException {
		int logN = new Integer(args[0]);
		TestPriorityQueue a = new TestPriorityQueue();
		a.getInput(10);
		GenRunnable gen = a.new GenRunnable(logN);
		EvaRunnable eva = a.new EvaRunnable(logN);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
	}
	

	public static void main(String[] args) throws InterruptedException {
		int logN = new Integer(args[1]);
		if(new Integer(args[0]) == 1) {
			TestPriorityQueue a = new TestPriorityQueue();
			a.getInput(40);
			GenRunnable gen = a.new GenRunnable(logN, args[2], new Integer(args[3]));
			Thread tGen = new Thread(gen);
			tGen.run();		
		}
		else {
			TestPriorityQueue a = new TestPriorityQueue();
			a.getInput(40);
			EvaRunnable eva = a.new EvaRunnable(logN, args[2], new Integer(args[3]));
			Thread tEva = new Thread(eva);
			tEva.run();
		}
	}
}