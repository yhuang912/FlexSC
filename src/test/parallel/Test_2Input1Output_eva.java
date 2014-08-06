package test.parallel;

import flexsc.Flag;
import flexsc.Mode;
import gc.GCSignal;
import network.Master;

public class Test_2Input1Output_eva extends Test_2Input1Output<GCSignal>{
	static int ARRAY_LENGTH = 100000;
	static Mode MODE = Mode.VERIFY;

	public static void main(String args[]) throws Exception {
		Test_2Input1Output.MASTER_EVA_PORT = new Integer(args[0]);
		Mode m = MODE;
		Test_2Input1Output_eva tt = new Test_2Input1Output_eva();


		int a[] = new int[ARRAY_LENGTH];
		for(int k = 0; k < Master.MACHINES; ++k){
			for(int i = 0; i < a.length/Master.MACHINES; ++i)
			{
				if (i == 0)
					a[k*a.length/Master.MACHINES + i] = ((Master.MACHINES - k) - 1);
				else 
					a[k*a.length/Master.MACHINES + i] = 0;
			}
		}
		Helper h = tt.new Helper(a, m);
		
		EvaRunnable eva = tt.new EvaRunnable(h);

		Thread tEva = new Thread(eva);
		tEva.start();
		tEva.join();	

		Flag.sw.addCounter();
			
		Flag.sw.print();
		
	}	

}
