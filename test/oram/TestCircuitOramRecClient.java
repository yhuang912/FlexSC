package oram;

import oram.TestCircuitOramRecOpt.EvaRunnable;
import flexsc.Flag;

public class TestCircuitOramRecClient {

	public  static void main(String args[]) throws Exception {
		for(int i = 15; i <=23 ; i++) {
			Flag.sw.flush();
			EvaRunnable eva = new EvaRunnable(args[0], 54321);
			eva.run();
			Flag.sw.print();
			System.out.print("\n");
		}
	}
}