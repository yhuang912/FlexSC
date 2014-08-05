package test.harness;

import org.junit.Assert;

import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;


public class TestFixedPoint<T> {
	final int len = 32;
	final int offset = 20;
	public abstract class Helper {
		Mode m;
		double a,b;
		public Helper(double a, double b, Mode m) {
			this.m = m;
			this.b = b;
			this.a = a;
		}
		public abstract T[] secureCompute(T[] a, T[] b, int offset, CompEnv<T> env) throws Exception;
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
				@SuppressWarnings("unchecked")
				CompEnv<T> gen = CompEnv.getEnv(h.m, Party.Alice, is, os);
				
				T[] fgc1 = gen.inputOfAliceFixedPoint(h.a, len, offset);
				T[] fgc2 = gen.inputOfBobFixedPoint(0, len, offset);
				T[] re = h.secureCompute(fgc1, fgc2, offset, gen);
									
				z = gen.outputToAliceFixedPoint(re, offset);

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
				
				T[] fgc1 = eva.inputOfAliceFixedPoint(0, len, offset);
				T[] fgc2 = eva.inputOfBobFixedPoint(h.b, len, offset);
				T[] re = h.secureCompute(fgc1, fgc2, offset, eva);
									
				eva.outputToAliceFixedPoint(re, offset);

				
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void runThreads(Helper h) throws InterruptedException {
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