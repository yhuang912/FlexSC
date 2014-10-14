package testlibs;

import harness.TestFixedPoint;

import java.util.Random;

import org.junit.Test;

import circuits.arithmetic.FixedPointLib;
import flexsc.CompEnv;

//import gc.Boolean;

public class TestFixedPointLib extends TestFixedPoint<Boolean> {

	@Test
	public void testFixPointAdd() throws Exception {
		Random rng = new Random();

		for (int i = 0; i < testCases; i++) {
			double d1 = rng.nextInt(1 << 30) % 10000.0;
			double d2 = rng.nextInt(1 << 30) % 10000.0;
			runThreads(new Helper(d1, d2) {

				@Override
				public Boolean[] secureCompute(Boolean[] a, Boolean[] b,
						int offset, CompEnv<Boolean> env) throws Exception {
					return new FixedPointLib<Boolean>(env, width, offset).add(
							a, b);
				}

				@Override
				public double plainCompute(double a, double b) {
					return a + b;
				}
			});
		}
	}

	@Test
	public void testFixPointSub() throws Exception {
		Random rng = new Random();

		for (int i = 0; i < testCases; i++) {
			double d1 = rng.nextInt(1 << 30) % 10000.0;
			double d2 = rng.nextInt(1 << 30) % 10000.0;
			runThreads(new Helper(d1, d2) {

				@Override
				public Boolean[] secureCompute(Boolean[] a, Boolean[] b,
						int offset, CompEnv<Boolean> env) throws Exception {
					return new FixedPointLib<Boolean>(env, width, offset).sub(
							a, b);
				}

				@Override
				public double plainCompute(double a, double b) {
					return a - b;
				}
			});
		}
	}

	@Test
	public void testFixPointMultiply() throws Exception {
		Random rng = new Random();

		for (int i = 0; i < testCases; i++) {
			double d1 = rng.nextInt(1 << 30) % 100.0;
			double d2 = rng.nextInt(1 << 30) % 100.0;
			runThreads(new Helper(d1, d2) {

				@Override
				public Boolean[] secureCompute(Boolean[] a, Boolean[] b,
						int offset, CompEnv<Boolean> env) throws Exception {
					return new FixedPointLib<Boolean>(env, width, offset)
							.multiply(a, b);
				}

				@Override
				public double plainCompute(double a, double b) {
					return a * b;
				}
			});
		}
	}

	@Test
	public void testFixPointDiv() throws Exception {
		Random rng = new Random();

		for (int i = 0; i < testCases; i++) {
			double d1 = rng.nextInt(1 << 30) % 100.0;
			double d2 = rng.nextInt(1 << 30) % 100.0;
			if (d2 == 0)
				++d2;
			runThreads(new Helper(d1, d2) {

				@Override
				public Boolean[] secureCompute(Boolean[] a, Boolean[] b,
						int offset, CompEnv<Boolean> env) throws Exception {
					return new FixedPointLib<Boolean>(env, width, offset).div(
							a, b);
				}

				@Override
				public double plainCompute(double a, double b) {
					return a / b;
				}
			}, 0.01);
		}
	}

	@Test
	public void testFixPointSqaureRoot() throws Exception {
		Random rng = new Random();

		for (int i = 0; i < testCases; i++) {
			double d1 = rng.nextInt(1 << 30) % 100.0;
			double d2 = rng.nextInt(1 << 30) % 100.0;
			if (d2 == 0)
				++d2;
			runThreads(new Helper(d1, d2) {

				@Override
				public Boolean[] secureCompute(Boolean[] a, Boolean[] b,
						int offset, CompEnv<Boolean> env) throws Exception {
					return new FixedPointLib<Boolean>(env, width, offset).sqrt(a);
				}

				@Override
				public double plainCompute(double a, double b) {
					return Math.sqrt(a);
				}
			}, 0.01);
		}
	}
}