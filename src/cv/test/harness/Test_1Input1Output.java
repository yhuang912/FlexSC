package cv.test.harness;

import org.junit.Assert;

import cv.CVCompEnv;
import test.Utils;


public class Test_1Input1Output {
	public abstract class Helper {
		int intA, intB;
		Boolean[] a;
		public Helper(int aa) {
			intA = aa;

			a = Utils.toBooleanArray(Utils.fromInt(aa, 32));
		}
		public abstract Boolean[] secureCompute(Boolean[] Signala, CVCompEnv e) throws Exception;
		public abstract int plainCompute(int x);
	}

	public void runTest(Helper h) throws Exception {
		boolean[] z = Utils.tobooleanArray(h.secureCompute(h.a, new CVCompEnv()));

		Assert.assertEquals(h.plainCompute(h.intA), Utils.toSignedInt(z));
	}
}