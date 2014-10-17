package oakland.histogram;

import flexsc.PMCompEnv.Statistics;

public class ComputeResult {

	static public void main(String args[]) throws InterruptedException {
		for (int logN = 10; logN <= 20; ++logN) {
			Statistics mapreduce = MapreduceHistogram.getCount(1 << logN);
			Statistics oram = ORAMHistogram.getCount(1 << logN);
			System.out.println(logN + "\t" + mapreduce.andGate + "\t"
					+ oram.andGate + "\t" + mapreduce.NumEncAlice + "\t"
					+ oram.NumEncAlice);
		}
	}
}