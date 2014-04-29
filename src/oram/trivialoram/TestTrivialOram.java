package oram.trivialoram;

import org.junit.Assert;
import org.junit.Test;
import flexsc.Mode;
import test.Utils;


public class TestTrivialOram {
	int N = 1<<5;
	int dataSize = 13;
	int writeCount = N;
	int readCount = N;
	class GenRunnable extends network.Server implements Runnable {
		public int[] idens;
		GenRunnable () {
		}

		public void run() {
			try {
				listen(54321);

				TrivialOramClient<Boolean> client = new TrivialOramClient<Boolean>(is, os, N, dataSize, Mode.VERIFY);

				for(int i = 0; i < writeCount; ++i) {
					int element = i%N;
					client.write(element, Utils.fromInt(2*element, client.lengthOfData));
				}

				for(int i = 1; i < readCount; ++i){
					int element = i%N;
					boolean[] b = client.read(element);

					if(Utils.toInt(b) != 2*element)
						System.out.println("inconsistent: "+element+" "+Utils.toInt(b));
					Assert.assertTrue(Utils.toInt(b) == 2*element);
				}

				idens = new int[N];
				for(int i = 0; i < N; ++i)
					idens[i]=Utils.toInt( new boolean[]{client.bucket[i].isDummy});
				
				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		public int[] idens;
		EvaRunnable () {
		}

		public void run() {
			try {
				connect("localhost", 54321);				
				TrivialOramServer<Boolean> server = new TrivialOramServer<Boolean>(is, os, N, dataSize, Mode.VERIFY);
				System.out.flush();
				
				for(int i = 0; i < writeCount; ++i) {
					server.access();
				}

				for(int i = 1; i < readCount; ++i){
					server.access();
				}

				idens = new int[N];
				for(int i = 0; i < N; ++i)
					idens[i]=Utils.toInt( new boolean[]{server.bucket[i].isDummy});

				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void printTree(GenRunnable gen, EvaRunnable eva) {
		for(int j = 1; j < gen.idens.length; ++j){
			System.out.print(xor(gen.idens[j], eva.idens[j]));
		}
		System.out.print("\n");
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
		//printTree(gen, eva);
	}
	
	public int xor(int a, int b) {
		return a ^ b;
		
	}

}