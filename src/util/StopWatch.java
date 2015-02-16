// Copyright (C) 2014 by Xiao Shaun Wang <wangxiao@cs.umd.edu>
package util;

public class StopWatch {
	public long ands = 0;
	public static int TOTAL = 0;
	public static int GC = 1;
	public static int OTIO = 2;
	public static int GCIO = 3;
	public static int OT = 4;
	
	double startTimeOT = 0;
	double stopTimeOT = 0;
	public double elapsedTimeOT = 0;

	double startTimeGC = 0;
	double stopTimeGC = 0;
	public double elapsedTimeGC = 0;

	double startTimeTotal = 0;
	double stopTimeTotal = 0;
	public double elapsedTimeTotal = 0;

	double startTimeOTIO = 0;
	double stopTimeOTIO = 0;
	public double elapsedTimeOTIO = 0;

	double startTimeGCIO = 0;
	double stopTimeGCIO = 0;
	public double elapsedTimeGCIO = 0;
	boolean countTime;
	long counter = 0;

	public StopWatch(boolean countTime) {
		this.countTime = countTime;
	}

	public void startOT() {
		if (countTime)
			startTimeOT = System.nanoTime();
	}

	public void stopOT() {
		if (countTime) {
			stopTimeOT = System.nanoTime();
			elapsedTimeOT += stopTimeOT - startTimeOT;
		}
	}
	
	double[] start = new double[10];
	double[] stop = new double[10];
	double[] elapsed = new double[10];
	int[] cnt = new int[10];
	public void start(int i) {
		if (countTime) {
			start[i] = System.nanoTime();
			cnt[i]++;
		}
	}

	public void stop(int i) {
		if (countTime) {
			stop[i] = System.nanoTime();
			elapsed[i] += stop[i] - start[i];
			cnt[i]--;
		}
	}

	public void startOTIO() {
		if (countTime)
			startTimeOTIO = System.nanoTime();
	}

	public void stopOTIO() {
		if (countTime) {
			stopTimeOTIO = System.nanoTime();
			elapsedTimeOTIO += stopTimeOTIO - startTimeOTIO;
		}
	}

	public void startGC() {
		if (countTime)
			startTimeGC = System.nanoTime();
	}

	public void stopGC() {
		if (countTime) {
			stopTimeGC = System.nanoTime();
			elapsedTimeGC += stopTimeGC - startTimeGC;
		}
	}

	public void startGCIO() {
		if (countTime)
			startTimeGCIO = System.nanoTime();
	}

	public void stopGCIO() {
		if (countTime) {
			stopTimeGCIO = System.nanoTime();
			elapsedTimeGCIO += stopTimeGCIO - startTimeGCIO;
		}
	}

	public void startTotal() {
		startTimeTotal = System.nanoTime();
	}

	public double stopTotal() {
		stopTimeTotal = System.nanoTime();
		elapsedTimeTotal += stopTimeTotal - startTimeTotal;
		return stopTimeTotal - startTimeTotal;
	}

	public void addCounter() {
		++counter;
	}

	public void flush() {
		ands = 0;
		startTimeOT = 0;
		stopTimeOT = 0;
		elapsedTimeOT = 0;

		startTimeGC = 0;
		stopTimeGC = 0;
		elapsedTimeGC = 0;

		startTimeTotal = 0;
		stopTimeTotal = 0;
		elapsedTimeTotal = 0;

		startTimeOTIO = 0;
		stopTimeOTIO = 0;
		elapsedTimeOTIO = 0;

		startTimeGCIO = 0;
		stopTimeGCIO = 0;
		elapsedTimeGCIO = 0;
		counter = 0;

	}

	public void print() {
		if(counter == 0)++counter;
		System.out
				.println("Total Time \t GC CPU Time\t GCIO Time\t OTCPU Time\t OTIO Time\n");
		System.out.println(elapsedTimeTotal / 1000000000.0 / counter + "\t"
				+ (elapsedTimeGC - elapsedTimeGCIO) / 1000000000.0 / counter
				+ "\t" + elapsedTimeGCIO / 1000000000.0 / counter + " "
				+ (elapsedTimeOT - elapsedTimeOTIO) / 1000000000.0 / counter
				+ "\t" + elapsedTimeOTIO / 1000000000.0 / counter + "\n");
		
		for(int i = 5; i < 10; ++i) {
			if(cnt[i] != 0) {
				System.out.println(i+"-th counter is not correct");
			} else {
				System.out.println(i+"th counter:" + (elapsed[i]) / 1000000000.0 / counter);
			}
		}
	}

	public void print(int i) {
		System.out.println("timer:\t"+i+" \t"+elapsedTimeTotal / 1000000000.0 / counter + "\t"
		+ (elapsedTimeGC - elapsedTimeGCIO) / 1000000000.0 / counter
		+ "\t" + elapsedTimeGCIO / 1000000000.0 / counter + " "
		+ (elapsedTimeOT - elapsedTimeOTIO) / 1000000000.0 / counter
		+ "\t" + elapsedTimeOTIO / 1000000000.0 / counter + "\n");
	}
}
