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

	public void sortWithPayload(T[][] a, T[][] data, T isAscending) {
		this.isAscending = isAscending;
		bitonicSortWithPayload(a, data, 0, a.length, isAscending);
	}

	private void bitonicSortWithPayload(T[][] a, T[][] data, int start, int n, T dir) {
		if (n > 1) {
			int m = n / 2;
			bitonicSortWithPayload(a, data, start, m, isAscending);
			bitonicSortWithPayload(a, data, start + m, n - m, not(isAscending));
			bitonicMergeWithPayload(a, data, start, n, dir);
		}
	}

	public void bitonicMergeWithPayload(T[][] a, T[][] data, int start, int n, T dir) {
		if (n > 1) {
			int m = compareAndSwapFirstWithPayload(a, data, start, n, dir);
			bitonicMergeWithPayload(a, data, start, m, dir);
			bitonicMergeWithPayload(a, data, start + m, n - m, dir);
		}
	}

	public int compareAndSwapFirstWithPayload(T[][] a, T[][] data, int start, int n, T dir) {
		int m = n / 2;
		for (int i = start; i < start + m; i++) {
			compareAndSwapWithPayload(a, data, i, i + m, dir);
		}
		return m;
	}

	private void compareAndSwapWithPayload(T[][] a, T[][] data, int i, int j, T dir) {
    	T greater = not(leq(a[i], a[j]));
    	T swap = eq(greater, dir);
    	T[] ki = mux(a[i], a[j], swap);
    	T[] kj = mux(a[j], a[i], swap);
    	a[i] = ki;
    	a[j] = kj;

    	T[] di = mux(data[i], data[j], swap);
    	T[] dj = mux(data[j], data[i], swap);
    	data[i] = di;
    	data[j] = dj;
    }
}
