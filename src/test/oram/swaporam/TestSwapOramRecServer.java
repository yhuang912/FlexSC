package test.oram.swaporam;

import test.oram.swaporam.TestSwapOramRec.GenRunnable;
import flexsc.Flag;

public class TestSwapOramRecServer {
	public static void main(String [ ] args) throws InterruptedException {
		TestSwapOramRec c = new TestSwapOramRec();
//		GenRunnable gen = c.new GenRunnable(new Integer(args[0]), new Integer(args[1]), 
//					new Integer(args[2]), new Integer(args[3]),
//						new Integer(args[4]), new Integer(args[5]) );
		GenRunnable gen = c.new GenRunnable(12345, 20, 6, 32,  4, 10);
		
		gen.run();
		Flag.sw.print();
}
}