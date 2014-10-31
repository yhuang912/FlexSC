package compiledlib.sketch;

import java.security.SecureRandom;

import oram.SecureArray;

import org.junit.Test;

import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;

public class testSketch {

	public Mode m;
	java.util.Stack<Integer> cstack = new java.util.Stack<Integer>();
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

	public void compute(CompEnv<Boolean> env,
			IntegerLib<Boolean> lib) throws Exception {
		int delta = 5;
		SecureArray[] a = new SecureArray[delta];
		for(int i = 0; i < delta; ++i)
			a[i] = new SecureArray<Boolean>(env, 100, 64);
		Boolean[][][] hash_seed = new Boolean[delta][6][64];
//		ams_sketch s = new ams_sketch(env, lib, null, hash_seed , a);
		count_min_sketch s = new count_min_sketch(env, lib, null, hash_seed , a);
//		count_sketch s = new count_sketch(env, lib, null, hash_seed , a);
		s.init();
		if (m == Mode.COUNT) {
			((PMCompEnv) (env)).statistic.flush();
		}
		s.insert(lib.toSignals(1, 64), lib.toSignals(1, 64));

	}

	class GenRunnable extends network.Server implements Runnable {
		Boolean[] z;
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
				if (m == Mode.COUNT) {
					sta = ((PMCompEnv) (env)).statistic;
					sta.flush();
				}

				compute(env, lib);
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
				CompEnv env = CompEnv.getEnv(m, Party.Bob, is, os);
				IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
				compute(env, lib);
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
		m = Mode.COUNT;
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
}
