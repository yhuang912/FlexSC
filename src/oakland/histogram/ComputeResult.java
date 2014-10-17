package oakland.histogram;

import flexsc.PMCompEnv.Statistics;

public class ComputeResult {
	static public void main(String args[]) throws InterruptedException {
		for (int logN = new Integer(args[0]); logN <= 20; logN+=new Integer(args[1])) {
			Statistics mapreduce = MapreduceHistogram.getCount(1 << logN);
			Statistics oram = ORAMHistogram.getCount(1 << logN);
			System.out.println(logN + "\t" + mapreduce.andGate + "\t"
					+ oram.andGate + "\t" + mapreduce.NumEncAlice + "\t"
					+ oram.NumEncAlice);
		}
	}
}