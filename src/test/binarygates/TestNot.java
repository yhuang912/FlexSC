package test.binarygates;

import java.util.Random;

import flexsc.CompEnv;
import gc.Signal;

import org.junit.Test;

import test.harness.Test_1Input1Output;
import circuits.CircuitLib;


public class TestNot extends Test_1Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			runThreads(
				new Helper(rnd.nextInt(1<<30)) {
					public Signal[] secureCompute(Signal[] Signala, CompEnv<Signal> e) throws Exception {
						return new CircuitLib(e).not(Signala);
					}

					public int plainCompute(int x) {
						return x^0xFFFFFFFF;
					}
				});
		}		
	}
}
