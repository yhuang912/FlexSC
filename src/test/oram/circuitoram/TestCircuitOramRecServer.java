package test.oram.circuitoram;

import test.oram.circuitoram.TestCircuitOramRec.GenRunnable;
import flexsc.Flag;


public class TestCircuitOramRecServer {
	public static void main(String [ ] args) throws InterruptedException {
		TestCircuitOramRec c = new TestCircuitOramRec();
//		GenRunnable gen = c.new GenRunnable(new Integer(args[0]), new Integer(args[1]), 
//					new Integer(args[2]), new Integer(args[3]),
//						new Integer(args[4]), new Integer(args[5]) );
		GenRunnable gen = c.new GenRunnable(12345, 20, 6, 32,  4, 10);
		
		gen.run();
		Flag.sw.print();
}
}