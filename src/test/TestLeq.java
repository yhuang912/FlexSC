package test;

import java.util.Random;

import flexsc.CompEnv;
import gc.Signal;

import org.junit.Test;

import circuits.IntegerLib;


public class TestLeq extends Test_2Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt(1<<30), rnd.nextInt(1<<30)) {
				Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception {
					return new Signal[]{new IntegerLib(e).leq(Signala ,Signalb)}; }

				int plainCompute(int x, int y) {
					return (x<=y)?1:0; }
			});
		}
		
		for (int i = 0; i < testCases; i++) {
			int a = rnd.nextInt(1<<30);
			runThreads(new Helper(a, a) {
				Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception {
					return new Signal[]{new IntegerLib(e).leq(Signala ,Signalb)}; }

				int plainCompute(int x, int y) {
					return (x<=y)?1:0; }
			});
		}
	}
}