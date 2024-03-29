package test.ints;

import java.util.Random;

import flexsc.*;
import gc.GCSignal;
import org.junit.Test;
import test.harness.Test_1Input1Output;
import circuits.IntegerLib;


public class TestRightPublicShift extends Test_1Input1Output<GCSignal>{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			final int shift = Math.abs(rnd.nextInt()%32);
			runThreads(
				new Helper(rnd.nextInt(1<<30), Mode.REAL) {
					public GCSignal[] secureCompute(GCSignal[] Signala, CompEnv<GCSignal> e) throws Exception {
						return new IntegerLib<GCSignal>(e).rightPublicShift(Signala, shift);
					}

					public int plainCompute(int x) {
						return x>>shift;
					}
				});
		}		
	}
}