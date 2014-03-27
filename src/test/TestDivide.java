package test;

import java.util.Random;

import flexsc.CompEnv;
import gc.Signal;

import org.junit.Test;

import circuits.IntegerLib;


public class TestDivide extends Test_2Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			int b = rnd.nextInt(1<<30);
			b = (b == 0) ? 1 : b;
			runThreads(new Helper(rnd.nextInt(1<<30), b){
				Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception {
					return new IntegerLib(e).divide(Signala ,Signalb);}

				int plainCompute(int x, int y) {
					return x/y;}
			});
		}		
	}
}