package test;

import java.util.Random;

import gc.CircuitLib;
import gc.CompEnv;
import gc.IntegerLib;
import gc.Signal;

import org.junit.Test;


public class TestConditionalLeftPublicShift extends Test_1Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 1000;

		for (int i = 0; i < testCases; i++) {
			final int shift = Math.abs(rnd.nextInt()%32);
			test1Case(
				new Helper(rnd.nextInt(1<<30)) {
					Signal[] secureCompute(Signal[] Signala, CompEnv<Signal> e) throws Exception {
						IntegerLib lib = new IntegerLib(e);
						return lib.conditionalLeftPublicShift(Signala, shift, CircuitLib.SIGNAL_ONE);
					}

					int plainCompute(int x) {
						return x << shift;
					}
				});
			
			test1Case(
					new Helper(rnd.nextInt(1<<30)) {
						Signal[] secureCompute(Signal[] Signala, CompEnv<Signal> e) throws Exception {
							IntegerLib lib = new IntegerLib(e);
							return lib.conditionalLeftPublicShift(Signala, shift, CircuitLib.SIGNAL_ZERO);
						}

						int plainCompute(int x) {
							return x;
						}
					});

		}		
	}
}