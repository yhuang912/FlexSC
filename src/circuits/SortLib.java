// Copyright (C) 2014 by Xiao Shaun Wang <wangxiao@cs.umd.edu>
package circuits;

import flexsc.CompEnv;
import flexsc.Comparator;

public class SortLib<T> {
	IntegerLib<T> lib; 
	Comparator<T> c;
	public SortLib(CompEnv<T> e, IntegerLib<T> lib) {
		this.lib = lib;
	}

	public void sort(T[][] a, T dir, Comparator<T> c) {
		this.c = c;
		bitonicSort(a, 0, a.length, dir);
	}

	private void bitonicSort(T[][] key, int lo, int n, T dir) {
		if (n > 1) {
			int m = n / 2;
			bitonicSort(key, lo, m, lib.not(dir));
			bitonicSort(key, lo + m, n - m, dir);
			bitonicMerge(key, lo, n, dir);
		}
	}

	private void bitonicMerge(T[][] key, int lo, int n, T dir) {
		if (n > 1) {
			int m = greatestPowerOfTwoLessThan(n);
			for (int i = lo; i < lo + n - m; i++)
				compare(key, i, i + m, dir);
			bitonicMerge(key, lo, m,  dir);
			bitonicMerge(key, lo + m, n - m, dir);
		}
	}

	private void compare(T[][] key, int i, int j, T dir) {
		try {
			T greater = lib.not(c.compare(key[i], key[j]));
			T swap = lib.eq(greater, dir);
			T[] s = lib.mux(key[j], key[i], swap);
			s = lib.xor(s, key[i]);
			T[] ki = lib.xor(key[j], s);
			T[] kj = lib.xor(key[i], s);
			key[i] = ki;
			key[j] = kj;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int greatestPowerOfTwoLessThan(int n) {
		int k = 1;
		while (k < n)
			k = k << 1;
		return k >> 1;
	}
}