package test.parallel;

import flexsc.Flag;
import flexsc.Mode;
import gc.GCSignal;

public class Test_2Input1Output_gen extends Test_2Input1Output<GCSignal>{

	public static void main(String args[])throws Exception {
		Test_2Input1Output.MASTER_GEN_PORT = new Integer(args[0]);

		Mode m = Test_2Input1Output_eva.MODE;
		Test_2Input1Output_gen tt = new Test_2Input1Output_gen();

		int a[] = new int[Test_2Input1Output_eva.ARRAY_LENGTH];
		Helper h = tt.new Helper(a, m);
		
		GenRunnable gen = tt.new GenRunnable(h);

		Thread tGen = new Thread(gen);
		tGen.start(); Thread.sleep(5);
		tGen.join();
		
		Flag.sw.addCounter();
		Flag.sw.print();
	}	
}