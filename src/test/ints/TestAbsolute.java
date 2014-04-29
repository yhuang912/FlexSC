package test.ints;

import java.util.Random;
import gc.GCSignal;
import flexsc.*;
import org.junit.Test;
import test.harness.Test_1Input1Output;
import circuits.IntegerLib;

public class TestAbsolute extends Test_1Input1Output<GCSignal>{
	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			runThreads(
					new Helper(rnd.nextInt(1<<30), Mode.REAL) {
						public  GCSignal[] secureCompute(GCSignal[] Signala, CompEnv<GCSignal> e) throws Exception {
							return new IntegerLib<GCSignal>(e).absolute(Signala);
						}

						public int plainCompute(int x) {
							return (int)(Math.abs(x));
						}
					});
		}		
	}
}

