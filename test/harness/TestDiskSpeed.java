package harness;

import gc.GCSignal;

public class TestDiskSpeed {
	
	public static void main(String args[]) throws Exception {
		double t1 = System.nanoTime();
		for(int i = 0; i < 10000000; ++i) {
//			GCSignal.receive(gc.offline.GCGen.fin);
		}
		System.out.println(   10000000*10 / ((System.nanoTime()-t1)/1000000000.0)/1024/1024  );
	}
}
