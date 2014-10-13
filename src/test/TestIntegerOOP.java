package test;

import flexsc.CompEnv;
import gcHalfANDs.GCEva;
import gcHalfANDs.GCGen;
import gcHalfANDs.GCSignal;

import org.junit.Assert;


public class TestIntegerOOP {
	public abstract class Helper {
		int intA;
		boolean[] a;
		Helper(int aa) {
			intA = aa;
			a = Utils.fromInt(aa, 32);
		}
		abstract GCSignal[] secureCompute(GCSignal[] Signala, CompEnv<GCSignal> e) throws Exception;
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

				GCSignal[] a = gen.inputOfBob(new  boolean[32]);
				GCSignal[] d = h.secureCompute(a, gen);
				os.flush();

				z = gen.outputToAlice(d);

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
				GCSignal[] a = eva.inputOfBob(h.a);

				GCSignal[] d = h.secureCompute(a, eva);
				
				eva.outputToAlice(d);
				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void test1Case(Helper helper) throws Exception {
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