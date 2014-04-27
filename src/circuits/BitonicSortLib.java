package circuits;

import flexsc.CompEnv;
import gc.GCSignal;

public class BitonicSortLib extends IntegerLib
{
    public BitonicSortLib(CompEnv<GCSignal> e) {
		super(e);
	}

    public void sortWithPayload(GCSignal[][] a, GCSignal[][] data, GCSignal isAscending) throws Exception {
        bitonicSortWithPayload(a, data, 0, a.length, isAscending);
    }

    private void bitonicSortWithPayload(GCSignal[][]key, GCSignal[][] data, int lo, int n, GCSignal dir) throws Exception {
        if (n > 1) {
            int m=n/2;
            bitonicSortWithPayload(key, data, lo, m, not(dir));
            bitonicSortWithPayload(key, data, lo+m, n-m, dir);
            bitonicMergeWithPayload(key, data, lo, n, dir);
        }
    }

    private void bitonicMergeWithPayload(GCSignal[][] key, GCSignal[][] data, int lo, int n, GCSignal dir) throws Exception {
        if (n > 1) {
            int m=greatestPowerOfTwoLessThan(n);
            for (int i = lo; i < lo + n - m; i++)
                compareWithPayload(key, data, i, i+m, dir);
            bitonicMergeWithPayload(key, data, lo, m, dir);
            bitonicMergeWithPayload(key, data, lo + m, n - m, dir);
        }
    }

    private void compareWithPayload(GCSignal[][] key, GCSignal[][] data, int i, int j, GCSignal dir) throws Exception {
    	GCSignal greater = not(leq(key[i], key[j]));
    	GCSignal swap = eq(greater, dir);
    	GCSignal[] ki = mux(key[i], key[j], swap);
    	GCSignal[] kj = mux(key[j], key[i], swap);
    	key[i] = ki;
    	key[j] = kj;
    	
    	GCSignal[] di = mux(data[i], data[j], swap);
    	GCSignal[] dj = mux(data[j], data[i], swap);
    	data[i] = di;
    	data[j] = dj;
    }
 
    
    public void sort(GCSignal[][] a, GCSignal isAscending) throws Exception {
        bitonicSort(a, 0, a.length, isAscending);
    }

    private void bitonicSort(GCSignal[][]key, int lo, int n, GCSignal dir) throws Exception {
        if (n > 1) {
            int m=n/2;
            bitonicSort(key, lo, m, not(dir));
            bitonicSort(key, lo+m, n-m, dir);
            bitonicMerge(key, lo, n, dir);
        }
    }

    private void bitonicMerge(GCSignal[][] key, int lo, int n, GCSignal dir) throws Exception {
        if (n > 1) {
            int m=greatestPowerOfTwoLessThan(n);
            for (int i = lo; i < lo + n - m; i++)
                compare(key, i, i+m, dir);
            bitonicMerge(key, lo, m, dir);
            bitonicMerge(key, lo + m, n - m, dir);
        }
    }

    private void compare(GCSignal[][] key, int i, int j, GCSignal dir) throws Exception {
    	GCSignal greater = not(leq(key[i], key[j]));
    	GCSignal swap = eq(greater, dir);
    	GCSignal[] ki = mux(key[i], key[j], swap);
    	GCSignal[] kj = mux(key[j], key[i], swap);
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