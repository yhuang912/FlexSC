package oakland.sqlquery;

import oakland.sqlquery.MapreduceSQL.GenRunnable;
import flexsc.Flag;

public class TestCircuitOramRecServer {

	public  static void main(String args[]) throws Exception {
		for(int i = 10; i <=14 ; i+=1) {
			Flag.sw.flush();
			GenRunnable gen = new GenRunnable(1<<i);
			gen.run();
			Flag.sw.addCounter();
			Flag.sw.print(i);
			System.out.print("\n");
			//asdasdasdas
		}
	}
}