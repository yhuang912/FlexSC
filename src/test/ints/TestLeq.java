package test.ints;

import java.util.Random;

import flexsc.CompEnv;
import gc.Signal;

import org.junit.Test;

import test.Utils;
import test.harness.Test_2Input1Output;
import test.harness.Test_2Input1Output.Helper;
import circuits.IntegerLib;


public class TestLeq extends Test_2Input1Output{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			runThreads(new Helper(rnd.nextInt()%(1<<30), rnd.nextInt()%(1<<30)) {
				public Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception {
					return new Signal[]{new IntegerLib(e).leq(Signala ,Signalb)}; }

				public int plainCompute(int x, int y) {
					return (int) Utils.toSignedInt(new boolean[]{(x<=y)});}
			});
		}
		
		for (int i = 0; i < testCases; i++) {
			int a = rnd.nextInt(1<<30);
			runThreads(new Helper(a, a) {
				public Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception {
					return new Signal[]{new IntegerLib(e).leq(Signala ,Signalb)}; }

				public int plainCompute(int x, int y) {
					return (int) Utils.toSignedInt(new boolean[]{(x<=y)});}
			});
		}
	}
}