package test.ints;

import java.util.Random;

import flexsc.CompEnv;
import gc.GCSignal;

import org.junit.Test;

import test.harness.Test_1Input1Output;
import test.harness.Test_1Input1Output.Helper;
import circuits.IntegerLib;


public class TestIncrementByOne extends Test_1Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			runThreads(
				new Helper(rnd.nextInt(1<<30)) {
					public GCSignal[] secureCompute(GCSignal[] Signala, CompEnv<GCSignal> e) throws Exception {
						return new IntegerLib(e).incrementByOne(Signala);
					}

					public int plainCompute(int x) {
						return x+1;
					}
				});
		}		
	}
}