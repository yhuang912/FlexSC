package test;

import java.util.Random;

import flexsc.CompEnv;
import gc.Signal;

import org.junit.Test;

import circuits.IntegerLib;


public class TestRightPublicShift extends Test_1Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			final int shift = Math.abs(rnd.nextInt()%32);
			runThreads(
				new Helper(rnd.nextInt(1<<30)) {
					Signal[] secureCompute(Signal[] Signala, CompEnv<Signal> e) throws Exception {
						return new IntegerLib(e).rightPublicShift(Signala, shift);
					}

					int plainCompute(int x) {
						return x>>shift;
					}
				});
		}		
	}
}