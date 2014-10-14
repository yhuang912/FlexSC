package testlibs;

import harness.TestSortHarness;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import circuits.IntegerLib;
import circuits.SortLib;
import flexsc.CompEnv;
import flexsc.Comparator;

public class TestSortLib extends TestSortHarness<Boolean> {

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

			Helper helper = new Helper(a) {
				public Boolean[][] secureCompute(Boolean[][] Signala,
						CompEnv<Boolean> e) throws Exception {
					IntegerLib<Boolean> ilib = new IntegerLib<Boolean>(e);

					SortLib<Boolean> lib = new SortLib<Boolean>(e, ilib);
					lib.sort(Signala, ilib.SIGNAL_ONE,
							new IntComparator<Boolean>(ilib));

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