package circuits;

import flexsc.CompEnv;
import gc.Signal;

public class BitonicSortLib extends IntegerLib
{
    public BitonicSortLib(CompEnv<Signal> e) {
		super(e);
	}

    public void sortWithPayload(Signal[][] a, Signal[][] data, Signal isAscending) throws Exception {
        bitonicSortWithPayload(a, data, 0, a.length, isAscending);
    }

    private void bitonicSortWithPayload(Signal[][]key, Signal[][] data, int lo, int n, Signal dir) throws Exception {
        if (n > 1) {
            int m=n/2;
            bitonicSortWithPayload(key, data, lo, m, not(dir));
            bitonicSortWithPayload(key, data, lo+m, n-m, dir);
            bitonicMergeWithPayload(key, data, lo, n, dir);
        }
    }

    private void bitonicMergeWithPayload(Signal[][] key, Signal[][] data, int lo, int n, Signal dir) throws Exception {
        if (n > 1) {
            int m=greatestPowerOfTwoLessThan(n);
            for (int i = lo; i < lo + n - m; i++)
                compareWithPayload(key, data, i, i+m, dir);
            bitonicMergeWithPayload(key, data, lo, m, dir);
            bitonicMergeWithPayload(key, data, lo + m, n - m, dir);
        }
    }

    private void compareWithPayload(Signal[][] key, Signal[][] data, int i, int j, Signal dir) throws Exception {
    	Signal greater = not(leq(key[i], key[j]));
    	Signal swap = eq(greater, dir);
    	Signal[] ki = mux(key[i], key[j], swap);
    	Signal[] kj = mux(key[j], key[i], swap);
    	key[i] = ki;
    	key[j] = kj;
    	
    	Signal[] di = mux(data[i], data[j], swap);
    	Signal[] dj = mux(data[j], data[i], swap);
    	data[i] = di;
    	data[j] = dj;
    }
 
    
    public void sort(Signal[][] a, Signal isAscending) throws Exception {
        bitonicSort(a, 0, a.length, isAscending);
    }

    private void bitonicSort(Signal[][]key, int lo, int n, Signal dir) throws Exception {
        if (n > 1) {
            int m=n/2;
            bitonicSort(key, lo, m, not(dir));
            bitonicSort(key, lo+m, n-m, dir);
            bitonicMerge(key, lo, n, dir);
        }
    }

    private void bitonicMerge(Signal[][] key, int lo, int n, Signal dir) throws Exception {
        if (n > 1) {
            int m=greatestPowerOfTwoLessThan(n);
            for (int i = lo; i < lo + n - m; i++)
                compare(key, i, i+m, dir);
            bitonicMerge(key, lo, m, dir);
            bitonicMerge(key, lo + m, n - m, dir);
        }
    }

    private void compare(Signal[][] key, int i, int j, Signal dir) throws Exception {
    	Signal greater = not(leq(key[i], key[j]));
    	Signal swap = eq(greater, dir);
    	Signal[] ki = mux(key[i], key[j], swap);
    	Signal[] kj = mux(key[j], key[i], swap);
    	key[i] = ki;
    	key[j] = kj;
    }
    
    private int greatestPowerOfTwoLessThan(int n) {
        int k=1;
        while (k<n)
            k=k<<1;
        return k>>1;
    }
}