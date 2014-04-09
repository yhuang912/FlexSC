package oramgc.kaiminOram;

import java.security.SecureRandom;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import oramgc.OramParty.BlockInBinary;
import oramgc.OramParty.Party;
import test.Utils;


public class TestKaiminOram {
	final int N = 64;
	final int nodeCapacity = 6;
	final int leafCapacity = 6;
	int[] posMap = new int[N];
	int writecount = N*2;
	int readcount = N;
	int dataSize = 32;
	public TestKaiminOram(){
		SecureRandom rng = new SecureRandom();
		for(int i = 0; i < posMap.length; ++i)
			posMap[i] = rng.nextInt(N);
	}
	SecureRandom rng = new SecureRandom();
	boolean breaksignal = false;
	
	class GenRunnable extends network.Server implements Runnable {
		GenRunnable () {
		}
		public int[][] idens;
		public int[] queue;
		public void run() {
			try {
				listen(54321);

				int data[] = new int[N+1];
				KaiminOramClient client = new KaiminOramClient(is, os, N, dataSize, Party.CLIENT, nodeCapacity, leafCapacity);
				System.out.println("logN:"+client.logN+", N:"+client.N);
				
				idens = new int[client.tree.length][nodeCapacity];
				for(int i = client.tree.length/2; i < client.tree.length; ++i)
					idens[i] = new int[leafCapacity];
				
				for(int i = 0; i < writecount; ++i) {
					int element = Math.abs(i % (N-1)) +1;

					int oldValue = posMap[element];
					int newValue = rng.nextInt(1<<client.lengthOfPos);
					System.out.println(element+" "+oldValue+" "+newValue);
					client.write(element, oldValue, newValue, Utils.fromInt(element, client.lengthOfData));
					data[element] = element;
					posMap[element] = newValue;
				}

				for(int i = 1; i < readcount; ++i){
					int element = i;
					int oldValue = posMap[element];
					int newValue = rng.nextInt(1<<client.lengthOfPos);
					
					BlockInBinary b = client.read(element, oldValue, newValue);
					Assert.assertTrue(Utils.toInt(b.data) == data[element]);
					if(Utils.toInt(b.data) != data[element]){
						System.out.println("inconsistent: "+element+" "+Utils.toInt(b.data) + " "+data[element]+" "+posMap[element]);
					}
					posMap[element] = newValue;
				}
				
				for(int j = 1; j < client.tree.length/2; ++j)
					for(int i = 0; i < nodeCapacity; ++i)
						idens[j][i]=Utils.toInt(client.tree[j][i].data);
				for(int j = client.tree.length/2; j < client.tree.length; ++j)
					for(int i = 0; i < leafCapacity; ++i)
						idens[j][i]=Utils.toInt(client.tree[j][i].data);
				
				queue = new int[client.queueCapacity];
				for(int j = 0; j < client.queueCapacity; ++j)
						queue[j]=Utils.toInt(client.queue[j].data);

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
		public int[] queue;
		EvaRunnable () {
		}

		public void run() {
			try {
				connect("localhost", 54321);				
				KaiminOramServer server= new KaiminOramServer(is, os, N, dataSize, Party.SERVER, nodeCapacity, leafCapacity);
				
				idens = new int[server.tree.length][nodeCapacity];
				for(int i = server.tree.length/2; i < server.tree.length; ++i)
					idens[i] = new int[leafCapacity];
				
				
				for(int i = 0; i < writecount; ++i) {
					int element = Math.abs(i % (N-1)) +1;
					int oldValue = posMap[element];
					server.write(oldValue);
				}

				for(int i = 1; i < readcount; ++i){
					int element = i;
					int oldValue = posMap[element];
					server.read(oldValue);
				}
				
				for(int j = 1; j < server.tree.length/2; ++j)
					for(int i = 0; i < nodeCapacity; ++i)
						idens[j][i]=Utils.toInt(server.tree[j][i].data);
				for(int j = server.tree.length/2; j < server.tree.length; ++j)
					for(int i = 0; i < leafCapacity; ++i)
						idens[j][i]=Utils.toInt(server.tree[j][i].data);

				queue = new int[server.queueCapacity];
				for(int j = 0; j < server.queueCapacity; ++j)
					queue[j]=Utils.toInt(server.queue[j].data);

				
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
		System.out.print(Arrays.toString(xor(gen.queue, eva.queue)));
		System.out.print("\nposmap:"+Arrays.toString(posMap));
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
		System.out.println();
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