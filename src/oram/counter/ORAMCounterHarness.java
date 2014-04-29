package oram.counter;

import cv.MeasureCompEnv.Statistic;

public class ORAMCounterHarness {
	int N;
	int capacity;
	int dataSize;
	int securityParameter;
	Statistic statistic;
	public ORAMCounterHarness(int logN, int capacity, int dataSize, int securityParameter) {
		this.N = 1<<logN;
		this.capacity = capacity;
		this.dataSize = dataSize;
		this.securityParameter = securityParameter;
		statistic = new Statistic();
	}
}