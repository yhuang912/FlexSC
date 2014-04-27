package test.ints;

import java.util.Random;

import flexsc.CompEnv;
import gc.GCSignal;

import org.junit.Test;

import test.harness.Test_2Input1Output;
import test.harness.Test_2Input1Output.Helper;
import circuits.IntegerLib;


public class TestDivide extends Test_2Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			int b = rnd.nextInt()%(1<<15);
			int a = rnd.nextInt()%(1<<15);
			b = (b == 0) ? 1 : b;
			runThreads(new Helper(a, b){
				public GCSignal[] secureCompute(GCSignal[] Signala, GCSignal[] Signalb, CompEnv<GCSignal> e) throws Exception {
					return new IntegerLib<GCSignal>(e).divide(Signala ,Signalb);}

				public int plainCompute(int x, int y) {
					return x/y;}
			});
		}		
	}
}