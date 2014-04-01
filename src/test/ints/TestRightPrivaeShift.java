package test.ints;

import java.util.Random;

import flexsc.CompEnv;
import gc.Signal;

import org.junit.Test;

import test.harness.Test_2Input1Output;
import test.harness.Test_2Input1Output.Helper;
import circuits.IntegerLib;


public class TestRightPrivaeShift extends Test_2Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			int shift = rnd.nextInt(1<<5);
			runThreads(new Helper(rnd.nextInt(1<<30), shift) {
				public Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception {
					return new IntegerLib(e).rightPrivateShift(Signala ,Signalb);}

				public int plainCompute(int x, int y) {
					return x>>y;}
			});
		}		
	}
}