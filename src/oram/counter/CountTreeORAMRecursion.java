package oram.counter;

import oram.treeoram.RecursiveTreeOramClient;
import oram.treeoram.RecursiveTreeOramServer;
import pm.PMCompEnv;
import flexsc.*;
import test.Utils;

public class CountTreeORAMRecursion extends ORAMCounterHarness{
	int cutoff, recurFactor;
	public CountTreeORAMRecursion(int logN, int capacity, int dataSize, int securityParameter, int cutoff, int recurFactor) {
		super(logN, capacity, dataSize, securityParameter);
		this.cutoff = cutoff;
		this.recurFactor = recurFactor;
	}
	
	class GenRunnable extends network.Server implements Runnable {
		GenRunnable () {
		}
		public void run() {
			try {
				listen(54321);
				RecursiveTreeOramClient<Boolean> client = new RecursiveTreeOramClient<Boolean>(is, os, N, dataSize, capacity, cutoff, recurFactor, Mode.COUNT);
				client.write(1, Utils.fromInt(1, client.clients.get(0).lengthOfData));
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
				RecursiveTreeOramServer<Boolean> server = new RecursiveTreeOramServer<Boolean>(is, os, N, dataSize, capacity, cutoff, recurFactor, Mode.COUNT);
				for(int i = 0; i < server.servers.size(); ++i) {
					PMCompEnv mce = (PMCompEnv)server.servers.get(i).eva;
					mce.statistic.flush();
				}
				server.access();
				
				for(int i = 0; i < server.servers.size(); ++i) {
					PMCompEnv mce = (PMCompEnv)server.servers.get(i).eva;
					statistic.add(mce.statistic);
				}
				
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
		CountTreeORAMRecursion c = new CountTreeORAMRecursion(10, 4, 10, 80, 5, 5);
		c.count();
		System.out.print(c.statistic.andGate);
	}
}