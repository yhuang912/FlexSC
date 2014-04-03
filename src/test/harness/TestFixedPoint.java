package test.harness;


import flexsc.CompEnv;
import gc.GCEva;
import gc.GCGen;
import gc.Signal;
import org.junit.Assert;

import test.Utils;


public class TestFixedPoint {
	final int len = 32;
	final int offset = 20;
	public abstract class Helper {
		double a,b;
		public Helper(double a, double b) {
			this.b = b;
			this.a = a;
		}
		public abstract Signal[] secureCompute(Signal[] a, Signal[] b, int offset, CompEnv<Signal> env) throws Exception;
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
				Signal[] fgc1 = gen.inputOfGenFixPoint(h.a, len, offset);
				Signal[] fgc2 = gen.inputOfEvaFixPoint(len, offset);
				Signal[] re = h.secureCompute(fgc1, fgc2, offset, gen);
									
				boolean[] res = gen.outputToGen(re);
				z = Utils.toFixPoint(res, len, offset);

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
				Signal[] fgc1 = eva.inputOfGenFixPoint(len, offset);
				Signal[] fgc2 = eva.inputOfEvaFixPoint(h.b, len, offset);
				Signal[] re = h.secureCompute(fgc1, fgc2, offset, eva);
									
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

		
		if(Math.abs(h.plainCompute(h.a, h.b)-gen.z)>1E-5)
			System.out.print(Math.abs(h.plainCompute(h.a, h.b)-gen.z)+" "+gen.z+" "+h.plainCompute(h.a, h.b)+" "+h.a+" "+h.b+"\n");
		Assert.assertTrue(Math.abs(h.plainCompute(h.a, h.b)-gen.z)<1E-5);
	}
}