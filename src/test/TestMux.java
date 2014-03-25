package test;

import org.junit.Test;

import circuits.CircuitLib;
import circuits.IntegerLib;
import flexsc.CompEnv;
import gc.Signal;

public class TestMux extends Test_2Input1Output{

	@Test
	public void testAllCases() throws Exception {
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(0b1100, 0b1010) { // This particular pair of inputs exhausts 4 possible inputs, excluding selection signal 
				Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception {
					IntegerLib lib = new IntegerLib(e);
					return lib.mux(Signala ,Signalb, CircuitLib.SIGNAL_ONE);}

				int plainCompute(int x, int y) {
					return y;}
			});
		}
		
		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(0b1100, 0b1010) { // This particular pair of inputs exhausts 4 possible inputs, excluding selection signal
				Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception {
					IntegerLib lib = new IntegerLib(e);
					return lib.mux(Signala ,Signalb, CircuitLib.SIGNAL_ZERO);}

				int plainCompute(int x, int y) {
					return x;}
			});
		}
	}
}