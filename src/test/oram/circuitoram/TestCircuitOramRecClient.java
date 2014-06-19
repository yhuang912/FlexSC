package test.oram.circuitoram;

import test.oram.circuitoram.TestCircuitOramRec.EvaRunnable;
import flexsc.Flag;


public class TestCircuitOramRecClient {
	
	public static void main(String [ ] args) {
		TestCircuitOramRec s = new TestCircuitOramRec();
//		EvaRunnable eva = s.new EvaRunnable(args[0], new Integer(args[1]));

		EvaRunnable eva = s.new EvaRunnable("localhost", 12345);
		
		eva.run();
		Flag.sw.print();
	}
}