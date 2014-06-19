package test.oram.cscoram;

import test.oram.cscoram.TestNewOramRec.EvaRunnable;
import flexsc.Flag;

public class TestNewOramRecClient {
	
	public static void main(String [ ] args) {
		TestNewOramRec s = new TestNewOramRec();
//		EvaRunnable eva = s.new EvaRunnable(args[0], new Integer(args[1]));

		EvaRunnable eva = s.new EvaRunnable("localhost", 12345);
		
		eva.run();
		Flag.sw.print();
	}
}