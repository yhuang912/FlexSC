package test.harness;

import flexsc.CompEnv;
import gc.GCEva;
import gc.GCGen;
import gc.GCSignal;

import org.junit.Assert;

import test.Utils;


public class Test_1Input1Output {
	public abstract static class Helper {
		int intA;
		boolean[] a;
		public Helper(int aa) {
			intA = aa;
			a = Utils.fromInt(aa, 32);
		}
		public abstract GCSignal[] secureCompute(GCSignal[] Signala, CompEnv<GCSignal> e) throws Exception;
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

				GCGen gen = new GCGen(is, os);

				GCSignal[] a = gen.inputOfEva(new  boolean[32]);
				GCSignal[] d = h.secureCompute(a, gen);
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

				GCEva eva = new GCEva(is, os);
				GCSignal[] a = eva.inputOfEva(h.a);
				GCSignal[] d = h.secureCompute(a, eva);
				
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