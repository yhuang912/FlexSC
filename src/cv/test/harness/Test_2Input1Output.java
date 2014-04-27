package cv.test.harness;

import org.junit.Assert;

import cv.CVCompEnv;
import test.Utils;


public class Test_2Input1Output {
	public abstract class Helper {
		int intA, intB;
		Boolean[] a;
		Boolean[] b;
		public Helper(int aa, int bb) {
			intA = aa;
			intB = bb;

			a = Utils.toBooleanArray(Utils.fromInt(aa, 32));
			b = Utils.toBooleanArray(Utils.fromInt(bb, 32));
		}
		public abstract Boolean[] secureCompute(Boolean[] Signala, Boolean[] Signalb, CVCompEnv e) throws Exception;
		public abstract int plainCompute(int x, int y);
	}

//	class GenRunnable extends network.Server implements Runnable {
//		boolean[] z;
//		Helper h;
//		GenRunnable (Helper h) {
//			this.h = h;
//		}
//
//		public void run() {
//			try {
//				listen(54321);
//
//				GCGen gen = new GCGen(is, os);
//				GCSignal[] a = gen.inputOfGen(h.a);
//				GCSignal [] b = gen.inputOfEva(new boolean[32]);
//				
//				GCSignal[] d = h.secureCompute(a, b, gen);
//				os.flush();
//
//				z = gen.outputToGen(d);
//
//				disconnect();
//			} catch (Exception e) {
//				e.printStackTrace();
//				System.exit(1);
//			}
//		}
//	}
//
//	class EvaRunnable extends network.Client implements Runnable {
//		Helper h;
//		EvaRunnable (Helper h) {
//			this.h = h;
//		}
//
//		public void run() {
//			try {
//				connect("localhost", 54321);				
//
//				GCEva eva = new GCEva(is, os);
//				
//				GCSignal [] a = eva.inputOfGen(new boolean[32]);
//				GCSignal [] b = eva.inputOfEva(h.b);
//				
//				GCSignal[] d = h.secureCompute(a, b, eva);
//				
//				eva.outputToGen(d);
//				os.flush();
//
//				disconnect();
//			} catch (Exception e) {
//				e.printStackTrace();
//				System.exit(1);
//			}
//		}
//	}

	public void runTest(Helper h) throws Exception {
		boolean[] z = Utils.tobooleanArray(h.secureCompute(h.a, h.b, new CVCompEnv()));

		//System.out.println(Arrays.toString(gen.z));
		Assert.assertEquals(h.plainCompute(h.intA, h.intB), Utils.toSignedInt(z));
	}

	

}