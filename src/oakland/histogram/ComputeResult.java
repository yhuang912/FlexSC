package oakland.histogram;

import flexsc.PMCompEnv.Statistics;

public class ComputeResult {
	static public void main(String args[]) throws InterruptedException {
		for (int logN = 4; logN <= 20; logN+=2) {
			Statistics mapreduce = MapreduceHistogram.getCount(1 << logN);
			Statistics oram = ORAMHistogram.getCount(1 << logN);
			System.out.println(logN + "\t" + mapreduce.andGate + "\t"
					+ oram.andGate + "\t" + mapreduce.NumEncAlice + "\t"
					+ oram.NumEncAlice + " "+(oram.andGate/(double)mapreduce.andGate) );
		}
	}
}