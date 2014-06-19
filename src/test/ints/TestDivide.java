package test.ints;

import flexsc.CompEnv;
import flexsc.Mode;
import gc.GCSignal;

import java.util.Random;

import org.junit.Test;

import test.harness.Test_2Input1Output;
import circuits.IntegerLib;


public class TestDivide extends Test_2Input1Output<GCSignal>{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			int b = rnd.nextInt()%(1<<15);
			int a = rnd.nextInt()%(1<<15);
			b = (b == 0) ? 1 : b;
			runThreads(new Helper(a, b, Mode.REAL){
				public GCSignal[] secureCompute(GCSignal[] Signala, GCSignal[] Signalb, CompEnv<GCSignal> e) throws Exception {
					return new IntegerLib<GCSignal>(e).divide(Signala ,Signalb);}

				public int plainCompute(int x, int y) {
					return x/y;}
			});
		}		
	}
}