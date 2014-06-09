package test.oram.swaporam;

import test.oram.swaporam.TestSwapOramRec.EvaRunnable;
import flexsc.Flag;


public class TestSwapOramRecClient {
	
	public static void main(String [ ] args) {
		TestSwapOramRec s = new TestSwapOramRec();
//		EvaRunnable eva = s.new EvaRunnable(args[0], new Integer(args[1]));

		EvaRunnable eva = s.new EvaRunnable("localhost", 12345);
		
		eva.run();
		Flag.sw.print();
	}
}