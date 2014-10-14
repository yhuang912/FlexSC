package testlibs;

import harness.Test_2Input1Output;

import java.util.Random;

import org.junit.Test;

import util.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;

//import gc.Boolean;

public class TestIntegerLib extends Test_2Input1Output<Boolean> {

	Random rnd = new Random();

	@Test
	public void testIntAdd() throws Exception {
		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt() % (1 << 31), rnd.nextInt()
					% (1 << 31)) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e).add(Signala, Signalb);
				}

				public int plainCompute(int x, int y) {
					return x + y;
				}
			});
		}
	}

	@Test
	public void testIntSub() throws Exception {

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt() % (1 << 30), rnd.nextInt()
					% (1 << 30)) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e).sub(Signala, Signalb);
				}

				public int plainCompute(int x, int y) {
					return x - y;
				}
			});
		}
	}

	@Test
	public void testIntDiv() throws Exception {

		for (int i = 0; i < testCases; i++) {
			int b = rnd.nextInt() % (1 << 15);
			int a = rnd.nextInt() % (1 << 15);
			b = (b == 0) ? 1 : b;
			runThreads(new Helper(a, b) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e).div(Signala, Signalb);
				}

				public int plainCompute(int x, int y) {
					return x / y;
				}
			});
		}
	}

	@Test
	public void testIntMultiplication() throws Exception {

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt() % (1 << 30), rnd.nextInt()
					% (1 << 30)) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e)
							.multiply(Signala, Signalb);
				}

				public int plainCompute(int x, int y) {
					return x * y;
				}
			});
		}
	}

	@Test
	public void testIntMod() throws Exception {

		for (int i = 0; i < testCases; i++) {
			int b = rnd.nextInt() % (1 << 15);
			b = (b == 0) ? 1 : b;
			runThreads(new Helper(rnd.nextInt() % (1 << 15), b) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e).mod(Signala, Signalb);
				}

				public int plainCompute(int x, int y) {
					return x % y;
				}
			});
		}
	}

	@Test
	public void testIntEq() throws Exception {

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt() % (1 << 30), rnd.nextInt()
					% (1 << 30)) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new Boolean[] { new IntegerLib<Boolean>(e).eq(
							Signala, Signalb) };
				}

				public int plainCompute(int x, int y) {
					return (x == y) ? 1 : 0;
				}
			});
		}

		for (int i = 0; i < testCases; i++) {
			int a = rnd.nextInt(1 << 30);
			runThreads(new Helper(a, a) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new Boolean[] { new IntegerLib<Boolean>(e).eq(
							Signala, Signalb) };
				}

				public int plainCompute(int x, int y) {
					return (int) Utils.toSignedInt(new boolean[] { (x == y) });
				}
			});
		}
	}

	@Test
	public void testIntGeq() throws Exception {

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt() % (1 << 30), rnd.nextInt()
					% (1 << 30)) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new Boolean[] { new IntegerLib<Boolean>(e).geq(
							Signala, Signalb) };
				}

				public int plainCompute(int x, int y) {
					return (int) Utils.toSignedInt(new boolean[] { (x >= y) });
				}
			});
		}

		for (int i = 0; i < testCases; i++) {
			int a = rnd.nextInt() % (1 << 30);
			runThreads(new Helper(a, a) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new Boolean[] { new IntegerLib<Boolean>(e).geq(
							Signala, Signalb) };
				}

				public int plainCompute(int x, int y) {
					return (int) Utils.toSignedInt(new boolean[] { (x >= y) });
				}
			});
		}
	}

	@Test
	public void testIntLeq() throws Exception {

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt() % (1 << 30), rnd.nextInt()
					% (1 << 30)) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new Boolean[] { new IntegerLib<Boolean>(e).leq(
							Signala, Signalb) };
				}

				public int plainCompute(int x, int y) {
					return (int) Utils.toSignedInt(new boolean[] { (x <= y) });
				}
			});
		}

		for (int i = 0; i < testCases; i++) {
			int a = rnd.nextInt(1 << 30);
			runThreads(new Helper(a, a) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new Boolean[] { new IntegerLib<Boolean>(e).leq(
							Signala, Signalb) };
				}

				public int plainCompute(int x, int y) {
					return (int) Utils.toSignedInt(new boolean[] { (x <= y) });
				}
			});
		}
	}

	@Test
	public void testIntSqrt() throws Exception {
		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt(1 << 30), 0) {
				@Override
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e).sqrt(Signala);
				}

				@Override
				public int plainCompute(int x, int y) {
					return (int) Math.sqrt(x);
				}
			});
		}
	}

	@Test
	public void testIntAbs() throws Exception {
		Random rnd = new Random();

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt(1 << 30), 0) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e).absolute(Signala);
				}

				public int plainCompute(int x, int y) {
					return (int) (Math.abs(x));
				}
			});
		}
	}

	@Test
	public void testIncrementByOne() throws Exception {
		Random rnd = new Random();

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt(1 << 30), 0) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e).incrementByOne(Signala);
				}

				public int plainCompute(int x, int y) {
					return x + 1;
				}
			});
		}
	}

	@Test
	public void testDecrementByOne() throws Exception {
		Random rnd = new Random();

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt(1 << 30), 0) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e).decrementByOne(Signala);
				}

				public int plainCompute(int x, int y) {
					return x - 1;
				}
			});
		}
	}
}