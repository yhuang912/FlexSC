package oakland.sqlquery;

import flexsc.PMCompEnv.Statistics;

public class ComputeResult {

	static public void main(String args[]) throws InterruptedException {
		for (int logN = 10; logN <= 18; logN+=2) {
			Statistics mapreduce = MapreduceSQL.getCount(1 << logN);
			Statistics oram = ORAMSQL.getCount(1 << logN);
			System.out.println(logN + "\t" + mapreduce.andGate + "\t"
					+ oram.andGate + "\t" + mapreduce.NumEncAlice + "\t"
					+ oram.NumEncAlice);
		}
	}
}