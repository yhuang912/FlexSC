package oram.counter;

import oram.circuitoram.RecursiveCircuitOramClient;
import oram.circuitoram.RecursiveCircuitOramServer;
import pm.PMCompEnv;
import test.Utils;
import flexsc.Mode;

public class CountCircuitORAMRecursion extends ORAMCounterHarness{
	int cutoff, recurFactor,add;
	public CountCircuitORAMRecursion(int logN, int capacity, int dataSize, int securityParameter, int cutoff, int recurFactor, int add) {
		super(logN, capacity, dataSize, securityParameter);
		this.cutoff = cutoff;
		this.recurFactor = recurFactor;
		this.add=add;
	}
	
	public CountCircuitORAMRecursion(int logN, int capacity, int dataSize, int securityParameter, int cutoff, int recurFactor) {
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
				RecursiveCircuitOramClient<Boolean> client = new RecursiveCircuitOramClient<Boolean>(is, os, N, dataSize,  cutoff, recurFactor,capacity, Mode.COUNT, securityParameter);
				client.write(1, Utils.fromInt(1, client.clients.get(0).lengthOfData));
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
				RecursiveCircuitOramServer<Boolean> server = new RecursiveCircuitOramServer<Boolean>(is, os, N, dataSize,  cutoff, recurFactor,capacity, Mode.COUNT, securityParameter);
				for(int i = 0; i < server.servers.size(); ++i) {
					PMCompEnv mce = (PMCompEnv)server.servers.get(i).eva;
					mce.statistic.flush();
				}
				server.access();
				server.access();
				
				for(int i = 0; i < server.servers.size(); ++i) {
					PMCompEnv mce = (PMCompEnv)server.servers.get(i).eva;
					statistic.add(mce.statistic);
				}
				statistic.andGate/=2;
				statistic.xorGate/=2;
				statistic.OTs/=2;
				statistic.NumEncAlice/=2;
				statistic.NumEncBob/=2;
				statistic.notGate/=2;
				statistic.bandwidth/=2;
				
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
		CountCircuitORAMRecursion c = new CountCircuitORAMRecursion(10, 4, 10, 80, 5, 5);
		c.count();
		System.out.print(c.statistic.andGate);
	}
}