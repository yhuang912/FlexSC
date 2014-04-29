package test.binarygates;

import java.util.Random;
import flexsc.*;
import gc.GCSignal;
import org.junit.Test;
import test.harness.Test_1Input1Output;
import circuits.CircuitLib;


public class TestNot extends Test_1Input1Output<GCSignal>{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			runThreads(
				new Helper(rnd.nextInt(1<<30), Mode.REAL) {
					public GCSignal[] secureCompute(GCSignal[] Signala, CompEnv<GCSignal> e) throws Exception {
						return new CircuitLib<GCSignal>(e).not(Signala);
					}

					public int plainCompute(int x) {
						return x^0xFFFFFFFF;
					}
				});
		}		
	}
}
