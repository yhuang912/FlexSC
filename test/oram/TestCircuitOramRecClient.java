package oram;

import oram.TestCircuitOramRecOpt.EvaRunnable;
import flexsc.Flag;

public class TestCircuitOramRecClient {

	public  static void main(String args[]) throws Exception {
			Flag.sw.flush();
			EvaRunnable eva = new EvaRunnable("localhost", 12345);
			eva.run();
			Flag.sw.print();
			System.out.print("\n");
	}
}