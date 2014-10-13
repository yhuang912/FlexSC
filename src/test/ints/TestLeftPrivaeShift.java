package test.ints;

import java.util.Random;

import flexsc.*;
import gc.GCSignal;

import org.junit.Test;

import test.harness.Test_2Input1Output;
import circuits.IntegerLib;


public class TestLeftPrivaeShift extends Test_2Input1Output<GCSignal>{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			int shift = rnd.nextInt(1<<5);
			runThreads(new Helper(rnd.nextInt(1<<30), shift, Mode.REAL) {
				public GCSignal[] secureCompute(GCSignal[] Signala, GCSignal[] Signalb, CompEnv<GCSignal> e) throws Exception {
					return new IntegerLib<GCSignal>(e).leftPrivateShift(Signala ,Signalb);}

				public int plainCompute(int x, int y) {
					return x<<y;}
			});
		}		
	}
}