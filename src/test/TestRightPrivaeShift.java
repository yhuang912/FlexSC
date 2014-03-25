package test;

import java.util.Random;

import gc.CompEnv;
import gc.IntegerLib;
import gc.Signal;
import org.junit.Test;


public class TestRightPrivaeShift extends Test_2Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 1000;

		for (int i = 0; i < testCases; i++) {
			int shift = rnd.nextInt(1<<5);
			test1Case(new Helper(rnd.nextInt(1<<30), shift) {
				Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception {
					return new IntegerLib(e).rightPrivateShift(Signala ,Signalb);}

				int plainCompute(int x, int y) {
					return x>>y;}
			});
		}		
	}
}