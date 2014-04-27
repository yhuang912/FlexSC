package test.ints;

import java.util.Random;

import flexsc.CompEnv;
import gc.GCSignal;

import org.junit.Test;

import test.harness.Test_1Input1Output;
import test.harness.Test_1Input1Output.Helper;
import circuits.IntegerLib;


public class TestLeftPublicShift extends Test_1Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			final int shift = Math.abs(rnd.nextInt()%32);
			runThreads(
				new Helper(rnd.nextInt(1<<30)) {
					public GCSignal[] secureCompute(GCSignal[] Signala, CompEnv<GCSignal> e) throws Exception {
						return new IntegerLib(e).leftPublicShift(Signala, shift);
					}

					public int plainCompute(int x) {
						return x<<shift;
					}
				});
		}		
	}
}