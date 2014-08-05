package test.harness;

import java.util.Arrays;

import objects.Float.Representation;

import org.junit.Assert;

import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;

public class TestDFTHarness<T> {
	public abstract class Helper {
		double[] a,b;
		Mode m;
		public Helper(double[] a, double[] b, Mode m) {
			this.m = m;
			this.b = b;
			this.a = a;
		}
		public abstract void secureCompute(Representation<T>[] a, Representation<T>[] b, CompEnv<T> env) throws Exception;
		public abstract void plainCompute(double a[], double b[]);
	}
	
	class GenRunnable extends network.Server implements Runnable {
		Helper h;
		double[] z1, z2;

		GenRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> gen = CompEnv.getEnv(h.m, Party.Alice, is, os);				

				Representation<T>[] fgc1 = new Representation[h.a.length];
				Representation<T>[] fgc2 = new Representation[h.b.length];
				
				for(int i = 0; i < fgc1.length; ++i)
					fgc1[i] = gen.inputOfAliceFloatPoint(h.a[i], 23, 9);
				for(int i = 0; i < fgc2.length; ++i)
					fgc2[i] = gen.inputOfBobFloatPoint(0, 23, 9);
				
				h.secureCompute(fgc1, fgc2, gen);
				os.flush();
				
				z1 = new double[fgc1.length];
				z2 = new double[fgc2.length];
				for(int i = 0; i < fgc1.length; ++i)
					z1[i] = gen.outputToAliceFloatPoint(fgc1[i]);

				for(int i = 0; i < fgc2.length; ++i)
					z2[i] = gen.outputToAliceFloatPoint(fgc2[i]);

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

				Representation<T>[] fgc1 = new Representation[h.a.length];
				Representation<T>[] fgc2 = new Representation[h.b.length];
				for(int i = 0; i < fgc1.length; ++i)
					fgc1[i] = eva.inputOfAliceFloatPoint(0, 23, 9);
				for(int i = 0; i < fgc2.length; ++i)
					fgc2[i] = eva.inputOfBobFloatPoint(h.b[i], 23, 9);
				
				h.secureCompute(fgc1, fgc2, eva);
				
				for(int i = 0; i < fgc1.length; ++i)
					eva.outputToAliceFloatPoint(fgc1[i]);
				for(int i = 0; i < fgc2.length; ++i)
					eva.outputToAliceFloatPoint(fgc2[i]);
				os.flush();
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
		
		h.plainCompute(h.a, h.b);
		
		System.out.println("real secureCompute:"+Arrays.toString(gen.z1));
		System.out.println("real plainCompute :"+Arrays.toString(h.a));
		
		System.out.println("img secureCompute:"+Arrays.toString(gen.z2));
		System.out.println("img plainCompute :"+Arrays.toString(h.b));
		
		for(int i = 0; i < LENGTH; ++i)
			Assert.assertTrue(Math.abs(gen.z1[i] - h.a[i]) < 1);
		for(int i = 0; i < LENGTH; ++i)
			Assert.assertTrue(Math.abs(gen.z2[i] - h.b[i]) < 1);
	}
	protected final int LENGTH = 8;

}