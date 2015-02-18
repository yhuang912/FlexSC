package oram;

import oram.TestCircuitOramRecOpt.EvaRunnable;
import flexsc.Flag;

public class TestCircuitOramRecClient {

	public  static void main(String args[]) throws Exception {
//		for(int i = 1; i <=26 ; i++) {
			Flag.sw.flush();
			EvaRunnable eva = new EvaRunnable(args[0], 54321);
//			EvaRunnable eva = new EvaRunnable("localhost", 54321);
			eva.run();
			Flag.sw.print();
			System.out.print("\n");
//		}
	}
}
