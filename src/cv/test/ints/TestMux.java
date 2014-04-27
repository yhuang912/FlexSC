package cv.test.ints;

import org.junit.Test;

import cv.test.harness.Test_2Input1Output;
import circuits.CircuitLib;
import cv.CVCompEnv;

public class TestMux extends Test_2Input1Output{

	@Test
	public void testAllCases() throws Exception {
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			runTest(new Helper(0b1100, 0b1010) { // This particular pair of inputs exhausts 4 possible inputs, excluding selection signal 
				public Boolean[] secureCompute(Boolean[] a, Boolean[] b, CVCompEnv e) throws Exception {
					CircuitLib<Boolean> lib = new CircuitLib<Boolean>(e);
					return lib.mux(a, b, lib.SIGNAL_ONE);}

				public int plainCompute(int x, int y) {
					return y;}
			});
		}
		
		for (int i = 0; i < testCases; i++) {
			runTest(new Helper(0b1100, 0b1010) { // This particular pair of inputs exhausts 4 possible inputs, excluding selection signal
				public Boolean[] secureCompute(Boolean[] a, Boolean[] b, CVCompEnv e) throws Exception {
					CircuitLib<Boolean> lib = new CircuitLib<Boolean>(e);
					return lib.mux(a, b, lib.SIGNAL_ZERO);}

				public int plainCompute(int x, int y) {
					return x;}
			});
		}
	}
}