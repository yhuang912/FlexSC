package oram.counter;

import oram.CSCOram.RecursiveCSCOramClient;
import oram.CSCOram.RecursiveCSCOramServer;
import pm.PMCompEnv;
import test.Utils;
import flexsc.Mode;

public class CountCSCORAMRecursion extends ORAMCounterHarness{
	int cutoff, recurFactor,add;
	public CountCSCORAMRecursion(int logN, int capacity, int dataSize, int securityParameter, int cutoff, int recurFactor, int add) {
		super(logN, capacity, dataSize, securityParameter);
		this.cutoff = cutoff;
		this.recurFactor = recurFactor;
		this.add=add;
	}
	
	public CountCSCORAMRecursion(int logN, int capacity, int dataSize, int securityParameter, int cutoff, int recurFactor) {
		super(logN, capacity, dataSize, securityParameter);
		this.cutoff = cutoff;
		this.recurFactor = recurFactor;
		this.add=12345;
	}
	
	class GenRunnable extends network.Server implements Runnable {
		GenRunnable () {
		}
		public void run() {
			try {
				listen(add);
				RecursiveCSCOramClient<Boolean> client = new RecursiveCSCOramClient<Boolean>(is, os, N, dataSize,  cutoff, recurFactor,capacity, Mode.COUNT, securityParameter);
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
				connect("localhost", add);
				RecursiveCSCOramServer<Boolean> server = new RecursiveCSCOramServer<Boolean>(is, os, N, dataSize,  cutoff, recurFactor,capacity, Mode.COUNT, securityParameter);
				for(int i = 0; i < server.servers.size(); ++i) {
					PMCompEnv mce = (PMCompEnv)server.servers.get(i).eva;
					mce.statistic.flush();
				}
				{
					PMCompEnv mce = (PMCompEnv)server.baseOram.eva;
					mce.statistic.flush();
				}
				
				server.access();
//				server.access();
				
				for(int i = 0; i < server.servers.size(); ++i) {
					PMCompEnv mce = (PMCompEnv)server.servers.get(i).eva;
					statistic.add(mce.statistic);
				}
				{
					PMCompEnv mce = (PMCompEnv)server.baseOram.eva;
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
		CountCSCORAMRecursion c = new CountCSCORAMRecursion(10, 4, 10, 80, 5, 5);
		c.count();
		System.out.print(c.statistic.andGate);
	}
}