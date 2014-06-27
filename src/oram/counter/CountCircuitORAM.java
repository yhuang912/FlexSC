package oram.counter;

import java.util.Arrays;

import oram.circuitoram.CircuitOramClient;
import oram.circuitoram.CircuitOramServer;
import pm.PMCompEnv;
import test.Utils;
import flexsc.Mode;
import flexsc.Party;

public class CountCircuitORAM extends ORAMCounterHarness{

	public CountCircuitORAM(int logN, int capacity, int dataSize, int securityParameter) {
		super(logN, capacity, dataSize, securityParameter);
	}
	
	class GenRunnable extends network.Server implements Runnable {
		GenRunnable () {
		}
		public void run() {
			try {
				listen(54321);
				CircuitOramClient<Boolean> client = new CircuitOramClient<Boolean>(is, os, N, dataSize, Party.Alice, capacity, Mode.COUNT, securityParameter);
				System.gc();
				client.write(1, 1, 1, Utils.fromInt(1, client.lengthOfData));
				System.gc();
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
				CircuitOramServer<Boolean> server = new CircuitOramServer<Boolean>(is, os, N, dataSize, Party.Bob, capacity, Mode.COUNT, securityParameter);
				PMCompEnv mce = (PMCompEnv)server.eva;
				mce.statistic.flush();
				System.gc();
				server.access(1);
				System.gc();
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
		int base = 15;
		int num = 1;
		int[] logs = new int[num];
		int[] y = new int[num];
		for(int log = base; log < base+num; ++log) {
			CountCSCORAM c = new CountCSCORAM(log, 5, 32, 80);
			c.count();
			logs[log-base] = log;
			y[log-base] = (int) (c.statistic.andGate/(double)log);
		}
		System.out.print("x="+Arrays.toString(logs));
		System.out.print("y="+Arrays.toString(y));
	}
}