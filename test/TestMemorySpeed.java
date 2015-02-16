import gc.GCSignal;



public class TestMemorySpeed {

	public static void main(String[] args)throws Exception {
		
		int len = (int) (1024*1024*1.1);
		GCSignal[] a = new GCSignal[len];
		double t1 = System.nanoTime();
		for(int i = len/11; i < len; ++i) {
			a[i] = GCSignal.newInstance(new byte[10]);
		}
		System.out.println((System.nanoTime()-t1)/1000000000.0);
//		while(true){}
	}
}
