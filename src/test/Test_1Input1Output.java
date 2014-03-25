package test;

import flexsc.CompEnv;
import gc.GCEva;
import gc.GCGen;
import gc.Signal;

import org.junit.Assert;


public class Test_1Input1Output {
	public abstract class Helper {
		int intA;
		boolean[] a;
		Helper(int aa) {
			intA = aa;
			a = Utils.fromInt(aa, 32);
		}
		abstract Signal[] secureCompute(Signal[] Signala, CompEnv<Signal> e) throws Exception;
		abstract int plainCompute(int x);
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

				Signal[] a = gen.inputOfEva(new  boolean[32]);
				Signal[] d = h.secureCompute(a, gen);
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
				Signal[] a = eva.inputOfEva(h.a);
				Signal[] d = h.secureCompute(a, eva);
				
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