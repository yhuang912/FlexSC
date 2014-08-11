package circuits;

import flexsc.CompEnv;

public class AnotherBitonicSortLib<T> extends IntegerLib<T> {
	T isAscending;

	public AnotherBitonicSortLib(CompEnv<T> e) {
		super(e);
	}

	public void sort(T[][] a, T isAscending) {
		this.isAscending = isAscending;
		bitonicSort(a, 0, a.length, isAscending);
	}

	private void bitonicSort(T[][] a, int start, int n, T dir) {
		if (n > 1) {
			int m = n / 2;
			bitonicSort(a, start, m, isAscending);
			bitonicSort(a, start + m, n - m, not(isAscending));
			bitonicMerge(a, start, n, dir);
		}
	}

	public void bitonicMerge(T[][] a, int start, int n, T dir) {
		if (n > 1) {
			int m = compareAndSwapFirst(a, start, n, dir);
			bitonicMerge(a, start, m, dir);
			bitonicMerge(a, start + m, n - m, dir);
		}
	}

	public int compareAndSwapFirst(T[][] a, int start, int n, T dir) {
		int m = n / 2;
		for (int i = start; i < start + m; i++) {
			compareAndSwap(a, i, i + m, dir);
		}
		return m;
	}

	private void compareAndSwap(T[][] a, int i, int j, T dir) {
    	T greater = not(leq(a[i], a[j]));
    	T swap = eq(greater, dir);
    	T[] ki = mux(a[i], a[j], swap);
    	T[] kj = mux(a[j], a[i], swap);
    	a[i] = ki;
    	a[j] = kj;
    }
}
