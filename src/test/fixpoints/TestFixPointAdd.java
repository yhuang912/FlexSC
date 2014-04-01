package test.fixpoints;

import java.util.Random;

import flexsc.CompEnv;
import gc.Signal;
import org.junit.Test;
import test.harness.TestFixPoint;
import test.harness.TestFloat;
import circuits.FixPointLib;


public class TestFixPointAdd extends TestFixPoint {

	@Test
	public void testAllCases() throws Exception {
		Random rng = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			double d1 = rng.nextInt(1<<30)%1000000.0/100000.0;
			double d2 = rng.nextInt(1<<30)%1000000.0/100000.0;
			runThreads(new Helper(d1, d2) {
				
				@Override
				public Signal[] secureCompute(Signal[] a, Signal[] b, int offset, CompEnv<Signal> env) throws Exception {
					return new FixPointLib(env).add(a, b, offset);
				}
				
				@Override
				public double plainCompute(double a, double b) {
					return a+b;
				}
			});
		}
	}
}