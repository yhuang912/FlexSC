package test;

import java.util.Random;

import objects.Float.GCFloat;
import gc.CompEnv;
import gc.FloatLib;
import gc.Signal;

import org.junit.Test;


public class TestFloatAdd extends TestFloat {

	@Test
	public void testAllCases() throws Exception {
		Random rng = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			test1Case(new Helper(rng.nextDouble(), rng.nextDouble()) {
				
				@Override
				GCFloat secureCompute(GCFloat a, GCFloat b, CompEnv<Signal> env) throws Exception {
					return new FloatLib(env).add(a, b);
				}
				
				@Override
				double plainCompute(double a, double b) {
					return a+b;
				}
			});
		}
	}
}