package test.floats;

import java.util.Random;

import objects.Float.Represention;
import flexsc.CompEnv;
import gc.Signal;

import org.junit.Test;

import test.harness.TestFloat;
import circuits.FloatLib;


public class TestFloatAdd extends TestFloat {

	@Test
	public void testAllCases() throws Exception {
		Random rng = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rng.nextDouble(), rng.nextDouble()) {
				
				@Override
				public Represention secureCompute(Represention a, Represention b, CompEnv<Signal> env) throws Exception {
					return new FloatLib(env).add(a, b);
				}
				
				@Override
				public double plainCompute(double a, double b) {
					return a+b;
				}
			});
		}
	}
}