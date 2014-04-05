package test.harness;

import java.util.Arrays;

import flexsc.CompEnv;
import gc.GCEva;
import gc.GCGen;
import gc.Signal;

import org.junit.Assert;

import test.Utils;


public class Test_2Input1Output {
	public abstract class Helper {
		int intA, intB;
		boolean[] a;
		boolean[] b;
		public Helper(int aa, int bb) {
			intA = aa;
			intB = bb;

			a = Utils.fromInt(aa, 32);
			b = Utils.fromInt(bb, 32);
		}
		public abstract Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception;
		public abstract int plainCompute(int x, int y);
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
				Signal[] a = gen.inputOfGen(h.a);
				Signal [] b = gen.inputOfEva(new boolean[32]);
				
				Signal[] d = h.secureCompute(a, b, gen);
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
				
				Signal [] a = eva.inputOfGen(new boolean[32]);
				Signal [] b = eva.inputOfEva(h.b);
				
				Signal[] d = h.secureCompute(a, b, eva);
				
				eva.outputToGen(d);
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
		tGen.start(); Thread.sleep(5);
		tEva.start();
		tGen.join();

		//System.out.println(Arrays.toString(gen.z));
		Assert.assertEquals(h.plainCompute(h.intA, h.intB), Utils.toSignedInt(gen.z));
	}

	

}