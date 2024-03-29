package oram.counter;

import oram.clporam.CLPOramBasicClient;
import oram.clporam.CLPOramBasicServer;
import pm.PMCompEnv;
import flexsc.*;
import test.Utils;

public class CountKaiminORAM extends ORAMCounterHarness{
	public CountKaiminORAM(int logN, int capacity, int dataSize, int securityParameter) {
		super(logN, capacity, dataSize, securityParameter);
	}
	
	class GenRunnable extends network.Server implements Runnable {
		GenRunnable () {
		}
		public void run() {
			try {
				listen(54321);
				CLPOramBasicClient<Boolean> client = new CLPOramBasicClient<Boolean>(is, os, N, dataSize, Party.Alice, capacity, capacity, Mode.COUNT);
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
				CLPOramBasicServer<Boolean> server = new CLPOramBasicServer<Boolean>(is, os, N, dataSize, Party.Bob, capacity, capacity, Mode.COUNT);
				PMCompEnv mce = (PMCompEnv)server.eva;
				mce.statistic.flush();
				server.access(1);
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
		CountKaiminORAM c = new CountKaiminORAM(10, 6, 16, 80);
		c.count();
		System.out.println(c.statistic.andGate);
		System.out.println(c.statistic.OTs);
	}
}