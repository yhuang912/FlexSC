package test.DFT;


import java.util.Arrays;
import java.util.Random;

import objects.Float.Representation;
import flexsc.CompEnv;
import gc.GCEva;
import gc.GCGen;
import gc.GCSignal;

import org.junit.Assert;
import org.junit.Test;

import circuits.DFTLib;


public class TestDFT {
	public abstract class Helper {
		double[] a,b;
		Helper(double[] a, double[] b) {
			this.b = b;
			this.a = a;
		}
		abstract void secureCompute(Representation<GCSignal>[] a, Representation<GCSignal>[] b, CompEnv<GCSignal> env) throws Exception;
		abstract void plainCompute(double a[], double b[]);
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

				GCGen gen = new GCGen(is, os);
				Representation<GCSignal>[] fgc1 =new Representation<GCSignal>[h.a.length];
				Representation<GCSignal>[] fgc2 =new Representation<GCSignal>[h.b.length];
				
				for(int i = 0; i < fgc1.length; ++i)
					fgc1[i] = gen.inputOfGen(h.a[i], 23, 9);
				for(int i = 0; i < fgc2.length; ++i)
					fgc2[i] = gen.inputOfEva(23, 9);
				
				h.secureCompute(fgc1, fgc2, gen);
				os.flush();
				
				z1 = new double[fgc1.length];
				z2 = new double[fgc2.length];
				for(int i = 0; i < fgc1.length; ++i)
					z1[i] = gen.outputToGen(fgc1[i]);

				for(int i = 0; i < fgc2.length; ++i)
					z2[i] = gen.outputToGen(fgc2[i]);

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

				Representation<GCSignal>[] fgc1 = new Representation<GCSignal>[h.a.length];
				Representation<GCSignal>[] fgc2 = new Representation<GCSignal>[h.b.length];
				for(int i = 0; i < fgc1.length; ++i)
					fgc1[i] = eva.inputOfGen(23, 9);
				for(int i = 0; i < fgc2.length; ++i)
					fgc2[i] = eva.inputOfEva(h.b[i], 23, 9);
				
				h.secureCompute(fgc1, fgc2, eva);
				
				for(int i = 0; i < fgc1.length; ++i)
					eva.outputToGen(fgc1[i]);
				for(int i = 0; i < fgc2.length; ++i)
					eva.outputToGen(fgc2[i]);
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
	final int LENGTH = 8;
	@Test
	public void testAllCases() throws Exception {
		Random rng = new Random();
		
		double[] real = new double[LENGTH];
		double[] img = new double[LENGTH];
		for(int i = 0; i < LENGTH; ++i) {
			real[i] = rng.nextInt()%10000;
			img[i] = 0;
		}
		int testCases = 1;

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(real, img) {

				@Override
				void secureCompute(Representation<GCSignal>[] a, Representation<GCSignal>[] b,
						CompEnv<GCSignal> env) throws Exception {
					new DFTLib<GCSignal>(env).FFT(a, b);
				}

				@Override
				void plainCompute(double[] a, double[] b) {
					new DFT(LENGTH).fft(a, b);
				}
			});
		}
	}
}