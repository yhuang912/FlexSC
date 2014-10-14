package testlibs;

import harness.TestSortHarness;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import circuits.BitonicSortLib;
import flexsc.CompEnv;

public class TestBitonicSortLib extends TestSortHarness<Boolean> {

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		for (int i = 0; i < 10; i++) {
			int[] a = new int[1000];
			for (int j = 0; j < a.length; ++j)
				a[j] = rnd.nextInt() % (1 << 30);

			Helper helper = new Helper(a) {
				public Boolean[][] secureCompute(Boolean[][] Signala,
						CompEnv<Boolean> e) throws Exception {
					BitonicSortLib<Boolean> lib = new BitonicSortLib<Boolean>(e);
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