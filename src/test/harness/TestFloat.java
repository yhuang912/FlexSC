package test.harness;

import objects.Float.Representation;

import org.junit.Assert;

import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;


public class TestFloat<T> {
	public abstract class Helper {
		double a,b;
		Mode m;
		public Helper(double a, double b, Mode m) {
			this.m = m;
			this.b = b;
			this.a = a;
		}
		public abstract Representation<T> secureCompute(Representation<T> a, Representation<T> b, CompEnv<T> env) throws Exception;
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
				
				Representation<T> fgc1 = (Representation<T>) gen.inputOfAliceFloatPoint(h.a, 23, 9);
				Representation<T> fgc2 = (Representation<T>) gen.inputOfBobFloatPoint(0, 23, 9);
				Representation<T> re = h.secureCompute(fgc1, fgc2, gen);
									
				z = gen.outputToAliceFloatPoint(re);

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
				
				Representation<T> fgc1 = eva.inputOfAliceFloatPoint(0, 23, 9);
				Representation<T> fgc2 = eva.inputOfBobFloatPoint(h.b, 23, 9);
				Representation<T> re = h.secureCompute(fgc1, fgc2, eva);
									
				eva.outputToAliceFloatPoint(re);
				
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

		if(Math.abs(h.plainCompute(h.a, h.b)-gen.z)>3E-6)
			System.out.print(gen.z+" "+h.plainCompute(h.a, h.b)+" "+h.a+" "+h.b+"\n");
		Assert.assertTrue(Math.abs(h.plainCompute(h.a, h.b)-gen.z)<=3E-6);
	}
}