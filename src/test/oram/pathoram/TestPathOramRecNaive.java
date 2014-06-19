package test.oram.pathoram;

import java.security.SecureRandom;

import oram.pathoramNaive.RecursivePathOramClient;
import oram.pathoramNaive.RecursivePathOramServer;

import org.junit.Test;

import test.Utils;
import flexsc.Mode;


public class TestPathOramRecNaive {
	final int N = 1<<7;
	int recurFactor = 4;
	int cutoff = 1<<10;
	int capacity = 4;
	int dataSize = 10;
	int writeCount = N;
	int readCount = N;
	public TestPathOramRecNaive(){
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
				
				RecursivePathOramClient<Boolean> client = new RecursivePathOramClient<Boolean>(is, os, N, dataSize, capacity, cutoff, recurFactor,  Mode.VERIFY, 80);
				for(int i = 0; i < writeCount; ++i) {
					int element = i%N;
					client.write(element, Utils.fromInt(element, dataSize));
				}

				for(int i = 0; i < readCount; ++i){
					int element = i%N;
					boolean[] b = client.read(element);
					//Assert.assertTrue(Utils.toInt(b) == element);
					if(Utils.toInt(b) != element)
						System.out.println("inconsistent: "+element+" "+Utils.toInt(b));
				}
				
				idens = new int[client.clients.get(0).tree.length][capacity];
				du = new boolean[client.clients.get(0).tree.length][capacity];

				for(int j = 1; j < client.clients.get(0).tree.length; ++j)
					for(int i = 0; i < capacity; ++i){
						idens[j][i]=Utils.toInt(client.clients.get(0).tree[j][i].data);
						du[j][i]=client.clients.get(0).tree[j][i].isDummy;	
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
				
				RecursivePathOramServer<Boolean> server = new RecursivePathOramServer<Boolean>(is, os, N, dataSize, capacity, cutoff, recurFactor,  Mode.VERIFY, 80);
				for(int i = 0; i < writeCount; ++i) {
					server.access();
				}

				for(int i = 0; i < readCount; ++i){
					server.access();
				}
				
				idens = new int[server.servers.get(0).tree.length][capacity];
				du = new boolean[server.servers.get(0).tree.length][capacity];

				for(int j = 1; j < server.servers.get(0).tree.length; ++j)
					for(int i = 0; i < capacity; ++i){
						idens[j][i]=Utils.toInt(server.servers.get(0).tree[j][i].data);
						du[j][i]=server.servers.get(0).tree[j][i].isDummy;
					}						

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