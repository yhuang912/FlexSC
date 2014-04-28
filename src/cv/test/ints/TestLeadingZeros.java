package cv.test.ints;

import java.util.Random;
import flexsc.CompEnv;
import org.junit.Assert;
import org.junit.Test;
import cv.CVCompEnv;
import test.Utils;
import circuits.IntegerLib;


public class TestLeadingZeros {
	public abstract class Helper {
		long intA;
		Boolean[] a;
		Helper(long aa) {
			intA = aa;
			a =  Utils.toBooleanArray( Utils.fromLong(aa, 64));
		}
		abstract Boolean[] secureCompute(Boolean[] Signala, CompEnv<Boolean> e) throws Exception;
		abstract int plainCompute(long x);
	}

	public void runTest(Helper h) throws Exception {
		CVCompEnv e = new CVCompEnv();
		boolean[] z = Utils.tobooleanArray(h.secureCompute(h.a, e));
		Assert.assertEquals(h.plainCompute(h.intA), Utils.toInt(z));
	}

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			runTest(
				new Helper(rnd.nextLong()) {
					Boolean[] secureCompute(Boolean[] Signala, CompEnv<Boolean> e) throws Exception {
						return new IntegerLib<Boolean>(e).leadingZeros(Signala);
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
