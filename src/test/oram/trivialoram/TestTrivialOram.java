package test.oram.trivialoram;

import oram.trivialoram.TrivialOramClient;
import oram.trivialoram.TrivialOramServer;

import org.junit.Test;

import flexsc.*;
import gcHalfANDs.GCSignal;
import test.Utils;


public class TestTrivialOram {
	final int N = 1<<11;
	int writecount = 1;
	int readcount = 1;
	int dataSize = 32;
	public TestTrivialOram() {
	}
	
	class GenRunnable extends network.Server implements Runnable {
		int port;
		GenRunnable (int port) {
			this.port = port;
		}

		public void run() {
			try {
				listen(port);

				int data[] = new int[N+1];
				TrivialOramClient<GCSignal> client = new TrivialOramClient<GCSignal>(is, os, N, dataSize, Mode.REAL);
//				TrivialOramClient<Boolean> client = new TrivialOramClient<Boolean>(is, os, N, dataSize, Mode.VERIFY);
				System.out.println("logN:"+client.logN+", N:"+client.N);
				
				
				for(int i = 0; i < writecount; ++i) {
					int element = i%N;


					data[element] = 2*element+1;
					long t1 = System.currentTimeMillis();
					client.write(element, Utils.fromInt(data[element], client.lengthOfData));
					long t2 = System.currentTimeMillis() - t1;
					System.out.println("time: "+t2/1000.0);
					System.gc();
					Runtime rt = Runtime.getRuntime(); 
				    double usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024.0 / 1024.0;
				    System.out.println("mem: "+usedMB);


				}

				for(int i = 0; i < readcount; ++i){
					int element = i%N;

					boolean[] b = client.read(element);
					if(Utils.toInt(b) != data[element]) {
						System.out.println("inconsistent: "+element+" "+Utils.toInt(b) + " "+data[element]);
					}
				}
	
				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		String host;
		int port;
		public int[][] idens;
		public boolean[][] du;

		EvaRunnable (String host, int port) {
			this.host =  host;
			this.port = port;
		}

		public void run() {
			try {
				connect(host, port);				
				TrivialOramServer<GCSignal> server = new TrivialOramServer<GCSignal>(is, os, N, dataSize, Mode.REAL);
//				TrivialOramServer<Boolean> server = new TrivialOramServer<Boolean>(is, os, N, dataSize, Mode.VERIFY);
				
				
				for(int i = 0; i < writecount; ++i) {
					server.access();
				}

				for(int i = 0; i < readcount; ++i){
					server.access();
				}
				
				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	@Test
	public void runThreads() throws Exception {
		GenRunnable gen = new GenRunnable(1234);
		EvaRunnable eva = new EvaRunnable("localhost", 1234);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(10);
		tEva.start();
		tGen.join();
	}
}