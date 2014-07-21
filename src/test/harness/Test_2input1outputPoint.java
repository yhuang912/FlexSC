package test.harness;

import ml.datastructure.Point;

import org.junit.Assert;

import test.Utils;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;

public class Test_2input1outputPoint<T> {
	public abstract class Helper {
		int dimension;
		int width;
		int[] intA, intB;
		boolean[][] a;
		boolean[][] b;
		Mode m;

		public Helper(int[] aa, int[] bb, Mode m, int dimension, int width) {
			this.dimension = dimension;
			this.width = width;
			this.m = m;
			intA = aa;
			intB = bb;

			a = new boolean[dimension][width];
			b = new boolean[dimension][width];
			for (int i = 0; i < dimension; i++) {
				a[i] = Utils.fromInt(aa[i], 32);
				b[i] = Utils.fromInt(bb[i], 32);
			}
		}

		public abstract Point<T> secureCompute(Point<T> Signala, Point<T> Signalb, CompEnv<T> e) throws Exception;
		public abstract int[] plainCompute(int[] x, int[] y);
	}

	class GenRunnable extends network.Server implements Runnable {
		boolean[][] z;
		Helper h;
		GenRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> gen = CompEnv.getEnv(h.m, Party.Alice, is, os);				
				
				Point<T> a = new Point<T>(gen, 2, false);
				Point<T> b = new Point<T>(gen, 2, false);
				for (int i = 0; i < a.getDimension(); i++) {
					a.coordinates[i] = gen.inputOfAlice(h.a[i]);
					b.coordinates[i] = gen.inputOfBob(new boolean[32]);
				}

				Point<T> res = h.secureCompute(a, b, gen);
				os.flush();

				z = new boolean[a.getDimension()][];
				for (int i = 0; i < a.getDimension(); i++) {
					z[i] = gen.outputToAlice(res.coordinates[i]);
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
		EvaRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				connect("localhost", 54321);				
				@SuppressWarnings("unchecked")
				CompEnv<T> eva = CompEnv.getEnv(h.m, Party.Bob, is, os);
				
				Point<T> a = new Point<T>(eva, 2, false);
				Point<T> b = new Point<T>(eva, 2, false);
				for (int i = 0; i < a.getDimension(); i++) {
					a.coordinates[i] = eva.inputOfAlice(new boolean[32]);
					b.coordinates[i] = eva.inputOfBob(h.b[i]);
				}

				Point<T> res = h.secureCompute(a, b, eva);
				os.flush();

				// Point<Boolean> ungarbledRes = new Point<Boolean>(eva, 2);
				boolean[][] ungarbledRes = new boolean[2][30];
				for (int i = 0; i < a.getDimension(); i++) {
					ungarbledRes[i] = eva.outputToAlice(res.coordinates[i]);
					System.out.println(Utils.toInt(ungarbledRes[i]));
				}

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void runThreads(Helper h) throws Exception {
		GenRunnable gen = new GenRunnable(h);
		EvaRunnable eva = new EvaRunnable(h);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();

		//System.out.println(Arrays.toString(gen.z));
		long[] z = new long[gen.z.length];
		for (int i = 0; i < gen.z.length; i++) {
			z[i] = Utils.toSignedInt(gen.z[i]);
		}
		int[] temp = h.plainCompute(h.intA, h.intB);
		for (int i = 0; i < h.intA.length; i++) {
			Assert.assertEquals(temp[i], z[i]);
		}
	}

}
