package test;

import java.util.Random;

import gc.CompEnv;
import gc.IntegerLib;
import gc.Signal;
import org.junit.Test;


public class TestAbsolute extends Test_1Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			test1Case(
				new Helper(rnd.nextInt(1<<30)) {
					Signal[] secureCompute(Signal[] Signala, CompEnv<Signal> e) throws Exception {
						return new IntegerLib(e).absolute(Signala);
					}

					int plainCompute(int x) {
						return (int)(Math.abs(x));
					}
				});
		}		
	}
}