package test.harness;


import objects.Float.Representation;
import flexsc.CompEnv;
import flexsc.Signal;
import gc.GCEva;
import gc.GCGen;
import gc.GCSignal;

import org.junit.Assert;


public class TestFloat {
	public abstract class Helper {
		double a,b;
		public Helper(double a, double b) {
			this.b = b;
			this.a = a;
		}
		public abstract Representation<GCSignal> secureCompute(Representation<GCSignal> a, Representation<GCSignal> b, CompEnv<GCSignal> env) throws Exception;
		public abstract double plainCompute(double a, double b);
	}
	
	class GenRunnable extends network.Server implements Runnable {
		Helper h;
		double z;

		GenRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				listen(54321);

				GCGen gen = new GCGen(is, os);
				Representation<GCSignal> fgc1 = gen.inputOfGen(h.a, 23, 9);
				Representation<GCSignal> fgc2 = gen.inputOfEva(23, 9);
				Representation<GCSignal> re = h.secureCompute(fgc1, fgc2, gen);
									
				z = gen.outputToGen(re);

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

				GCEva eva = new GCEva(is, os);
				Representation<GCSignal> fgc1 = eva.inputOfGen(23, 9);
				Representation<GCSignal> fgc2 = eva.inputOfEva(h.b, 23, 9);
				Representation<GCSignal> re = h.secureCompute(fgc1, fgc2, eva);
									
				eva.outputToGen(re);
				
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

		if(Math.abs(h.plainCompute(h.a, h.b)-gen.z)>3E-6)
			System.out.print(gen.z+" "+h.plainCompute(h.a, h.b)+" "+h.a+" "+h.b+"\n");
		Assert.assertTrue(Math.abs(h.plainCompute(h.a, h.b)-gen.z)<=3E-6);
	}
}