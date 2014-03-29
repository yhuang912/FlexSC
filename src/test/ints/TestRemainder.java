package test.ints;

import java.util.Random;

import flexsc.CompEnv;
import gc.Signal;

import org.junit.Test;

import test.harness.Test_2Input1Output;
import test.harness.Test_2Input1Output.Helper;
import circuits.IntegerLib;



public class TestRemainder extends Test_2Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			int b = rnd.nextInt(1<<30);
			b = (b == 0) ? 1 : b;
			runThreads(new Helper(rnd.nextInt(1<<30), b){
				public Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception {
					return new IntegerLib(e).reminder(Signala ,Signalb);}

				public int plainCompute(int x, int y) {
					return x%y;}
			});
		}		
	}
}