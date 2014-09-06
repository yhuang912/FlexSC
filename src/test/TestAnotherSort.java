package test;

import flexsc.CompEnv;
import flexsc.Mode;
import gc.GCSignal;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import test.harness.TestSortHarness;
import circuits.AnotherBitonicSortLib;
import circuits.BitonicSortLib;

public class TestAnotherSort extends TestSortHarness<GCSignal>{
	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 1;

		for (int i = 0; i < testCases; i++) {
			int [] a = new int[512];
			for(int j = 0; j < a.length; ++j)
				a[j] = rnd.nextInt()%(1<<30);
			
			Helper helper = new Helper(a, Mode.REAL) {
				public GCSignal[][] secureCompute(GCSignal[][] Signala, CompEnv<GCSignal> e) throws Exception {
					AnotherBitonicSortLib<GCSignal> lib =  new AnotherBitonicSortLib<GCSignal>(e);
					lib.sort(Signala, lib.SIGNAL_ONE);
					return Signala;
				}
				
				@Override
				public int[] plainCompute(int[] intA) {
					Arrays.sort(intA);
					return intA;
				}
			};
			runThreads(helper);
		}		
	}
}