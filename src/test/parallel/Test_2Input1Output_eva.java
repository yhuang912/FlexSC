package test.parallel;

import network.Master;
import flexsc.Flag;
import flexsc.Mode;
import gc.GCSignal;

public class Test_2Input1Output_eva extends Test_2Input1Output<GCSignal>{
	static int ARRAY_LENGTH = 100000;
	static Mode MODE = Mode.REAL;

	public static void main(String args[]) throws Exception {
		// CompPool.MaxNumberTask = new Integer(args[0]);
		Mode m = MODE;
		Test_2Input1Output_eva tt = new Test_2Input1Output_eva();


		//for (int i = 1; i <= 10; i+=1) {
			int a[] = new int[ARRAY_LENGTH];
			/*for (int i = 0; i < a.length; i++) {
			a[i] = a.length/4 + 1;
			} */
			for(int k = 0; k < Master.MACHINES; ++k){
				for(int i = 0; i < a.length/Master.MACHINES; ++i)
				{
					if (i == 0)
						// a[k*a.length/Master.MACHINES + i] = 1 << ((Master.MACHINES - k) - 1);
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
			
//			Flag.sw.flush();
		//}
		Flag.sw.print();
		
	}	

}
