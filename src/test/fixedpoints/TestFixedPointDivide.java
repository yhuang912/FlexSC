package test.fixedpoints;

import java.util.Random;

import flexsc.CompEnv;
import gc.Signal;
import org.junit.Test;
import test.harness.TestFixedPoint;
import test.harness.TestFloat;
import circuits.FixedPointLib;


public class TestFixedPointDivide extends TestFixedPoint {

	@Test
	public void testAllCases() throws Exception {
		Random rng = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			double d1 = rng.nextInt(1<<30)%1000000.0/1000000.0;
			double d2 = rng.nextInt(1<<30)%1000000.0/1000000.0;
			runThreads(new Helper(d1, d2) {
				
				@Override
				public Signal[] secureCompute(Signal[] a, Signal[] b, int offset, CompEnv<Signal> env) throws Exception {
					return new FixedPointLib(env).divide(a, b, offset);
				}
				
				@Override
				public double plainCompute(double a, double b) {
					return a/b;
				}
			});
		}
	}
}