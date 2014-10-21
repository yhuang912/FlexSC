package test.parallel;

import flexsc.PMCompEnv.Statistics;

public class ComputeHistogramResult {
	static public void main(String args[]) throws InterruptedException {
//		for (int logN = new Integer(args[0]); logN <= 20; logN+=new Integer(args[1])) {
			Statistics oram = HistogramOram.getCount(256);
//			System.out.println(logN + "\t" + oram.andGate + "\t" + oram.NumEncAlice);
//		}
	}
}