package compiledlib.avltree;

import java.util.Random;

import oram.CircuitOram;

import org.junit.Test;

import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;

public class TestAVL {

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

	public class cmpp extends FUNC_1_INTsecure_T1_T1<BoolArray> {

		public cmpp(CompEnv<java.lang.Boolean> env,
				IntegerLib<java.lang.Boolean> lib, BoolArray factoryK)
				throws Exception {
			super(env, lib, factoryK);
		}

		@Override
		public java.lang.Boolean[] calc(BoolArray x0, BoolArray x1)
				throws Exception {
			Boolean res = lib.leq(x0.data, x1.data);
			return new Boolean[]{res, lib.SIGNAL_ZERO};
		}
		
	}
	public void compute(CompEnv<Boolean> env, AVLTree<BoolArray, BoolArray> ostack,
			IntegerLib<Boolean> lib, int logN) throws Exception {

		for (int i = 0; i < op.length; ++i) {
			BoolArray k = new BoolArray(env, lib);
			k.data = lib.toSignals(10, logN);
			BoolArray v = new BoolArray(env, lib);
			v.data = lib.toSignals(10, 32);
			ostack.insert(k, v, new cmpp(env, lib, new BoolArray(env, lib)));
		}
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
				AVLNode<BoolArray,BoolArray> node = new AVLNode<BoolArray,BoolArray>(
						env, lib, logN, new BoolArray(env, lib), new BoolArray(env, lib));
				IntStackNode intstacknode = new IntStackNode(env, lib, logN); 
				IntStack intstack = new IntStack(env, lib, logN, new CircuitOram<Boolean>(env, 1 << logN, intstacknode.numBits()));
				AVLTree<BoolArray, BoolArray> ostack = new AVLTree<BoolArray, BoolArray>(
						env,
						lib,
						logN, new BoolArray(env, lib), new BoolArray(env, lib),
						new CircuitOram<Boolean>(env, 1 << logN, node.numBits()), intstack);
				if (m == Mode.COUNT) {
					sta = ((PMCompEnv) (env)).statistic;
					sta.flush();
				}
				compute(env, ostack, lib, logN);
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
				CompEnv<Boolean> env = CompEnv.getEnv(m, Party.Bob, is, os);
				IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
				AVLNode<BoolArray,BoolArray> node = new AVLNode<BoolArray,BoolArray>(
						env, lib, logN, new BoolArray(env, lib), new BoolArray(env, lib));
				IntStackNode intstacknode = new IntStackNode(env, lib, logN); 
				IntStack intstack = new IntStack(env, lib, logN, new CircuitOram<Boolean>(env, 1 << logN, intstacknode.numBits()));
				AVLTree<BoolArray, BoolArray> ostack = new AVLTree<BoolArray, BoolArray>(
						env,
						lib,
						logN, new BoolArray(env, lib), new BoolArray(env, lib),
						new CircuitOram<Boolean>(env, 1 << logN, node.numBits()), intstack);
				
				compute(env, ostack, lib, logN);
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
		TestAVL a = new TestAVL();
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