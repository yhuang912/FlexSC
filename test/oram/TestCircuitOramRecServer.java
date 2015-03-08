package oram;

import oram.TestCircuitOramRecOpt.GenRunnable;
import flexsc.Flag;

public class TestCircuitOramRecServer {

	public  static void main(String args[]) throws Exception {
			Flag.sw.flush();
			GenRunnable gen = new GenRunnable(12345, new Integer(args[0]), 3, 1024-32, 8, 6);
			gen.run();
			Flag.sw.print();
			System.out.print("\n");
	}
}
