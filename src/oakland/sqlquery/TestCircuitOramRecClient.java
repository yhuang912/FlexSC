package oakland.sqlquery;

import oakland.sqlquery.MapreduceSQL.EvaRunnable;
import flexsc.Flag;

public class TestCircuitOramRecClient {

	public  static void main(String args[]) throws Exception {
		for(int i = 10; i <=14 ; i+=1) {
			Flag.sw.flush();
			EvaRunnable eva = new EvaRunnable(1<<i);
			eva.run();
			Flag.sw.addCounter();
			Flag.sw.print(i);
			System.out.print("\n");
		}
	}
}