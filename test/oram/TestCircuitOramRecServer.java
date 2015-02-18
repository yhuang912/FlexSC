package oram;

import oram.TestCircuitOramRecOpt.GenRunnable;
import flexsc.Flag;

public class TestCircuitOramRecServer {

	public  static void main(String args[]) throws Exception {

		for(int i = 10; i <=26 ; i+=2) {
			Flag.sw.flush();
			GenRunnable gen = new GenRunnable(54321, i, 3, 1024-32, 8, 6);
			gen.run();
			Flag.sw.print();
			System.out.print("\n");
		}
	}
}
