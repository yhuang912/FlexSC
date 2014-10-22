package oakland;

import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.Party;
import harness.TestHarness;

import java.util.Random;

import org.junit.Assert;

import circuits.arithmetic.IntegerLib;


public class triangularCounting extends TestHarness {

	static double[][] a;
	static int finalRes = 0;
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

	public static void getInput(int dim) {
		a = new double[dim][dim];
		for(int i = 0; i < dim; ++i)
			for(int j = 0; j < dim; ++j){
				if(rng.nextBoolean())
					a[i][j] = 1;
				else a[i][j] = 0;
			}

		for(int i = 0; i < dim; ++i)
			for(int j = 0; j < dim; ++j)
				for(int k = 0; k < dim; ++k)
					finalRes +=a[i][k]*a[k][j];
	}

	public static <T> T[] secureCompute(T[][]a, IntegerLib<T> lib) throws Exception {
		int dim = a.length;
		T[] res = lib.toSignals(0, 32);
		T[] counter = lib.env.newTArray(dim*dim);
		for(int i = 0; i < dim; ++i) {
			int c = 0;
			for(int j = 0; j < dim; ++j)
				for(int k = 0; k < dim; ++k)
					counter[c++]= lib.and(a[i][k], a[k][j]);
			res = lib.add(res, lib.padSignal(lib.numberOfOnes(counter), res.length));
		}

		return res;
	}

	public static class GenRunnable<T> extends network.Server implements Runnable {
		IntegerLib<T> lib;
		double z;

		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> gen = CompEnv.getEnv(m, Party.Alice, is, os);

				lib = new IntegerLib<T>(gen, 2);

				T[][] fgc1 = gen.newTArray(a.length, a[0].length);

				for (int i = 0; i < a.length; ++i){
					boolean[] tmp = new boolean[a.length];
					for (int j = 0; j < a[0].length; ++j)
						tmp[j] = a[i][j] == 1;
					fgc1[i] = lib.env.inputOfAlice(tmp);
				}
				T[] re = secureCompute(fgc1, lib);
				z = lib.outputToAlice(re);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static class EvaRunnable<T> extends network.Client implements Runnable {
		IntegerLib<T> lib;
		public double andgates;
		public double encs;

		public void run() {
			try {
				connect("localhost", 54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(m, Party.Bob, is, os);

				lib = new IntegerLib<T>(env, 2);


				T[][] fgc1 = env.newTArray(a.length, a[0].length);

				for (int i = 0; i < a.length; ++i){
					boolean[] tmp = new boolean[a.length];
					fgc1[i] = lib.env.inputOfAlice(tmp);
				}
				if (m == Mode.COUNT) {
					((PMCompEnv) env).statistic.flush();
				}

				double a = System.nanoTime();
				T[] res = secureCompute(fgc1, lib);
				System.out.println((System.nanoTime()-a)/1000000000);
				if (m == Mode.COUNT) {
					((PMCompEnv) env).statistic.finalize();
					andgates = ((PMCompEnv) env).statistic.andGate;
					encs = ((PMCompEnv) env).statistic.NumEncAlice;
				}

				env.outputToAlice(res);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static <T>void runThreads() throws Exception {
		GenRunnable<T> gen = new GenRunnable<T>();
		EvaRunnable<T> env = new EvaRunnable<T>();

		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(env);
		tGen.start();
		Thread.sleep(1);
		tEva.start();
		tGen.join();

		// PrintMatrix(result);
		// PrintMatrix(gen.z);
		if (m == Mode.COUNT) {
			System.out.println(env.andgates + " " + env.encs);
		} else {
			Assert.assertTrue( finalRes == gen.z);
//			System.out.print(gen.z+" "+finalRes);
		}
	}
	
	public  static void main(String args[]) throws Exception {
		getInput(new Integer(args[0]));
		triangularCounting.runThreads();
	}
}