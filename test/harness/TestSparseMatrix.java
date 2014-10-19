package harness;

import java.util.Random;

import org.junit.Assert;

import circuits.arithmetic.DenseMatrixLib;
import circuits.arithmetic.FixedPointLib;
import circuits.arithmetic.FloatLib;
import circuits.sparse_matrix.MatrixNode;
import circuits.sparse_matrix.SparseMatrixLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.Party;

public class TestSparseMatrix<T> extends TestHarness {
	public final int len = 64;
	public final int offset = 30;
	public final int VLength = 24;
	public final int PLength = 8;
	public final boolean testFixedPoint = false;
	public static int xlimit = 50;
	public static int ylimit = 50;

	public static int M = 50;

	public abstract class Helper {
		int xa[];
		int xb[];
		int ya[];
		int yb[];
		double va[];
		double vb[];

		double[][] a, b;
		int acnt = 0;
		int bcnt = 0;

		public Helper(double[][] a, double[][] b) {
			this.a = a;
			this.b = b;

			xa = new int[M];
			ya = new int[M];
			xb = new int[M];
			yb = new int[M];
			va = new double[M];
			vb = new double[M];
			for (int i = 0; i < a.length; ++i) {
				for (int j = 0; j < a[0].length; ++j)
					if (Math.abs(a[i][j] - 1e-6) > 1e-6) {
						xa[acnt] = i;
						ya[acnt] = j;
						va[acnt] = a[i][j];
						acnt++;
					} else
						va[acnt] = 0;
			}

			for (int i = 0; i < b.length; ++i) {
				for (int j = 0; j < b[0].length; ++j)
					if (Math.abs(b[i][j] - 1e-6) > 1e-6) {
						xb[bcnt] = i;
						yb[bcnt] = j;
						vb[bcnt] = b[i][j];
						bcnt++;
					} else
						vb[bcnt] = 0;
			}
		}

		public abstract MatrixNode<T>[] secureCompute(MatrixNode<T>[] a,
				MatrixNode<T>[] b, SparseMatrixLib<T> lib) throws Exception;

		public abstract double[][] plainCompute(double[][] a, double[][] b);
	}

	static Random rng = new Random(123);

	public double[][] randomMatrix(int n, int m, double sparseness) {
		double[][] d1 = new double[n][m];
		for (int k = 0; k < d1.length; ++k)
			for (int j = 0; j < d1[0].length; ++j)
				if (rng.nextDouble() < sparseness)
					d1[k][j] = rng.nextInt() * 10000 % 1000.0;
				else
					d1[k][j] = 0;
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

	class GenRunnable extends network.Server implements Runnable {
		Helper h;
		double[][] z;

		GenRunnable(Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> gen = CompEnv.getEnv(m, Party.Alice, is, os);

				SparseMatrixLib<T> slib;
				if (testFixedPoint)
					slib = new SparseMatrixLib<T>(new FixedPointLib<T>(gen,
							len, offset));
				else
					slib = new SparseMatrixLib<T>(new FloatLib<T>(gen, VLength,
							PLength));

				// PrintMatrix(h.a);
				// PrintMatrix(h.b);

				MatrixNode<T>[] fgc1 = new MatrixNode[h.va.length];
				MatrixNode<T>[] fgc2 = new MatrixNode[h.vb.length];
				int cnt1 = 0;
				int cnt2 = 0;
				for (int i = 0; i < h.va.length; ++i) {
					if (i < h.acnt)
						fgc1[cnt1++] = slib.inputOfAlice(h.xa[i], h.ya[i],
								h.va[i], false);
					else
						fgc1[cnt1++] = slib.inputOfAlice(h.xa[i], h.ya[i],
								h.va[i], true);
				}

				for (int i = 0; i < h.vb.length; ++i)
					if (i < h.bcnt)
						fgc2[cnt2++] = slib.inputOfAlice(h.xb[i], h.yb[i],
								h.vb[i], false);
					else
						fgc2[cnt2++] = slib.inputOfAlice(h.xa[i], h.ya[i],
								h.va[i], true);

				// MatrixNode<T>[] re = h.secureCompute(Arrays.copyOf(fgc1,
				// h.acnt), Arrays.copyOf(fgc2, h.bcnt), slib);
				MatrixNode<T>[] re = h.secureCompute(fgc1, fgc2, slib);
				// System.out.println(re.length);
				z = new double[h.a.length][h.a[0].length];
				for (int i = 0; i < re.length; ++i) {
					double[] res = slib.outputToAlice(re[i]);
					if (res[3] == 1) {
						System.out
								.println(res[0] + " " + res[1] + " " + res[2]);
						z[(int) res[0]][(int) res[1]] = res[2];
					}
					// else res[3] = 0;
				}

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
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

				SparseMatrixLib<T> slib;
				if (testFixedPoint)
					slib = new SparseMatrixLib<T>(new FixedPointLib<T>(env,
							len, offset));
				else
					slib = new SparseMatrixLib<T>(new FloatLib<T>(env, VLength,
							PLength));

				// PrintMatrix(h.a);

				MatrixNode<T>[] fgc1 = new MatrixNode[h.va.length];
				MatrixNode<T>[] fgc2 = new MatrixNode[h.vb.length];
				int cnt1 = 0;
				int cnt2 = 0;
				for (int i = 0; i < h.va.length; ++i) {
					if (i < h.acnt)
						fgc1[cnt1++] = slib.inputOfAlice(h.xa[i], h.ya[i],
								h.va[i], true);
					else
						fgc1[cnt1++] = slib.inputOfAlice(h.xa[i], h.ya[i],
								h.va[i], false);
				}

				for (int i = 0; i < h.vb.length; ++i)
					if (i < h.bcnt)
						fgc2[cnt2++] = slib.inputOfAlice(h.xb[i], h.yb[i],
								h.vb[i], true);
					else
						fgc2[cnt2++] = slib.inputOfAlice(h.xa[i], h.ya[i],
								h.va[i], false);

				if (m == Mode.COUNT) {
					((PMCompEnv) env).statistic.flush();
				}
				// MatrixNode<T>[] re = h.secureCompute(Arrays.copyOf(fgc1,
				// h.acnt), Arrays.copyOf(fgc2, h.bcnt), slib);
				MatrixNode<T>[] re = h.secureCompute(fgc1, fgc2, slib);
				if (m == Mode.COUNT) {
					((PMCompEnv) env).statistic.finalize();
					andgates = ((PMCompEnv) env).statistic.andGate;
					encs = ((PMCompEnv) env).statistic.NumEncAlice;
				}

				for (int i = 0; i < re.length; ++i)
					slib.outputToAlice(re[i]);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void runThreads(Helper h) throws Exception {
		GenRunnable gen = new GenRunnable(h);
		EvaRunnable env = new EvaRunnable(h);

		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(env);
		tGen.start();
		Thread.sleep(1);
		tEva.start();
		tGen.join();

		double[][] result = h.plainCompute(h.a, h.b);

		PrintMatrix(h.a);
		PrintMatrix(gen.z);
		if (m == Mode.COUNT) {
			System.out.println(env.andgates + " " + env.encs);
		} else {

			for (int i = 0; i < result.length; ++i)
				for (int j = 0; j < result[0].length; ++j) {
					double error = 0;
					if (gen.z[i][j] != 0)
						error = Math.abs((result[i][j] - gen.z[i][j])
								/ gen.z[i][j]);
					else
						error = Math.abs((result[i][j] - gen.z[i][j]));

					if (error > 1E-4)
						System.out.print(error + " " + gen.z[i][j] + " "
								+ result[i][j] + "(" + i + "," + j + ")\n");
					Assert.assertTrue(error < 1E-4);
				}
		}
	}
}