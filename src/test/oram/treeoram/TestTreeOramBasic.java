package test.oram.treeoram;

import java.security.SecureRandom;
import java.util.Random;

import oram.treeoram.TreeOramClient;
import oram.treeoram.TreeOramServer;

import org.junit.Assert;
import org.junit.Test;

import test.Utils;
import flexsc.Mode;
import flexsc.Party;


public class TestTreeOramBasic {
	final int N =(1<<5);
	final int capacity = 15;
	int[] posMap = new int[N+1];
	int writeCount = N*2;
	int readCount = N;
	int dataSize = 13;
	public TestTreeOramBasic(){
		Random rnd = new Random();
		for(int i = 0; i < posMap.length; ++i)
			posMap[i] = rnd.nextInt(N);
	}
	SecureRandom rng = new SecureRandom();
	class GenRunnable extends network.Server implements Runnable {
		GenRunnable () {
		}
		public void run() {
			try {
				listen(54321);

				int data[] = new int[N+1];
				//TreeOramClient<GCSignal> client = new TreeOramClient<GCSignal>(is, os, N, dataSize, Party.CLIENT, capacity, Mode.REAL);
				TreeOramClient<Boolean> client = new TreeOramClient<Boolean>(is, os, N, dataSize, Party.Alice, Mode.VERIFY, 10);
				System.out.println("logN:"+client.logN+", N:"+client.N);

				for(int i = 0; i < writeCount; ++i) {
					int element = i%N;

					int oldValue = posMap[element];
					int newValue = rng.nextInt(1<<client.lengthOfPos);
					System.out.println(element+" "+oldValue+" "+newValue);
					data[element] = 2*element+1;
					client.write(element, oldValue, newValue, Utils.fromInt(data[element], client.lengthOfData));
					
					posMap[element] = newValue;
				}

				for(int i = 0; i < readCount; ++i){
					int element = i%N;
					int oldValue = posMap[element];
					int newValue = rng.nextInt(1<<client.lengthOfPos);

					boolean[] b = client.read(element, oldValue, newValue);
					
					if(Utils.toInt(b) != data[element])
						System.out.println("inconsistent: "+element+" "+Utils.toInt(b) + " "+data[element]);
					Assert.assertTrue(Utils.toInt(b) == data[element]);
					posMap[element] = newValue;
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

		EvaRunnable () {
		}

		public void run() {
			try {
				connect("localhost", 54321);				
				//TreeOramServer<GCSignal> server = new TreeOramServer<GCSignal>(is, os, N, dataSize, Party.SERVER, capacity, Mode.REAL);
				TreeOramServer<Boolean> server = new TreeOramServer<Boolean>(is, os, N, dataSize, Party.Bob, Mode.VERIFY, 10);

				
				for(int i = 0; i < writeCount; ++i) {
					int element = i%N;
					int oldValue = posMap[element];
					server.access(oldValue);
				}

				for(int i = 0; i < readCount; ++i){
					int element = i%N;
					int oldValue = posMap[element];
					server.access(oldValue);
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
		GenRunnable gen = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(10);
		tEva.start();
		tGen.join();
		System.out.print("\n");
	}
}