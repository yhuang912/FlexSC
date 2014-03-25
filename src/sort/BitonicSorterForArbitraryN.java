package sort;

public class BitonicSorterForArbitraryN implements Sorter
{
    private int[] a;
    private final static boolean ASCENDING=true;    // sorting direction

    public void sort(int[] a)
    {
        this.a=a;
        bitonicSort(0, a.length, ASCENDING);
    }

    private void bitonicSort(int lo, int n, boolean dir)
    {
        if (n>1)
        {
            int m=n/2;
            bitonicSort(lo, m, !dir);
            bitonicSort(lo+m, n-m, dir);
            bitonicMerge(lo, n, dir);
        }
    }

    private void bitonicMerge(int lo, int n, boolean dir)
    {
        if (n>1)
        {
            int m=greatestPowerOfTwoLessThan(n);
            for (int i=lo; i<lo+n-m; i++)
                compare(i, i+m, dir);
            bitonicMerge(lo, m, dir);
            bitonicMerge(lo+m, n-m, dir);
        }
    }

    private void compare(int i, int j, boolean dir)
    {
        if (dir==(a[i]>a[j]))
            exchange(i, j);
    }

    private void exchange(int i, int j)
    {
        int t=a[i];
        a[i]=a[j];
        a[j]=t;
    }

    private int greatestPowerOfTwoLessThan(int n)
    {
        int k=1;
        while (k<n)
            k=k<<1;
        return k>>1;
    }

}