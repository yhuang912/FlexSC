package test.oram.circuitoram;

import oram.circuitoram.RecursiveCircuitOramClient;
import oram.circuitoram.RecursiveCircuitOramServer;

import org.junit.Test;

import test.Utils;
import flexsc.Flag;
import flexsc.Mode;
import gc.GCSignal;
public class TestCircuitOramRec {

	@Test
	public void runThreads() throws Exception {
		GenRunnable gen = new GenRunnable(12345, 20, 6, 32,  4, 10);
		EvaRunnable eva = new EvaRunnable("localhost", 12345);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(10);
		tEva.start();
		tGen.join();
		Flag.sw.print();
		System.out.print("\n");
	}

	final static int writeCount = 5;
	final static int readCount = 0;
	public TestCircuitOramRec() {
	}

	class GenRunnable extends network.Server  implements Runnable{
		int port;
		int logN;
		int N;
		int recurFactor;
		int cutoff;
		int capacity;
		int dataSize;
		int logCutoff;

		GenRunnable (int port, int logN, int capacity, int dataSize, int recurFactor, int logCutoff) {
			this.port = port;
			this.logN = logN;
			this.N = 1<<logN;
			this.recurFactor = recurFactor;
			this.logCutoff = logCutoff;
			this.cutoff = 1<<logCutoff;
			this.dataSize = dataSize;
			this.capacity = capacity;
		}
		public void run() {
			try {
				listen(port);

				os.write(logN);
				os.write(recurFactor);
				os.write(logCutoff);
				os.write(capacity);
				os.write(dataSize);
				os.flush();

				System.out.println("\nlogN recurFactor  cutoff capacity dataSize");
				System.out.println(logN+" "+recurFactor +" "+cutoff+" "+capacity+" "+dataSize);

				System.out.println("connected");				
				RecursiveCircuitOramClient<GCSignal> client = new RecursiveCircuitOramClient<GCSignal>(is, os, N, dataSize, cutoff, recurFactor, capacity, Mode.REAL, 80);

				for(int i = 0; i < writeCount; ++i) {
					int element = i%N;

					Flag.sw.startTotal();
					client.write(element, Utils.fromInt(element, dataSize));
					double t= Flag.sw.stopTotal();
					System.out.println(Flag.sw.ands+" "+t/1000000000.0 + " " +Flag.sw.ands/t*1000);
					Flag.sw.ands = 0;
					Flag.sw.addCounter();


					Runtime rt = Runtime.getRuntime(); 
					double usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024.0 / 1024.0;
					System.out.println("mem: "+usedMB);	
				}

				for(int i = 0; i < readCount; ++i){
					int element = i%N;
					boolean[] b = client.read(element);
					//Assert.assertTrue(Utils.toInt(b) == element);
					if(Utils.toInt(b) != element)
						System.out.println("inconsistent: "+element+" "+Utils.toInt(b));
				}

				os.flush();


				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable{

		String host;		
		int port;		
		EvaRunnable (String host, int port) {
			this.host = host;
			this.port = port;
		}
		public void run() {
			try {
				connect(host, port);

				int logN = is.read();
				int recurFactor = is.read();
				int logCutoff = is.read();
				int cutoff = 1<<logCutoff;
				int capacity = is.read();
				int dataSize = is.read();

				int N = 1<<logN;
				System.out.println("\nlogN recurFactor  cutoff capacity dataSize");
				System.out.println(logN+" "+recurFactor +" "+cutoff+" "+capacity+" "+dataSize);
				System.out.println("connected");
				RecursiveCircuitOramServer<GCSignal> server = new RecursiveCircuitOramServer<GCSignal>(is, os, N, dataSize, cutoff, recurFactor, capacity, Mode.REAL, 80);
				for(int i = 0; i < writeCount; ++i) {
//					Flag.sw.startTotal();
					server.access();
//					Flag.sw.stopTotal();
//					Flag.sw.addCounter();
					printStatistic();

				}

				for(int i = 0; i < readCount; ++i){
					server.access();
				}

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}