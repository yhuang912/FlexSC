package oram;

import oram.TestCircuitOramRecOpt.EvaRunnable;
import flexsc.Flag;

public class TestCircuitOramRecClient {

	public  static void main(String args[]) throws Exception {
		for(int i = 26; i <=26 ; i+=2) {
			Flag.sw.flush();
			EvaRunnable eva = new EvaRunnable("localhost", 54321);
			eva.run();
			Flag.sw.print();
			System.out.print("\n");
		}
	}
}