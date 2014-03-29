package test;

import java.util.Random;

import flexsc.CompEnv;
import gc.Signal;

import org.junit.Test;

import test.harness.Test_1Input1Output;
import circuits.CircuitLib;
import circuits.IntegerLib;


public class TestConditionalIncreament extends Test_1Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			runThreads(
					new Helper(rnd.nextInt(1<<30)) {
						public Signal[] secureCompute(Signal[] Signala, CompEnv<Signal> e) throws Exception {
							IntegerLib lib = new IntegerLib(e);
							return lib.conditionalDecrement(Signala, CircuitLib.SIGNAL_ONE);
						}

						public int plainCompute(int x) {
							return x-1;
						}
					});

			runThreads(
					new Helper(rnd.nextInt(1<<30)) {
						public Signal[] secureCompute(Signal[] Signala, CompEnv<Signal> e) throws Exception {
							IntegerLib lib = new IntegerLib(e);
							return lib.conditionalDecrement(Signala, CircuitLib.SIGNAL_ZERO);
						}

						public int plainCompute(int x) {
							return x;
						}
					});
		}		
	}
}