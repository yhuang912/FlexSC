package test.parallel;

import flexsc.CompPool;
import flexsc.Flag;
import flexsc.Mode;
import gc.GCSignal;

public class Test_2Input1Output_gen extends Test_2Input1Output<GCSignal>{

	public static void main(String args[])throws Exception {
		CompPool.MaxNumberTask = new Integer(args[0]);

		Mode m = Mode.REAL;
		Test_2Input1Output_gen tt = new Test_2Input1Output_gen();


		for (int i = 1; i <= 10; i+=1) {
			int a[] = new int[10000000];
			Helper h = tt.new Helper(a, m);
			
			GenRunnable gen = tt.new GenRunnable(h);

			Thread tGen = new Thread(gen);
			tGen.start(); Thread.sleep(5);
			tGen.join();	

			
			Flag.sw.addCounter();
//			Flag.sw.flush();
		}
		Flag.sw.print();

		
	}	

}