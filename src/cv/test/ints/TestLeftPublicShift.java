package cv.test.ints;

import java.util.Random;
import org.junit.Test;
import cv.CVCompEnv;
import cv.test.harness.Test_1Input1Output;
import circuits.IntegerLib;


public class TestLeftPublicShift extends Test_1Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			final int shift = rnd.nextInt(31);
			runTest(
				new Helper(rnd.nextInt()%(1<<30)) {
					public Boolean[] secureCompute(Boolean[] Signala, CVCompEnv e) throws Exception {
						return new IntegerLib<Boolean>(e).leftPublicShift(Signala, shift);
					}
					public int plainCompute(int x) {
						return x<<shift;
					}
				
			});
		}	
	}
}
