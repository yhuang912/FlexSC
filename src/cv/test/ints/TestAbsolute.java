package cv.test.ints;

import java.util.Random;
import org.junit.Test;
import cv.CVCompEnv;
import cv.test.harness.Test_1Input1Output;
import circuits.IntegerLib;


public class TestAbsolute extends Test_1Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			runTest(
				new Helper(rnd.nextInt(1<<30)) {
					@Override
					public int plainCompute(int x) {
						return (int)(Math.abs(x));
					}

					@Override
					public Boolean[] secureCompute(Boolean[] Signala,
							CVCompEnv e) throws Exception {
						return new IntegerLib<Boolean>(e).absolute(Signala);
					}
				});
		}		
	}
}