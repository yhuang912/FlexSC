package test.floats;

import java.util.Random;

import org.junit.Test;

import test.harness.TestFloat;
import circuits.arithmetic.FloatLib;
import flexsc.CompEnv;
//import gc.Boolean;


public class TestFloatAdd extends TestFloat<Boolean> {

	@Test
	public void testAllCases() throws Exception {
		Random rng = new Random();
		
		
		for (int i = 0; i < testCases; i++) {
			double a = rng. nextDouble()*(1<<30)-(1<<29);
			double b = rng.nextDouble()*(1<<30)-(1<<29);
			runThreads(new Helper(a,b) {
				
				@Override
				public Boolean[] secureCompute(Boolean[] a, Boolean[] b, CompEnv<Boolean> env) throws Exception {
					return new FloatLib<Boolean>(env, widthV, widthP).add(a, b);
				}
				
				@Override
				public double plainCompute(double a, double b) {
					return a+b;
				}
			});
		}
		
		double a = rng.nextDouble()*1000;
		runThreads(new Helper(a, -a) {
			
			@Override
			public Boolean[] secureCompute(Boolean[] a, Boolean[] b, CompEnv<Boolean> env) throws Exception {
				return new FloatLib<Boolean>(env, widthV, widthP).add(a, b);
			}
			
			@Override
			public double plainCompute(double a, double b) {
				return a+b;
			}
		});
	}
}