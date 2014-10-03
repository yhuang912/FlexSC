package test.fixedpoints;

import flexsc.CompEnv;
//import gc.Boolean;


import java.util.Random;

import org.junit.Test;

import test.harness.TestFixedPoint;
import circuits.arithmetic.FixedPointLib;


public class TestFixedPointDivide extends TestFixedPoint<Boolean> {

	@Test
	public void testAllCases() throws Exception {
		Random rng = new Random();

		for (int i = 0; i < testCases; i++) {
			double d1 = rng.nextDouble()*100;
			double d2 = rng.nextDouble()*100;
			runThreads(new Helper(d1, d2) {
				
				@Override
				public Boolean[] secureCompute(Boolean[] a, Boolean[] b, int offset, CompEnv<Boolean> env) throws Exception {
					return new FixedPointLib<Boolean>(env, width, offset).div(a, b);
				}
				
				@Override
				public double plainCompute(double a, double b) {
					return a/b;
				}
			}, 0.01);
		}
	}
}