package oramgc.treeoram;

import java.security.SecureRandom;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import oramgc.OramParty.BlockInBinary;
import oramgc.OramParty.Party;
import test.Utils;


public class TestTreeOram {
	final int N = 64;
	final int capacity = 10;
	int[] posMap = new int[N+1];
	int writeCount = 2*N;
	int readCount = N;
	int dataSize = 6;
	public TestTreeOram(){
		for(int i = 0; i < posMap.length; ++i)
			posMap[i] = 1;
	}
	SecureRandom rng = new SecureRandom();
	class GenRunnable extends network.Server implements Runnable {
		GenRunnable () {
		}
		public int[][] idens;
		public void run() {
			try {
				listen(54321);

				int data[] = new int[N+1];
				TreeOramClient client = new TreeOramClient(is, os, N, dataSize, Party.CLIENT, capacity);
				System.out.println("logN:"+client.logN+", N:"+client.N);

				idens = new int[client.tree.length][capacity];

				for(int i = 0; i < writeCount; ++i) {
					int element = Math.abs(i % (N-1)) +1;

					int oldValue = posMap[element];
					int newValue = rng.nextInt(1<<client.lengthOfPos);
					System.out.println(element+" "+oldValue+" "+newValue);
					client.write(element, oldValue, newValue, Utils.fromInt(element, client.lengthOfData));
					data[element] = element;
					posMap[element] = newValue;
				}

				for(int i = 1; i < readCount; ++i){
					int element = i;
					int oldValue = posMap[element];
					int newValue = rng.nextInt(1<<client.lengthOfPos);

					BlockInBinary b = client.read(element, oldValue, newValue);
					Assert.assertTrue(Utils.toInt(b.data) == data[element]);
					if(Utils.toInt(b.data) != data[element])
						System.out.println("inconsistent: "+element+" "+Utils.toInt(b.data) + " "+data[element]);
					posMap[element] = newValue;
				}

				for(int j = 1; j < client.tree.length; ++j)
					for(int i = 0; i < capacity; ++i)
						idens[j][i]=Utils.toInt(client.tree[j][i].iden);

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
		EvaRunnable () {
		}

		public void run() {
			try {
				connect("localhost", 54321);				
				TreeOramServer server = new TreeOramServer(is, os, N, dataSize, Party.SERVER, capacity);

				idens = new int[server.tree.length][capacity];
				for(int i = 0; i < writeCount; ++i) {
					int element = Math.abs(i % (N-1)) +1;
					int oldValue = posMap[element];
					server.access(oldValue);
				}

				for(int i = 1; i < readCount; ++i){
					int element = i;
					int oldValue = posMap[element];
					server.access(oldValue);
				}

				for(int j = 1; j < server.tree.length; ++j)
					for(int i = 0; i < capacity; ++i)
						idens[j][i]=Utils.toInt(server.tree[j][i].iden);


				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void printTree(GenRunnable gen, EvaRunnable eva) {
		int k = 1;
		int i = 1;
		for(int j = 1; j < gen.idens.length; ++j){
			System.out.print(Arrays.toString(xor(gen.idens[j], eva.idens[j])));
			if(i == k ){
				k = k*2;
				i = 0;
				System.out.print("\n");
			}
			++i;
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
		printTree(gen,eva);
		System.out.print("\n");
		System.out.println(Arrays.toString(posMap));
	}

	public boolean[] xor(boolean[]a, boolean[] b) {
		boolean[] res = new boolean[a.length];
		for(int i = 0; i <res.length; ++i)
			res[i] = a[i]^b[i];
		return res;

	}

	public int[] xor(int[]a, int[] b) {
		int[] res = new int[a.length];
		for(int i = 0; i <res.length; ++i)
			res[i] = a[i]^b[i];
		return res;

	}
}