package oram.counter;

import oram.clporam.CLPOramRecClient;
import oram.clporam.CLPOramRecServer;
import pm.PMCompEnv;
import test.Utils;
import flexsc.Mode;

public class CountKaiminORAMRecursion extends ORAMCounterHarness{
	int cutoff, recurFactor;
	public CountKaiminORAMRecursion(int logN, int capacity, int dataSize, int securityParameter, int cutoff, int recurFactor) {
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
				CLPOramRecClient<Boolean> client = new CLPOramRecClient<Boolean>(is, os, N, dataSize, cutoff, recurFactor, capacity, capacity, Mode.COUNT, securityParameter);
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
				CLPOramRecServer<Boolean> server = new CLPOramRecServer<Boolean>(is, os, N, dataSize, cutoff, recurFactor, capacity, capacity, Mode.COUNT, securityParameter);
				for(int i = 0; i < server.servers.size(); ++i) {
					PMCompEnv mce = (PMCompEnv)server.servers.get(i).eva;
					mce.statistic.flush();
				}
				server.access();
				
				for(int i = 0; i < server.servers.size(); ++i) {
					PMCompEnv mce = (PMCompEnv)server.servers.get(i).eva;
					statistic.add(mce.statistic);
				}
				
				statistic.andGate *= (1+1.0/(Math.log(N)/Math.log(2.0)));
				statistic.xorGate *= (1+1.0/(Math.log(N)/Math.log(2.0)));
				statistic.NumEncAlice *=(1+1.0/(Math.log(N)/Math.log(2.0)));
				statistic.NumEncBob *= (1+1.0/(Math.log(N)/Math.log(2.0)));

				
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
		CountKaiminORAMRecursion c = new CountKaiminORAMRecursion(10, 4, 10, 80, 5, 5);
		c.count();
		System.out.print(c.statistic.andGate);
	}
}