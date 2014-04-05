package test.floats;

import java.util.Random;

import objects.Float.Represention;
import flexsc.CompEnv;
import gc.Signal;

import org.junit.Test;

import test.harness.TestFloat;
import circuits.FloatLib;


public class TestFloatDivide extends TestFloat {

	@Test
	public void testAllCases() throws Exception {
		Random rng = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			double a = rng.nextDouble();
			a = (a == 0) ? 1:a;
			runThreads(new Helper(rng.nextDouble(), a) {
				
				@Override
				public Represention secureCompute(Represention a, Represention b, CompEnv<Signal> env) throws Exception {
					return new FloatLib(env).divide(a, b);
				}
				
				@Override
				public double plainCompute(double a, double b) {
					return a/b;
				}
			});
		}
	}
}