package test.oram.treeoram;

import java.security.SecureRandom;

import oram.treeoram.RecursiveTreeOramClient;
import oram.treeoram.RecursiveTreeOramServer;

import org.junit.Assert;
import org.junit.Test;

import test.Utils;
import flexsc.Mode;

public class TestTreeOramRec {
	final int N = 1<<5;
	int recurFactor = 2;
	int cutoff = 1<<2;
	int dataSize = 32;
	int writeCount = N*2;
	int readCount = N;
	public TestTreeOramRec(){
	}
	SecureRandom rng = new SecureRandom();
	class GenRunnable extends network.Server implements Runnable {
		GenRunnable () {
		}
		public int[][] idens;
		public boolean[][] du;
		public void run() {
			try {
				listen(54321);
				
				RecursiveTreeOramClient<Boolean> client = new RecursiveTreeOramClient<Boolean>(is, os, N, dataSize, cutoff, recurFactor, Mode.VERIFY, 10);
				for(int i = 0; i < writeCount; ++i) {
					int element = i%N;
					client.write(element, Utils.fromInt(element, dataSize));
				}

				for(int i = 0; i < readCount; ++i){
					int element = i%N;
					boolean[] b = client.read(element);
					if(Utils.toInt(b) != element)
						System.out.println("inconsistent: "+element+" "+Utils.toInt(b));
					Assert.assertTrue(Utils.toInt(b) == element);
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

		public int[][] idens;
		public boolean[][] du;
		EvaRunnable () {
		}

		public void run() {
			try {
				connect("localhost", 54321);		
				
				RecursiveTreeOramServer<Boolean> server = new RecursiveTreeOramServer<Boolean>(is, os, N, dataSize, cutoff, recurFactor,  Mode.VERIFY, 10);
				for(int i = 0; i < writeCount; ++i) {
					server.access();
				}

				for(int i = 0; i < readCount; ++i){
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
		GenRunnable gen = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(10);
		tEva.start();
		tGen.join();
	}
}