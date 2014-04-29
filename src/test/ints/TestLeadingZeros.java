package test.ints;

import java.util.Random;

import flexsc.CompEnv;
import gc.GCEva;
import gc.GCGen;
import gc.GCSignal;

import org.junit.Assert;
import org.junit.Test;

import test.Utils;
import circuits.IntegerLib;


public class TestLeadingZeros {
	public abstract class Helper {
		long intA;
		boolean[] a;
		Helper(long aa) {
			intA = aa;
			a = Utils.fromLong(aa, 64);
		}
		abstract GCSignal[] secureCompute(GCSignal[] Signala, CompEnv<GCSignal> e) throws Exception;
		abstract int plainCompute(long x);
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
				GCSignal[] a = new GCSignal[h.a.length];
				GCSignal[] d = null;

				GCGen gen = new GCGen(is, os);
				for (int i = 0; i < a.length; i++) 	
					a[i] = gen.inputOfBob(false);

				d = h.secureCompute(a, gen);
				os.flush();

				z = new boolean[d.length];

				for (int i = 0; i < d.length; i++)
					z[i] = gen.outputToAlice(d[i]);

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
				GCSignal[] a = new GCSignal[h.a.length];
				GCSignal[] d = null;

				GCEva eva = new GCEva(is, os);
				for(int i = 0; i < a.length; ++i)
					a[i] = eva.inputOfBob(h.a[i]);


				d = h.secureCompute(a, eva);
				for (int i = 0; i < d.length; i++) 
					eva.outputToAlice(d[i]);
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

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			runThreads(
				new Helper(rnd.nextLong()) {
					GCSignal[] secureCompute(GCSignal[] Signala, CompEnv<GCSignal> e) throws Exception {
						return new IntegerLib<GCSignal>(e).leadingZeros(Signala);
					}

					int plainCompute(long x) {
						int a = Integer.numberOfLeadingZeros((int)(x>>32));
						if(a == 32)
							a += Integer.numberOfLeadingZeros((int) (x));
						return a;
					}
				});
		}		
	}
}
