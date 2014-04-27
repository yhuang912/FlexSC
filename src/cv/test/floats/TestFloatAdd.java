package cv.test.floats;

import java.util.Random;

import objects.Float.Representation;

import org.junit.Test;

import cv.test.harness.TestFloat;
import cv.CVCompEnv;
import circuits.FloatLib;



public class TestFloatAdd extends TestFloat {

	@Test
	public void testAllCases() throws Exception {
		Random rng = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			runTest(new Helper(rng.nextDouble(), rng.nextDouble()) {
				
				@Override
				public Representation<Boolean> secureCompute(Representation<Boolean> a, Representation<Boolean> b, CVCompEnv env) throws Exception {
					return new FloatLib<Boolean>(env).add(a, b);
				}
				
				@Override
				public double plainCompute(double a, double b) {
					return a+b;
				}
			});
		}
	}
}