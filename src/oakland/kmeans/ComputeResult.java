package oakland.kmeans;

import flexsc.PMCompEnv.Statistics;

public class ComputeResult {

	static public void main(String args[]) throws InterruptedException {
		for (int logN = 5; logN <= 20; ++logN) {
			Statistics mapreduce = MapreduceKMeans.getCount(1 << logN);
			// Statistics oram = ORAMKMeans.getCount(1<<logN);
			// System.out.println(logN+"\t"+mapreduce.andGate+"\t"+oram.andGate+"\t"+mapreduce.NumEncAlice+"\t"+oram.NumEncAlice);
			System.out.println(logN + "\t" + mapreduce.andGate + "\t" + "\t"
					+ mapreduce.NumEncAlice + "\t");
		}

	}
}
