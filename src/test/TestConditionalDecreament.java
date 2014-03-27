package test;

import java.util.Random;

import flexsc.CompEnv;
import gc.Signal;

import org.junit.Test;

import circuits.CircuitLib;
import circuits.IntegerLib;


public class TestConditionalDecreament extends Test_1Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			runThreads(
					new Helper(rnd.nextInt(1<<30)) {
						Signal[] secureCompute(Signal[] Signala, CompEnv<Signal> e) throws Exception {
							IntegerLib lib = new IntegerLib(e);
							return lib.conditionalIncreament(Signala, CircuitLib.SIGNAL_ONE);
						}

						int plainCompute(int x) {
							return x+1;
						}
					});

			runThreads(
					new Helper(rnd.nextInt(1<<30)) {
						Signal[] secureCompute(Signal[] Signala, CompEnv<Signal> e) throws Exception {
							IntegerLib lib = new IntegerLib(e);
							return lib.conditionalIncreament(Signala, CircuitLib.SIGNAL_ZERO);
						}

						int plainCompute(int x) {
							return x;
						}
					});
		}		
	}
}