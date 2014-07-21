package test.ints;

import flexsc.CompEnv;
import flexsc.Mode;
import gc.GCSignal;

import java.util.Random;

import org.junit.Test;

import test.harness.Test_1Input1Output;
import circuits.IntegerLib;

public class TestIntegerSqrt extends Test_1Input1Output<GCSignal> {
	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 10;


		for (int i = 0; i < testCases; i++) {
			runThreads(
					new Helper(rnd.nextInt(1 << 10), Mode.REAL) {
						public  GCSignal[] secureCompute(GCSignal[] Signala, CompEnv<GCSignal> e) throws Exception {
							return new IntegerLib<GCSignal>(e).integerSqrt(Signala);
						}

						public int plainCompute(int x) {
							return (int) Math.sqrt(x);
							// return (int)(plainSqrt(x));
						}
					});
		}
	}

	private int plainSqrt(long a) {
		System.out.println("Starting plainSqrt");
		long rem = 0;
		long root = 0;
		for (int i = 0; i < 16; i++) {
			root = root << 1;
			rem = ((rem << 2) + (a >> 30));
			System.out.println(root + "\t" + rem);
			a = a << 2;
			root++;
			if (root <= rem) {
				rem = rem - root;
				root++;
			} else {
				root--;
			}
		}
		return (int) (root >> 1);
	}
}
