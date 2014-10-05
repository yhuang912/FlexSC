package circuits;

import java.io.IOException;

import flexsc.CompEnv;

public class BitonicSortLib<T> extends IntegerLib<T> {
	T isAscending;
	Comparator<T> comparator;

	public BitonicSortLib(CompEnv<T> e, Comparator<T> comparator) {
		super(e);
		this.comparator = comparator;
	}

	public void sort(T[][] a, T[][] data, T isAscending) {
		this.isAscending = isAscending;
		bitonicSort(a, data, 0, a.length, isAscending);
	}

	private void bitonicSort(T[][] a, T[][] data, int start, int n, T dir) {
		if (n > 1) {
			int m = n / 2;
			bitonicSort(a, data, start, m, isAscending);
			bitonicSort(a, data, start + m, n - m, not(isAscending));
			bitonicMerge(a, data, start, n, dir);
		}
	}

	public void bitonicMerge(T[][] a, T[][] data, int start, int n, T dir) {
		if (n > 1) {
			int m = compareAndSwapFirst(a, data, start, n, dir);
			bitonicMerge(a, data, start, m, dir);
			bitonicMerge(a, data, start + m, n - m, dir);
		}
	}

	public int compareAndSwapFirst(T[][] a, T[][] data, int start, int n, T dir) {
		int m = n / 2;
		for (int i = start; i < start + m; i++) {
			compareAndSwap(a, data, i, i + m, dir);
		}
		/*try {
			env.os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		return m;
	}

	private void compareAndSwap(T[][] a, T[][] data, int i, int j, T dir) {
		T[] tempdi = null, tempdj = null;
		if (data != null) {
			tempdi = data[i];
			tempdj = data[j];
		}
    	T greater = not(comparator.leq(a[i], a[j], tempdi, tempdj));
    	T swap = eq(greater, dir);
    	T[] s = mux(a[j], a[i], swap);
    	s = xor(s, a[i]);
    	T[] ki = xor(a[j], s);
    	T[] kj = xor(a[i], s);
    	a[i] = ki;
    	a[j] = kj;

    	if (data != null) {
	    	T[] s2 = mux(data[j], data[i], swap);
	    	s2 = xor(s2, data[i]);
	    	T[] di = xor(data[j], s2);
	    	T[] dj = xor(data[i], s2);
	    	data[i] = di;
	    	data[j] = dj;
    	}
    }
}
