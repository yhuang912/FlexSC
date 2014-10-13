package test.harness;

import flexsc.*;
import gc.halfANDs.GCEva;
import gc.halfANDs.GCGen;

import org.junit.Assert;

import pm.PMCompEnv;
import cv.CVCompEnv;


public class TestFixedPointMatrix<T> {
	final int len = 32;
	final int offset = 20;
	public abstract class Helper {
		Mode m;
		double[][] a,b;
		public Helper(double[][] a, double[][] b, Mode m) {
			this.m = m;
			this.b = b;
			this.a = a;
		}
		public abstract T[][][] secureCompute(T[][][] a, T[][][] b, int offset, CompEnv<T> env) throws Exception;
		public abstract double[][] plainCompute(double[][] a, double[][] b);
	}
	
	class GenRunnable extends network.Server implements Runnable {
		Helper h;
		double[][] z;

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

				T[][][] fgc1 = gen.newTArray(h.a.length, h.a[0].length, len);
				T[][][] fgc2 = gen.newTArray(h.b.length, h.b[0].length, len);
				for(int i = 0; i < h.a.length; ++i)
					for(int j = 0; j < h.a[0].length; ++j){
						fgc1[i][j] = gen.inputOfAliceFixedPoint(h.a[i][j], len, offset);
					}
				for(int i = 0; i < h.b.length; ++i)
					for(int j = 0; j < h.b[0].length; ++j){
						fgc2[i][j] = gen.inputOfAliceFixedPoint(h.b[i][j], len, offset);
					}
				
				T[][][] re = h.secureCompute(fgc1, fgc2, offset, gen);
				z = new double[re.length][re[0].length];
				for(int i = 0; i < re.length; ++i)
					for(int j = 0; j < re[0].length; ++j)
						z[i][j] = gen.outputToAliceFixedPoint(re[i][j], offset);

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
					eva = (CompEnv<T>) new CVCompEnv(is, os, Party.Bob);
				else if(h.m == Mode.COUNT)
					eva = (CompEnv<T>) new PMCompEnv(is, os, Party.Bob);

				
				T[][][] fgc1 = eva.newTArray(h.a.length, h.a[0].length, len);
				T[][][] fgc2 = eva.newTArray(h.b.length, h.b[0].length, len);
				for(int i = 0; i < h.a.length; ++i)
					for(int j = 0; j < h.a[0].length; ++j){
						fgc1[i][j] = eva.inputOfAliceFixedPoint(h.a[i][j], len, offset);
					}
				for(int i = 0; i < h.b.length; ++i)
					for(int j = 0; j < h.b[0].length; ++j){
						fgc2[i][j] = eva.inputOfAliceFixedPoint(h.b[i][j], len, offset);
					}
				
				T[][][] re = h.secureCompute(fgc1, fgc2, offset, eva);
				for(int i = 0; i < re.length; ++i)
					for(int j = 0; j < re[0].length; ++j)
						eva.outputToAliceFixedPoint(re[i][j], offset);

				
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
		tGen.start(); Thread.sleep(1);
		tEva.start();
		tGen.join();

		double[][] result = h.plainCompute(h.a, h.b);
		
		for(int i = 0; i < result.length; ++i)
			for(int j = 0; j < result[0].length; ++j){
				if(Math.abs(result[i][j]-gen.z[i][j])>1E-5)
					System.out.print(Math.abs(result[i][j]-gen.z[i][j])+" "+gen.z[i][j]+" "+result[i][j]+"\n");
				Assert.assertTrue(Math.abs(result[i][j]-gen.z[i][j])<=1E-5);
		}
	}
}