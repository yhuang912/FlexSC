package test.harness;

import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;
import gc.GCEva;
import gc.GCGen;

import org.junit.Assert;

import cv.CVCompEnv;
import cv.MeasureCompEnv;
import test.Utils;


public class Test_1Input1Output<T>{
	public abstract class Helper {
		int intA;
		boolean[] a;
		Mode m;
		public Helper(int aa, Mode m) {
			this.m = m;
			intA = aa;
			a = Utils.fromInt(aa, 32);
		}
		
		public abstract T[] secureCompute(T[] Signala, CompEnv<T> e) throws Exception;
		public abstract int plainCompute(int x);
	}

	class GenRunnable extends network.Server implements Runnable {
		boolean[] z;
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
					gen = (CompEnv<T>) new MeasureCompEnv(is, os, Party.Alice);			
				
				T[] a = gen.inputOfEva(new boolean[32]);
				 
				T[] d = h.secureCompute(a, gen);
				os.flush();

				z = gen.outputToGen(d);

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
					eva = (CompEnv<T>) new MeasureCompEnv(is, os, Party.Bob);

				T[] a = eva.inputOfEva(h.a);
				T[] d = h.secureCompute(a, eva);
				
				eva.outputToGen(d);
				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void runThreads(Helper helper) throws Exception {
		GenRunnable gen = new GenRunnable(helper);
		EvaRunnable eva = new EvaRunnable(helper);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(5);
		tEva.start();
		tGen.join();

		Assert.assertEquals(helper.plainCompute(helper.intA), Utils.toInt(gen.z));
	}
}