package compiledlib.avltree;

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

		FUNC_0_INTsecure_RECORDBoolArray_RECORDBoolArrayImpl cmp;
		
		public cmpp(CompEnv<java.lang.Boolean> env,
				BoolArray factoryK, 
				FUNC_0_INTsecure_RECORDBoolArray_RECORDBoolArrayImpl cmp)
				throws Exception {
			super(env, factoryK);
			this.cmp = cmp;
		}

		@Override
		public java.lang.Boolean[] calc(BoolArray x0, BoolArray x1)
				throws Exception {
			return cmp.calc(x0, x1);
		}
		
	}
	
	public void compute(CompEnv<Boolean> env, AVLTree<BoolArray, BoolArray> ostack,
			int logN) throws Exception {
		ostack.init();
//		System.out.println("initialization finished.");
		
		cmpp cmp = new cmpp(env, new BoolArray(env),
				new FUNC_0_INTsecure_RECORDBoolArray_RECORDBoolArrayImpl(env)
				);
		IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
		if(m == Mode.COUNT) {
			((PMCompEnv) (env)).statistic.flush();
		}
		for (int i = 0; i < op.length; ++i) {
			BoolArray k = new BoolArray(env);
			k.data = lib.toSignals(i, 32);
			BoolArray v = new BoolArray(env);
			v.data = lib.toSignals(i, 32);
//			System.out.println("inserting "+Utils.toInt(env.outputToAlice(k.data))+"\t"+Utils.toInt(env.outputToAlice(v.data)));
			ostack.insert(k, v, cmp);
			k.data = lib.toSignals(i-2, 32);
//			BoolArray ret = ostack.search(k, cmp);
//			System.out.println(Utils.toInt(env.outputToAlice(ret.data)));
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
				CompEnv<Boolean> env = CompEnv.getEnv(m, Party.Alice, is, os);
//				IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
				BoolArray ba = new BoolArray(env);
				ba.data = (Boolean[]) env.inputOfAlice(Utils.fromInt(0, 32));
				AVLNode<BoolArray,BoolArray> node = new AVLNode<BoolArray,BoolArray>(
						env, logN, ba, ba);
				IntStackNode intstacknode = new IntStackNode(env, logN); 
				IntStack intstack = new IntStack(env, logN, new CircuitOram<Boolean>(env, 1 << logN, intstacknode.numBits()));
				AVLTree<BoolArray, BoolArray> ostack = new AVLTree<BoolArray, BoolArray>(
						env,
//						lib,
						logN, ba, ba,
						new CircuitOram<Boolean>(env, 1 << logN, node.numBits()));
				if (m == Mode.COUNT) {
					sta = ((PMCompEnv) (env)).statistic;
					sta.flush();
				}
				compute(env, ostack, logN);
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
				BoolArray ba = new BoolArray(env);
				ba.data = (Boolean[]) env.inputOfAlice(Utils.fromInt(0, 32));
				AVLNode<BoolArray,BoolArray> node = new AVLNode<BoolArray,BoolArray>(
						env, logN, ba, ba);
				IntStackNode intstacknode = new IntStackNode(env, logN); 
				IntStack intstack = new IntStack(env, logN, new CircuitOram<Boolean>(env, 1 << logN, intstacknode.numBits()));
				AVLTree<BoolArray, BoolArray> ostack = new AVLTree<BoolArray, BoolArray>(
						env,
//						lib,
						logN, ba, ba,
						new CircuitOram<Boolean>(env, 1 << logN, node.numBits()));
				
				compute(env, ostack, logN);
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
		GenRunnable gen = new GenRunnable(5);
		EvaRunnable eva = new EvaRunnable(5);
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

	public static void main(String[] args) throws Exception {
		new TestAVL().runThreads();
//		int logN = 5;
//		TestAVL a = new TestAVL();
//		a.getInput(10);
//		a.m = Mode.REAL;
//		GenRunnable gen = a.new GenRunnable(logN);
//		EvaRunnable eva = a.new EvaRunnable(logN);
//		Thread tGen = new Thread(gen);
//		Thread tEva = new Thread(eva);
//		tGen.start();
//		Thread.sleep(5);
//		tEva.start();
//		tGen.join();
	}
}