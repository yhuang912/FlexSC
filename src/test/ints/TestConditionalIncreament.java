package test.ints;

import java.util.Random;

import flexsc.*;
import gcHalfANDs.GCSignal;

import org.junit.Test;

import test.harness.Test_1Input1Output;
import circuits.IntegerLib;


public class TestConditionalIncreament extends Test_1Input1Output<GCSignal>{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			runThreads(
					new Helper(rnd.nextInt(1<<30), Mode.REAL) {
						public GCSignal[] secureCompute(GCSignal[] Signala, CompEnv<GCSignal> e) throws Exception {
							IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(e);
							return lib.conditionalDecrement(Signala, lib.SIGNAL_ONE);
						}

						public int plainCompute(int x) {
							return x-1;
						}
					});

			runThreads(
					new Helper(rnd.nextInt(1<<30), Mode.REAL) {
						public GCSignal[] secureCompute(GCSignal[] Signala, CompEnv<GCSignal> e) throws Exception {
							IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(e);
							return lib.conditionalDecrement(Signala, lib.SIGNAL_ZERO);
						}

						public int plainCompute(int x) {
							return x;
						}
					});
		}		
	}
}