package oram.kaiminOram;

import java.security.SecureRandom;

import org.junit.Assert;
import org.junit.Test;

import oram.OramParty.Mode;
import oram.OramParty.Party;
import test.Utils;


public class TestKaiminOram {
	final int N = 1<<4;
	final int nodeCapacity = 6;
	final int leafCapacity = 6;
	int[] posMap = new int[N];
	int writecount = N*2;
	int readcount = N*2;
	int dataSize = 7;
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
		public boolean[][] du;
		public int[] queue;
		public void run() {
			try {
				listen(54321);

				int data[] = new int[N+1];
				KaiminOramClient<Boolean> client = new KaiminOramClient<Boolean>(is, os, N, dataSize, Party.CLIENT, nodeCapacity, leafCapacity, Mode.TEST);
				System.out.println("logN:"+client.logN+", N:"+client.N);
				
				idens = new int[client.tree.length][nodeCapacity];
				du = new boolean[client.tree.length][nodeCapacity];
				for(int i = client.tree.length/2; i < client.tree.length; ++i)
					idens[i] = new int[leafCapacity];
				
				for(int i = 0; i < writecount; ++i) {
					int element = i%N;

					int oldValue = posMap[element];
					int newValue = rng.nextInt(1<<client.lengthOfPos);
					System.out.println(element+" "+oldValue+" "+newValue);
					data[element] = 2*element+1;
					client.write(element, oldValue, newValue, Utils.fromInt(data[element], client.lengthOfData));
					
					posMap[element] = newValue;
				}

				for(int i = 0; i < readcount; ++i){
					int element = i%N;
					int oldValue = posMap[element];
					int newValue = rng.nextInt(1<<client.lengthOfPos);
					
					boolean[] b = client.read(element, oldValue, newValue);
					//Assert.assertTrue(Utils.toInt(b.data) == data[element]);
					if(Utils.toInt(b) != data[element]){
						System.out.println("inconsistent: "+element+" "+Utils.toInt(b) + " "+data[element]+" "+posMap[element]);
					}
					posMap[element] = newValue;
				}
				
				for(int j = 1; j < client.tree.length/2; ++j)
					for(int i = 0; i < nodeCapacity; ++i){
						idens[j][i]=Utils.toInt(client.tree[j][i].data);
						du[j][i] = client.tree[j][i].isDummy;
					}
				for(int j = client.tree.length/2; j < client.tree.length; ++j)
					for(int i = 0; i < leafCapacity; ++i){
						idens[j][i]=Utils.toInt(client.tree[j][i].data);
						du[j][i] = client.tree[j][i].isDummy;
					}
				
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
		public boolean[][] du;
		public int[] queue;
		EvaRunnable () {
		}

		public void run() {
			try {
				connect("localhost", 54321);				
				KaiminOramServer<Boolean> server= new KaiminOramServer<Boolean>(is, os, N, dataSize, Party.SERVER, nodeCapacity, leafCapacity, Mode.TEST);
				
				idens = new int[server.tree.length][nodeCapacity];
				du = new boolean[server.tree.length][nodeCapacity];
				for(int i = server.tree.length/2; i < server.tree.length; ++i)
					idens[i] = new int[leafCapacity];
				
				
				for(int i = 0; i < writecount; ++i) {
					int element = i%N;
					int oldValue = posMap[element];
					server.access(oldValue);
				}

				for(int i = 0; i < readcount; ++i){
					int element = i%N;
					int oldValue = posMap[element];
					server.access(oldValue);
				}
				
				for(int j = 1; j < server.tree.length/2; ++j)
					for(int i = 0; i < nodeCapacity; ++i) {
						idens[j][i]=Utils.toInt(server.tree[j][i].data);
						du[j][i] = server.tree[j][i].isDummy;
					}
				for(int j = server.tree.length/2; j < server.tree.length; ++j)
					for(int i = 0; i < leafCapacity; ++i){
						idens[j][i]=Utils.toInt(server.tree[j][i].data);
						du[j][i] = server.tree[j][i].isDummy;	
					}

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
			System.out.print("[");
			int[] a = xor(gen.idens[j], eva.idens[j]);
			boolean[] bb = xor(gen.du[j], eva.du[j]);
			for(int p = 0; p < eva.idens[j].length; ++p)
				if(bb[p])
					System.out.print("d,");
				else
					System.out.print(a[p]+",");
			System.out.print("]");
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