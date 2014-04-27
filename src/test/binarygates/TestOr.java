package test.binarygates;

import java.util.Random;
import flexsc.CompEnv;
import gc.GCSignal;
import org.junit.Test;
import test.harness.Test_2Input1Output;
import circuits.IntegerLib;


public class TestOr extends Test_2Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt(1<<30), rnd.nextInt(1<<30)) {
				public GCSignal[] secureCompute(GCSignal[] Signala, GCSignal[] Signalb, CompEnv<GCSignal> e) throws Exception {
					return new IntegerLib(e).or(Signala ,Signalb);}

				public int plainCompute(int x, int y) {
					return x | y;}
			});
		}		
	}
}
