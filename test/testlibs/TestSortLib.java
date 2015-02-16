package testlibs;

import flexsc.CompEnv;
import flexsc.Comparator;
import harness.TestHarness;
import harness.TestSortHarness;
import harness.TestSortHarness.Helper;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import circuits.SortLib;
import circuits.arithmetic.IntegerLib;

public class TestSortLib extends TestHarness {

	class IntComparator<T> implements Comparator<T> {
		IntegerLib<T> lib;

		public IntComparator(IntegerLib<T> lib) {
			this.lib = lib;
		}

		@Override
		public T compare(T[] a, T[] b) throws Exception {
			return lib.leq(a, b);
		}
	}

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();

		for (int i = 0; i < 10; i++) {
			int[] a = new int[1000];
			for (int j = 0; j < a.length; ++j)
				a[j] = rnd.nextInt() % (1 << 30);

			TestSortHarness.runThreads(new Helper(a) {
				public <T>T[][] secureCompute(T[][] Signala,
						CompEnv<T> e) throws Exception {
					IntegerLib<T> ilib = new IntegerLib<T>(e);

					SortLib<T> lib = new SortLib<T>(e, ilib);
					lib.sort(Signala, ilib.SIGNAL_ONE,
							new IntComparator<T>(ilib));

					return Signala;
				}

				@Override
				public int[] plainCompute(int[] intA) {
					Arrays.sort(intA);
					return intA;
				}
			});
		}
	}

}