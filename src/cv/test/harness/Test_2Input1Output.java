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

	public void runTest(Helper h) throws Exception {
		boolean[] z = Utils.tobooleanArray(h.secureCompute(h.a, h.b, new CVCompEnv()));

		Assert.assertEquals(h.plainCompute(h.intA, h.intB), Utils.toSignedInt(z));
	}
}