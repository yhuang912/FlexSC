package test.others;

import java.util.Random;

public class RandomizeShellSort {
	public static final int C=1; // number of region compare-exchange repetitions
	static public int c = 0;
	public static void exchange(int[]a,  int i, int j){
		++c;
		int temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}

	public static void compareExchange(int[] a, int i, int j) {
		if (((i < j) && (a[i] > a[j])) || ((i > j) && (a[i] < a[j])))
			exchange(a, i, j);
	}

	public static void permuteRandom(int a[], Random rand) {
		for (int i=0; i<a.length; i++) 
			exchange(a, i, rand.nextInt(a.length-i)+i);
		// Use the Knuth random perm. algorithm
	}
	// compare-exchange two regions of length offset each
	public static void compareRegions(int[] a, int s, int t, int offset, Random rand) {
		int mate[] = new int[offset]; // index offset array
		for (int count=0; count<C; count++) { // do C region compare-exchanges
			for (int i=0; i<offset; i++) 
				mate[i] = i;
			permuteRandom(mate,rand); // comment this out to get a deterministic Shellsort 
			for (int i=0; i<offset; i++)
				compareExchange(a, s+i, t+mate[i]);
		} 
	}
	public static void randomizedShellSort(int[] a) {
		int n = a.length; // we assume that n is a power of 2
		Random rand = new Random(); // random number generator (not shown)
		for (int offset = n/2; offset > 0; offset /= 2) {
			for (int i=0; i < n - offset; i += offset) // compare-exchange up 
				compareRegions(a,i,i+offset,offset,rand);
			for (int i=n-offset; i >= offset; i -= offset) // compare-exchange down 
				compareRegions(a,i-offset,i,offset,rand);
			for (int i=0; i < n-3*offset; i += offset) // compare 3 hops up 
				compareRegions(a,i,i+3*offset,offset,rand);
			for (int i=0; i < n-2*offset; i += offset) // compare 2 hops up 
				compareRegions(a,i,i+2*offset,offset,rand);
			for (int i=0; i < n; i += 2*offset) // compare odd-even regions
				compareRegions(a,i,i+offset,offset,rand);
			for (int i=offset; i < n-offset; i += 2*offset) // compare even-odd regions 
				compareRegions(a,i,i+offset,offset,rand);
		}
	}
	
	public static void main(String[] args)
	{
		Random r = new Random();
		for(int logN = 2; logN <= 20; ++logN){
//	int logN = 8;	
		int[] a = new int[1<<logN];
		for(int i = 0; i < a.length; ++i)
			a[i] = r.nextInt();
		randomizedShellSort(a);
		for(int i = 0; i < a.length-1; ++i)
			if(a[i] > a[i+1]){
				System.out.println("fail");
				break;
			}
//		System.out.print("OK\n"+c+" "+((logN*logN+logN)*(1<<logN)/4)  );
		System.out.println(logN+"\t"+c+"\t"+((logN*logN+logN)*(1<<logN)/4) );
		}
	}
}