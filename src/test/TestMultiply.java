package test;

import java.util.Random;

import gc.CompEnv;
import gc.IntegerLib;
import gc.Signal;
import org.junit.Test;


public class TestMultiply extends Test_2Input1Output{
	
	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt(1<<15), rnd.nextInt(1<<15)) {
				Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception {
					return new IntegerLib(e).multiply(Signala ,Signalb);}

				int plainCompute(int x, int y) {
					return x*y;}
			});
		}
	}
}