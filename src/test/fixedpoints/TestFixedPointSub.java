package test.fixedpoints;

import flexsc.CompEnv;
import flexsc.Mode;
import gc.GCSignal;

import java.util.Random;

import org.junit.Test;

import test.harness.TestFixedPoint;
import circuits.FixedPointLib;


public class TestFixedPointSub extends TestFixedPoint<GCSignal> {

	@Test
	public void testAllCases() throws InterruptedException {
		Random rng = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			double d1 = rng.nextInt(1<<30)%1000000.0/100000.0;
			double d2 = rng.nextInt(1<<30)%1000000.0/100000.0;
			runThreads(new Helper(d1, d2, Mode.REAL) {
				
				@Override
				public GCSignal[] secureCompute(GCSignal[] a, GCSignal[] b, int offset, CompEnv<GCSignal> env) throws Exception {
					return new FixedPointLib<GCSignal>(env).sub(a, b, offset);
				}
				
				@Override
				public double plainCompute(double a, double b) {
					return a-b;
				}
			});
		}
	}
}