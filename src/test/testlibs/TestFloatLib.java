package test.testlibs;

import gc.GCSignal;

import java.util.Random;

import org.junit.Test;

import test.harness.TestFloat;
import circuits.arithmetic.FloatLib;

//import gc.GCSignal;

public class TestFloatLib extends TestFloat<GCSignal> {

	Random rng = new Random();
	
	@Test
	public void testFloatAdd() throws Exception {
		for (int i = 0; i < testCases; i++) {
			double a = rng.nextDouble() * (1 << 20) - (1 << 19);
			double b = rng.nextDouble() * (1 << 20) - (1 << 19);
			
			runThreads(new Helper(a, b) {
				@Override
				public GCSignal[] secureCompute(GCSignal[] a, GCSignal[] b,
						FloatLib<GCSignal> lib) throws Exception {
					return lib.add(a, b);
				}
				@Override
				public double plainCompute(double a, double b) {
					return a + b;
				}
			});
		}

		double a = rng.nextDouble() * (1 << 20) - (1 << 19);
		runThreads(new Helper(a, -a) {
			@Override
			public GCSignal[] secureCompute(GCSignal[] a, GCSignal[] b,
					FloatLib<GCSignal> lib) throws Exception {
				return lib.add(a, b);
			}
			@Override
			public double plainCompute(double a, double b) {
				return a + b;
			}
		});
	}
	
	@Test
	public void testFloatSub() throws Exception {
		Random rng = new Random();

		for (int i = 0; i < testCases; i++) {
			double a = rng.nextDouble() * (1 << 20) - (1 << 19);
			double b = rng.nextDouble() * (1 << 20) - (1 << 19);
			runThreads(new Helper(a, b) {
				@Override
				public GCSignal[] secureCompute(GCSignal[] a, GCSignal[] b,
						FloatLib<GCSignal> lib) throws Exception {
					return lib.sub(a, b);
				}
				@Override
				public double plainCompute(double a, double b) {
					return a - b;
				}
			});
		}
		double a = rng.nextDouble() * (1 << 20) - (1 << 19);
		runThreads(new Helper(a, a) {
			@Override
			public GCSignal[] secureCompute(GCSignal[] a, GCSignal[] b,
					FloatLib<GCSignal> lib) throws Exception {
				return lib.sub(a, b);
			}
			@Override
			public double plainCompute(double a, double b) {
				return a - b;
			}
		});

	}
	
	@Test
	public void testFloatDiv() throws Exception {
		Random rng = new Random();

		for (int i = 0; i < testCases; i++) {
			double a = rng.nextDouble() * (1 << 20) - (1 << 19);
			double b = rng.nextDouble() * (1 << 20) - (1 << 19);
			a = (a == 0) ? 1 : a;
			runThreads(new Helper(b, a) {
				@Override
				public GCSignal[] secureCompute(GCSignal[] a, GCSignal[] b,
						FloatLib<GCSignal> lib) throws Exception {
					return lib.div(a, b);
				}
				@Override
				public double plainCompute(double a, double b) {
					return a / b;
				}
			});
		}
	}
	
	@Test
	public void testFloatMultiply() throws Exception {
		Random rng = new Random();

		for (int i = 0; i < testCases; i++) {
			double a = rng.nextDouble() * (1 << 20) - (1 << 19);
			double b = rng.nextDouble() * (1 << 20) - (1 << 19);
			runThreads(new Helper(a, b) {
				@Override
				public GCSignal[] secureCompute(GCSignal[] a, GCSignal[] b,
						FloatLib<GCSignal> lib) throws Exception {
					GCSignal[] res = lib.multiply(a, b);
					return res;
				}
				@Override
				public double plainCompute(double a, double b) {
					return a * b;
				}
			});
		}
	}
	
	@Test
	public void testFloatSq() throws Exception {
		Random rng = new Random();

		for (int i = 0; i < testCases; i++) {
			double a = rng.nextDouble() * (1 << 10);
			runThreads(new Helper(a, 1) {
				@Override
				public GCSignal[] secureCompute(GCSignal[] a, GCSignal[] b,
						FloatLib<GCSignal> lib) throws Exception {
					return lib.sqrt(a);
				}
				@Override
				public double plainCompute(double a, double b) {
					return Math.sqrt(a);
				}
			});
		}
	}
}