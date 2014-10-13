package test.harness;

import flexsc.*;
import gc.halfANDs.GCEva;
import gc.halfANDs.GCGen;

import org.junit.Assert;

import pm.PMCompEnv;
import cv.CVCompEnv;
import test.Utils;


public class Test_2Input1Output<T> {
	
	public abstract class Helper {
		int intA, intB;
		boolean[] a;
		boolean[] b;
		Mode m;
		public Helper(int aa, int bb, Mode m) {
			this.m = m;
			intA = aa;
			intB = bb;

			a = Utils.fromInt(aa, 32);
			b = Utils.fromInt(bb, 32);
		}
		public abstract T[] secureCompute(T[] Signala, T[] Signalb, CompEnv<T> e) throws Exception;
		public abstract int plainCompute(int x, int y);
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
					gen = (CompEnv<T>) new PMCompEnv(is, os, Party.Alice);						
				
				T[] a = gen.inputOfAlice(h.a);
				T[] b = gen.inputOfBob(new boolean[32]);

				T[] d = h.secureCompute(a, b, gen);
				os.flush();

				z = gen.outputToAlice(d);

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

				
				T[] a = eva.inputOfAlice(new boolean[32]);
				T[] b = eva.inputOfBob(h.b);

				T[] d = h.secureCompute(a, b, eva);
				
				eva.outputToAlice(d);
				os.flush();

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
		Assert.assertEquals(h.plainCompute(h.intA, h.intB), Utils.toSignedInt(gen.z));
	}

	

}