package oram.counter;

import oram.pathoramNaive.PathOramClient;
import oram.pathoramNaive.PathOramServer;
import cv.MeasureCompEnv;
import flexsc.*;
import test.Utils;

public class CountPathORAMNaive extends ORAMCounterHarness{
	public CountPathORAMNaive(int logN, int capacity, int dataSize, int securityParameter) {
		super(logN, capacity, dataSize, securityParameter);
	}
	
	class GenRunnable extends network.Server implements Runnable {
		GenRunnable () {
		}
		public void run() {
			try {
				listen(54321);
				PathOramClient<Boolean> client = new PathOramClient<Boolean>(is, os, N, dataSize, Party.Alice, Mode.COUNT);
				client.write(1, 1, 1, Utils.fromInt(1, client.lengthOfData));
				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		EvaRunnable () {
		}
		public void run() {
			try {
				connect("localhost", 54321);				
				PathOramServer<Boolean> server = new PathOramServer<Boolean>(is, os, N, dataSize, Party.Bob, Mode.COUNT);
				MeasureCompEnv mce = (MeasureCompEnv)server.eva;
				mce.statistic.flush();
				server.write(1);
				statistic = mce.statistic;
				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	public void count() throws Exception {
		GenRunnable gen = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(10);
		tEva.start();
		tGen.join();
	}

	public static void main(String [ ] args) throws Exception{
		CountPathORAMNaive c = new CountPathORAMNaive(10, 5, 10, 80);
		c.count();
		System.out.print(c.statistic.andGate);
	}
}