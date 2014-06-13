package test.harness;

import ml.datastructure.Point;

import org.junit.Assert;

import com.sun.java.swing.plaf.windows.resources.windows;

import pm.PMCompEnv;
import test.Utils;
import cv.CVCompEnv;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.Party;
import gc.GCEva;
import gc.GCGen;

public class Test_NinputMoutputPoint<T> {
	public abstract class Helper {
		int dimension;
		int width;
		int[][] intA, intB;
		boolean[][][] a;
		Mode m;
		int numberOfPoints ;
		public Helper(int[][] aa, Mode m, int dimension, int width) {
			this.dimension = dimension;
			this.width = width;
			this.m = m;
			intA = aa;
			numberOfPoints = aa.length;

			a = new boolean[numberOfPoints][dimension][width];
			for(int k = 0; k < numberOfPoints; ++k)
				for (int i = 0; i < dimension; i++) {
						a[k][i] = Utils.fromInt(
								aa[k][i], width);
				}
		}

		public abstract Point<T>[] secureCompute(Point<T>[] Signala, CompEnv<T> e) throws Exception;
		public abstract int[][] plainCompute(int[][] x);
	}

	class GenRunnable extends network.Server implements Runnable {
		boolean[][][] z;
		Helper h;
		GenRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				listen(54321);


				CompEnv<T> gen = null;
				if(h.m == Mode.REAL)
					gen = (CompEnv<T>) new GCGen(is, os);
				else if(h.m == Mode.VERIFY)
					gen = (CompEnv<T>) new CVCompEnv(is, os, Party.Alice);
				else if(h.m == Mode.COUNT) 
					gen = (CompEnv<T>) new PMCompEnv(is, os, Party.Alice);						


				Point<T>[] a = new Point[h.numberOfPoints];
				for(int i = 0; i < a.length; ++i)
					a[i] = new Point<>(gen, h.dimension, h.width, false);
				for(int i = 0; i < h.numberOfPoints; ++i) {
					for (int k = 0; k < h.dimension; k++) {
						a[i].coordinates[k] = gen.inputOfAlice(h.a[i][k]);
					}					
				}

				Point<T>[] res = h.secureCompute(a, gen);
					
				GCGen g = (GCGen)gen;
				System.out.println(Flag.sw.ands);
				z = new boolean[res.length][h.dimension][];
				for(int i = 0; i < res.length; ++i) {
					System.out.print("(");
					for(int k = 0; k < h.dimension; ++k){
						T[] temp = res[i].coordinates[k];
						z[i][k] = gen.outputToAlice(temp);	
						System.out.print(Utils.toInt(z[i][k])+",");
					}
					boolean isdummy = gen.outputToAlice(res[i].isDummy);
					System.out.print(isdummy+")\n");
				}
				os.flush();

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

				CompEnv<T> eva = null;

				if(h.m == Mode.REAL)
					eva = (CompEnv<T>) new GCEva(is, os);
				else if(h.m == Mode.VERIFY)
					eva = (CompEnv<T>) new CVCompEnv(is ,os, Party.Bob);
				else if (h.m == Mode.COUNT) 
					eva = (CompEnv<T>) new PMCompEnv(is, os, Party.Bob);

				Point<T>[] a = new Point[h.numberOfPoints];
				for(int i = 0; i < a.length; ++i)
					a[i] = new Point<>(eva, h.dimension, h.width, false);
				for(int i = 0; i < h.numberOfPoints; ++i) {
					for (int k = 0; k < h.dimension; k++) {
						a[i].coordinates[k] = eva.inputOfAlice(new boolean[h.width]);
					}				
				}				


				Point<T>[] res = h.secureCompute(a, eva);

				for(int i = 0; i < res.length; ++i) {
					for(int k = 0; k < h.dimension; ++k){
						eva.outputToAlice(res[i].coordinates[k]);

					}
					eva.outputToAlice(res[i].isDummy);
				}
				os.flush();
				printStatistic();
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
//		long[] z = new long[gen.z.length];
//		for (int i = 0; i < gen.z.length; i++) {
//			z[i] = Utils.toSignedInt(gen.z[i]);
//		}
//		int[] temp = h.plainCompute(h.intA, h.intB);
//		for (int i = 0; i < h.intA.length; i++) {
//			Assert.assertEquals(temp[i], z[i]);
//		}
	}

}
