package arithcircuit;

import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.Party;
import harness.TestHarness;

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Test;

import util.Utils;
import circuits.arithmetic.IntegerLib;



public class TestBigInteger extends TestHarness {
	public static int LENGTH = 256;

	public static <T>void secureCompute(T[] a, CompEnv<T> gen, int dim) {
		IntegerLib<T> lib = new IntegerLib<T>(gen);
//		a = lib.add(a, a);
		lib.multiply(a, a);
	}

	public static class GenRunnable<T> extends network.Server implements Runnable {

		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> gen = CompEnv.getEnv(m, Party.Alice, is, os);

				T [] a = gen.inputOfAlice(Utils.fromBigInteger(BigInteger.ONE, LENGTH));

				double[] t1 = new double[TestABB.times];
				for(int i = 0; i < t1.length; ++i) {
					t1[i] = System.nanoTime();
					secureCompute(a, gen, 1);
					t1[i] = System.nanoTime() - t1[i];
				}
				Arrays.sort(t1);
				System.out.println(t1[t1.length/2]/1000000000.0);

				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static class EvaRunnable<T> extends network.Client implements Runnable {

		public double andgates;
		public double encs;


		public void run() {
			try {
				connect("localhost", 54321);				
				@SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(m, Party.Bob, is, os);

				T [] a = env.inputOfAlice(new boolean[LENGTH]);
				if (m == Mode.COUNT) {
					((PMCompEnv) env).statistic.flush();
				}

				for(int i = 0; i < TestABB.times; ++i)
					secureCompute(a, env, 1);
				if (m == Mode.COUNT) {
					((PMCompEnv) env).statistic.finalize();
					andgates = ((PMCompEnv) env).statistic.andGate;
					encs = ((PMCompEnv) env).statistic.NumEncAlice;
				}

				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}


	@Test
	public void runThreads() throws Exception {
		for(int i = 1; i <= 10; ++i) {
			LENGTH = 1<<i;
			m = Mode.REAL;
			GenRunnable gen = new GenRunnable();
			EvaRunnable eva = new EvaRunnable();

			Thread tGen = new Thread(gen);
			Thread tEva = new Thread(eva);
			tGen.start(); Thread.sleep(5);
			tEva.start();
			tGen.join();
			if(m == Mode.COUNT)
				System.out.println((1<<i) + " "+eva.andgates);
		}
	}
}