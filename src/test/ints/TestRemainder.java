package test.ints;

import java.util.Random;

import flexsc.*;
import gc.GCSignal;

import org.junit.Test;

import test.harness.Test_2Input1Output;
import circuits.IntegerLib;



public class TestRemainder extends Test_2Input1Output<GCSignal>{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			int b = rnd.nextInt()%(1<<15);
			b = (b == 0) ? 1 : b;
			runThreads(new Helper(rnd.nextInt()%(1<<15), b, Mode.REAL){
				public GCSignal[] secureCompute(GCSignal[] Signala, GCSignal[] Signalb, CompEnv<GCSignal> e) throws Exception {
					return new IntegerLib<GCSignal>(e).reminder(Signala ,Signalb);}

				public int plainCompute(int x, int y) {
					return x%y;}
			});
		}		
	}
}