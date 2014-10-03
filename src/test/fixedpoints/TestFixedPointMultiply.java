package test.fixedpoints;

import java.util.Random;

import org.junit.Test;

import test.harness.TestFixedPoint;
import circuits.arithmetic.FixedPointLib;
import flexsc.CompEnv;


public class TestFixedPointMultiply extends TestFixedPoint<Boolean> {

	@Test
	public void testAllCases() throws Exception {
		Random rng = new Random();

		for (int i = 0; i < testCases; i++) {
			double d1 = rng.nextInt(1<<30)%1000000.0/1000000.0;
			double d2 = rng.nextInt(1<<30)%1000000.0/1000000.0;
			runThreads(new Helper(d1, d2) {
				
				@Override
				public Boolean[] secureCompute(Boolean[] a, Boolean[] b, int offset, CompEnv<Boolean> env) throws Exception {
					return new FixedPointLib<Boolean>(env, width, offset).multiply(a, b);
				}
				
				@Override
				public double plainCompute(double a, double b) {
					return a*b;
				}
			});
		}
	}
}