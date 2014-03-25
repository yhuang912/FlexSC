package test;

import java.util.Arrays;
import java.util.Random;
import gc.CompEnv;
import gc.GCEva;
import gc.GCGen;
import gc.Signal;
import org.junit.Assert;
import org.junit.Test;
import sort.BitonicSortLib;


public class TestSort {
	public abstract class Helper {
		int[] intA;
		boolean[][] a;
		Helper(int[] aa) {
			intA = aa;
			a = new boolean[aa.length][32];
			for(int i = 0; i < intA.length; ++i)
				a[i] = Utils.fromInt(aa[i], 32);
		}
		abstract Signal[][] secureCompute(Signal[][] Signala, CompEnv<Signal> e) throws Exception;
		abstract int[] plainCompute(int[] intA2);
	}

	class GenRunnable extends network.Server implements Runnable {
		boolean[][] z;
		Helper h;
		GenRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				listen(54321);
				
				Signal[][] a = new Signal[h.a.length][h.a[0].length];

				GCGen gen = new GCGen(is, os);
				for(int i = 0; i < a.length; ++i)
					a[i] = gen.inputOfEva(new boolean[32]);

				Signal[][] d = h.secureCompute(a, gen);
				os.flush();
				
				z = new boolean[d.length][d[0].length];
				for (int i = 0; i < d.length; i++)
					z[i] = gen.outputToGen(d[i]);
				os.flush();
				
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
				Signal[][] a = new Signal[h.a.length][h.a[0].length];

				GCEva eva = new GCEva(is, os);
				for(int i = 0; i < a.length; ++i)
					a[i] = eva.inputOfEva(h.a[i]);

				Signal[][] d = h.secureCompute(a, eva);
				
				for (int i = 0; i < d.length; i++)
					eva.outputToGen(d[i]);
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

		for(int i = 0; i < gen.z.length-1; ++i) {
			Assert.assertTrue(Utils.toInt(gen.z[i]) < Utils.toInt(gen.z[i+1]));
		}
	}
	
	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 1;

		for (int i = 0; i < testCases; i++) {
			int [] a = new int[900];
			for(int j = 0; j < a.length; ++j)
				a[j] = rnd.nextInt()%(1<<30);
			
			runThreads(new Helper(a) {
				Signal[][] secureCompute(Signal[][] Signala, CompEnv<Signal> e) throws Exception {
					BitonicSortLib lib =  new BitonicSortLib(e);
					lib.sort(Signala, BitonicSortLib.SIGNAL_ONE);
					return Signala;
				}
				
				@Override
				int[] plainCompute(int[] intA) {
					Arrays.sort(intA);
					return intA;
				}
			});
		}		
	}

}