package oakland;

import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.Party;
import harness.TestHarness;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import circuits.arithmetic.DenseMatrixLib;
import circuits.arithmetic.FloatLib;
import circuits.arithmetic.IntegerLib;


public class TestRidgeRegression extends TestHarness {
	public static int len = 32;
	public static  int offset = 15;
	public static  int VLength = 23;
	public static  int PLength = 8;
	public static  boolean testFixedPoint = true;

	public static  class Helper {
		double[][] a, b;

		public Helper(double[][] a, double[][] b) {
			this.b = b;
			this.a = a;
		}
	}

	static Random rng = new Random(123);

	public static double[][] randomMatrix(int n, int m) {
		double[][] d1 = new double[n][m];
		for (int k = 0; k < d1.length; ++k)
			for (int j = 0; j < d1[0].length; ++j)
				d1[k][j] = rng.nextInt(1000) / 100.0;
		return d1;
	}

	public void PrintMatrix(double[][] result) {
		System.out.print("[\n");
		for (int i = 0; i < result.length; ++i) {
			for (int j = 0; j < result[0].length; ++j)
				System.out.print(result[i][j] + " ");
			System.out.print(";\n");
		}
		System.out.print("]\n");
	}

	public static class GenRunnable<T> extends network.Server implements Runnable {
		Helper h;
		double[][] z;
		DenseMatrixLib<T> lib;

		GenRunnable(Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> gen = CompEnv.getEnv(m, Party.Alice, is, os);

				// PrintMatrix(h.a);
				if (testFixedPoint)
					lib = new DenseMatrixLib<T>(gen, new FloatLib<T>(gen,VLength,PLength));//
//							len, offset));
				else
					lib = new DenseMatrixLib<T>(gen, new IntegerLib<T>(gen,
							32));

				T[][][] fgc1 = gen.newTArray(h.a.length, h.a[0].length, 1);
				T[][][] fgc2 = gen.newTArray(h.b.length, h.b[0].length, 1);
				T[][][] re = null;
				for(int tt = 0; tt < 5; ++tt) {
				for (int i = 0; i < h.a.length; ++i)
					for (int j = 0; j < h.a[0].length; ++j) 
							fgc1[i][j] = lib.lib.inputOfAlice(h.a[i][j]);
					
				for (int i = 0; i < h.b.length; ++i)
					for (int j = 0; j < h.b[0].length; ++j)
							fgc2[i][j] = lib.lib.inputOfBob(h.b[i][j]);
					


				if(m == Mode.VERIFY)
					re = lib.rref(fgc1);
				else  re = lib.rref(lib.xor(fgc1, fgc2));
				}
				
				z = new double[re.length][re[0].length];
				for (int i = 0; i < re.length; ++i)
					for (int j = 0; j < re[0].length; ++j)
							z[i][j] = lib.lib.outputToAlice(re[i][j]);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static class EvaRunnable<T> extends network.Client implements Runnable {
		Helper h;
		DenseMatrixLib<T> lib;
		public double andgates;
		public double encs;

		EvaRunnable(Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				connect("localhost", 54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(m, Party.Bob, is, os);

				if (testFixedPoint)
					lib = new DenseMatrixLib<T>(env, new FloatLib<T>(env,VLength,PLength));////new FixedPointLib<T>(env,
//							len, offset));
				else
					lib = new DenseMatrixLib<T>(env, new IntegerLib<T>(env,
							32));


				T[][][] fgc1 = env.newTArray(h.a.length, h.a[0].length, 1);
				T[][][] fgc2 = env.newTArray(h.b.length, h.b[0].length, 1);
				
				double[] time = new double[5];
				T[][][] re = null;
				for(int tt = 0; tt < time.length; ++tt) {
				System.gc();
				double d1 = System.nanoTime();
				for (int i = 0; i < h.a.length; ++i)
					for (int j = 0; j < h.a[0].length; ++j)
							fgc1[i][j] = lib.lib.inputOfAlice(h.a[i][j]);
					
				for (int i = 0; i < h.b.length; ++i)
					for (int j = 0; j < h.b[0].length; ++j)
						fgc2[i][j] = lib.lib.inputOfBob(h.b[i][j]);

				if (m == Mode.COUNT) {
					((PMCompEnv) env).statistic.flush();
				}

				
				if(m == Mode.VERIFY)
					re = lib.rref(fgc1);
				else  re = lib.rref(lib.xor(fgc1, fgc2));
				
				time[tt] = (System.nanoTime()-d1)/1000000000.0;
				}
				
				if(m == Mode.REAL) {
					Arrays.sort(time);
					System.out.println(len+" "+time[time.length/2]);
				}
				
				
				if (m == Mode.COUNT) {
					((PMCompEnv) env).statistic.finalize();
					andgates = ((PMCompEnv) env).statistic.andGate;
					encs = ((PMCompEnv) env).statistic.NumEncAlice;
				}

				for (int i = 0; i < re.length; ++i)
					for (int j = 0; j < re[0].length; ++j)
						env.outputToAlice(re[i][j]);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static <T>void runThreads(Helper h) throws Exception {
		GenRunnable<T> gen = new GenRunnable<T>(h);
		EvaRunnable<T> env = new EvaRunnable<T>(h);

		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(env);
		tGen.start();
		Thread.sleep(1);
		tEva.start();
		tGen.join();

		double[][] result = DenseMatrixLib.rref(h.a);

		// PrintMatrix(result);
		// PrintMatrix(gen.z);
		if (m == Mode.COUNT) {
			System.out.println(env.andgates + " " + env.encs);
		} else if(m == Mode.VERIFY){

			for (int i = 0; i < result.length; ++i)
				for (int j = 0; j < result[0].length; ++j) {
					double error = 0;
					if (gen.z[i][j] != 0)
						error = Math.abs((result[i][j] - gen.z[i][j])
								/ gen.z[i][j]);
					else
						error = Math.abs((result[i][j] - gen.z[i][j]));

					if (error > 1E-3)
						System.out.print(error + " " + gen.z[i][j] + " "
								+ result[i][j] + "(" + i + "," + j + ")\n");
//					Assert.assertTrue(error < 1E-3);
				}
		}
	}
	@Test
	public void testAllCases() throws Exception {
		TestHarness.m = Mode.REAL;
		for (int i = 22; i < 40; i++) {
			TestRidgeRegression.len = i;
			double[][] d1 = TestRidgeRegression.randomMatrix(14, 14);
			double[][] d2 = TestRidgeRegression.randomMatrix(14, 14);

			TestRidgeRegression.runThreads(new Helper(d1, d2));
		}
	}
}