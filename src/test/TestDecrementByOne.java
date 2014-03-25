package test;

import java.util.Random;

import flexsc.CompEnv;
import gc.Signal;

import org.junit.Test;

import circuits.IntegerLib;

public class TestDecrementByOne extends Test_1Input1Output {

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 1000;

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt(1 << 30)) {
				Signal[] secureCompute(Signal[] Signala, CompEnv<Signal> e)
						throws Exception {
					return new IntegerLib(e).decrementByOne(Signala);
				}

				int plainCompute(int x) {
					return x - 1;
				}
			});
		}
	}
}