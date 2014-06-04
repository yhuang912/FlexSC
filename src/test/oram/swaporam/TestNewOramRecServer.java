package test.oram.swaporam;

import flexsc.Flag;
import test.oram.cscoram.TestNewOramRec.GenRunnable;

public class TestNewOramRecServer {
	public static void main(String [ ] args) throws InterruptedException {
		TestNewOramRec c = new TestNewOramRec();
//		GenRunnable gen = c.new GenRunnable(new Integer(args[0]), new Integer(args[1]), 
//					new Integer(args[2]), new Integer(args[3]),
//						new Integer(args[4]), new Integer(args[5]) );
		GenRunnable gen = c.new GenRunnable(12345, 5, 6, 32,  8, 5);
		
		gen.run();
//		System.out.println("bandwidth "+Flag.bandwidth);
		System.out.println(Flag.GarbleTime+" "+Flag.GargleIOTime+" "+Flag.OTIOTime+" "+Flag.OTTotalTime+" "+ Flag.TotalTime);
	}
}