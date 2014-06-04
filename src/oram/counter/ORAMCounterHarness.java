package oram.counter;

import pm.PMCompEnv.Statistics;

public class ORAMCounterHarness {
	int N;
	int capacity;
	int dataSize;
	int securityParameter;
	public Statistics statistic;
	public ORAMCounterHarness(int logN, int capacity, int dataSize, int securityParameter) {
		this.N = 1<<logN;
		this.capacity = capacity;
		this.dataSize = dataSize;
		this.securityParameter = securityParameter;
		statistic = new Statistics();
	}
}