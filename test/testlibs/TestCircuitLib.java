package testlibs;

import harness.Test_2Input1Output;

import java.util.Random;

import org.junit.Test;

import circuits.CircuitLib;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;

//import gc.Boolean;

public class TestCircuitLib extends Test_2Input1Output<Boolean> {

	Random rnd = new Random();

	@Test
	public void testRightPublicShift() throws Exception {
		Random rnd = new Random();

		for (int i = 0; i < testCases; i++) {
			final int shift = Math.abs(rnd.nextInt() % 32);
			runThreads(new Helper(rnd.nextInt(1 << 30), 0) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e).rightPublicShift(Signala,
							shift);
				}

				public int plainCompute(int x, int y) {
					return x >> shift;
				}
			});
		}
	}

	@Test
	public void testConditionalLeftPublicShift() throws Exception {
		Random rnd = new Random();

		for (int i = 0; i < testCases; i++) {
			final int shift = Math.abs(rnd.nextInt() % 32);
			runThreads(new Helper(rnd.nextInt(1 << 30), 0) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					IntegerLib<Boolean> lib = new IntegerLib<Boolean>(e);
					return lib.conditionalLeftPublicShift(Signala, shift,
							lib.SIGNAL_ONE);
				}

				public int plainCompute(int x, int y) {
					return x << shift;
				}
			});

			runThreads(new Helper(rnd.nextInt(1 << 30), 0) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					IntegerLib<Boolean> lib = new IntegerLib<Boolean>(e);
					return lib.conditionalLeftPublicShift(Signala, shift,
							lib.SIGNAL_ZERO);
				}

				public int plainCompute(int x, int y) {
					return x;
				}
			});

		}
	}

	@Test
	public void testConditionalRightPublicShift() throws Exception {
		Random rnd = new Random();

		for (int i = 0; i < testCases; i++) {
			final int shift = Math.abs(rnd.nextInt() % 32);
			runThreads(new Helper(rnd.nextInt(1 << 30), 0) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					IntegerLib<Boolean> lib = new IntegerLib<Boolean>(e);
					return lib.conditionalRightPublicShift(Signala, shift,
							lib.SIGNAL_ONE);
				}

				public int plainCompute(int x, int y) {
					return x >> shift;
				}
			});

			runThreads(new Helper(rnd.nextInt(1 << 30), 0) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					IntegerLib<Boolean> lib = new IntegerLib<Boolean>(e);
					return lib.conditionalRightPublicShift(Signala, shift,
							lib.SIGNAL_ZERO);
				}

				public int plainCompute(int x, int y) {
					return x;
				}
			});

		}
	}

	@Test
	public void testLeftPrivateShift() throws Exception {
		Random rnd = new Random();

		for (int i = 0; i < testCases; i++) {
			int shift = rnd.nextInt(1 << 5);
			runThreads(new Helper(rnd.nextInt(1 << 30), shift) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e).leftPrivateShift(Signala,
							Signalb);
				}

				public int plainCompute(int x, int y) {
					return x << y;
				}
			});
		}
	}

	@Test
	public void testLeftPublicShift() throws Exception {
		Random rnd = new Random();

		for (int i = 0; i < testCases; i++) {
			final int shift = Math.abs(rnd.nextInt() % 32);
			runThreads(new Helper(rnd.nextInt(1 << 30), 0) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e).leftPublicShift(Signala,
							shift);
				}

				public int plainCompute(int x, int y) {
					return x << shift;
				}
			});
		}
	}

	public int commonPrefix(int l1, int l2) {
		int res = 0;
		int diff = l1 ^ l2;
		if ((diff & 0xFFFF0000) == 0) {
			res += 16;
			diff <<= 16;
		}
		if ((diff & 0xFF000000) == 0) {
			res += 8;
			diff <<= 8;
		}
		if ((diff & 0xF0000000) == 0) {
			res += 4;
			diff <<= 4;
		}
		if ((diff & 0xC0000000) == 0) {
			res += 2;
			diff <<= 2;
		}
		if ((diff & 0x80000000) == 0) {
			res += 1;
			diff <<= 1;
		}
		if (diff == 0) {
			res += 1;
		}
		return res;
	}

	@Test
	public void testLengthOfCommenPrefix() throws Exception {
		Random rnd = new Random();

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt(1 << 30), rnd.nextInt(1 << 30)) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e).lengthOfCommenPrefix(
							Signala, Signalb);
				}

				public int plainCompute(int x, int y) {
					return commonPrefix(x, y);
				}
			});
		}
	}

	@Test
	public void testRightPrivateShift() throws Exception {
		Random rnd = new Random();

		for (int i = 0; i < testCases; i++) {
			int shift = rnd.nextInt(1 << 5);
			runThreads(new Helper(rnd.nextInt(1 << 30), shift) {
				public Boolean[] secureCompute(Boolean[] Signala,
						Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e).rightPrivateShift(
							Signala, Signalb);
				}

				public int plainCompute(int x, int y) {
					return x >> y;
				}
			});
		}
	}

	@Test
	public void testAllCases() throws Exception {

		for (int i = 0; i < 1; i++) {
			runThreads(new Helper(0b1100, 0b1010) { // This particular pair of
													// inputs exhausts 4
													// possible inputs,
													// excluding selection
													// signal
				public Boolean[] secureCompute(Boolean[] a, Boolean[] b,
						CompEnv<Boolean> e) throws Exception {
					CircuitLib<Boolean> lib = new CircuitLib<Boolean>(e);
					return lib.mux(a, b, lib.SIGNAL_ONE);
				}

				public int plainCompute(int x, int y) {
					return y;
				}
			});
		}

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(0b1100, 0b1010) { // This particular pair of
													// inputs exhausts 4
													// possible inputs,
													// excluding selection
													// signal
				public Boolean[] secureCompute(Boolean[] a, Boolean[] b,
						CompEnv<Boolean> e) throws Exception {
					CircuitLib<Boolean> lib = new CircuitLib<Boolean>(e);
					return lib.mux(a, b, lib.SIGNAL_ZERO);
				}

				public int plainCompute(int x, int y) {
					return x;
				}
			});
		}
	}
}