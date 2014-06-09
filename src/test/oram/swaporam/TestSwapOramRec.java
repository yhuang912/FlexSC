package test.oram.swaporam;

import flexsc.*;
import gc.GCSignal;
import oram.swapoam.RecursiveSwapOramClient;
import oram.swapoam.RecursiveSwapOramServer;

import org.junit.Assert;
import org.junit.Test;

import test.Utils;
public class TestSwapOramRec {
	
	@Test
	public void runThreads() throws Exception {
		GenRunnable gen = new GenRunnable(12345, 20, 6, 32,  4, 10);
		EvaRunnable eva = new EvaRunnable("localhost", 12345);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(10);
		tEva.start();
		tGen.join();
		printTree(gen,eva);
		System.out.print("\n");

		System.out.println();
	}
	
	final static int writeCount = 32;
	final static int readCount = 32;
	public TestSwapOramRec() {
	}
	
	class GenRunnable extends network.Server  implements Runnable{
		int port;
		int logN = 1<<10;
		int N;
		int recurFactor = 8;
		int cutoff = 1<<10;
		int capacity = 6;
		int dataSize = 32;
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
		public int[][] idens;
		public boolean[][] du;
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
				double T = 0;
				
				RecursiveSwapOramClient<GCSignal> client = new RecursiveSwapOramClient<GCSignal>(is, os, N, dataSize, cutoff, recurFactor, capacity, Mode.REAL, 80);

				Flag.bandwidth = 0;
				for(int i = 0; i < writeCount; ++i) {
					int element = i%N;
					
					long t1 = System.currentTimeMillis();
					client.write(element, Utils.fromInt(element, dataSize));
					long t2 = System.currentTimeMillis() - t1;
					Flag.TotalTime += t2;
					System.out.println("time: "+t2/1000.0);
					T+=t2;
					//System.gc();
					Runtime rt = Runtime.getRuntime(); 
				    double usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024.0 / 1024.0;
				    System.out.println("mem: "+usedMB);	
				}
				System.out.println("avg time : "+T/10.0);

				for(int i = 0; i < readCount; ++i){
					int element = i%N;
					boolean[] b = client.read(element);
					//Assert.assertTrue(Utils.toInt(b) == element);
					if(Utils.toInt(b) != element)
						System.out.println("inconsistent: "+element+" "+Utils.toInt(b));
				}
				
				os.flush();

				idens = new int[client.clients.get(0).tree.length][];
				du = new boolean[client.clients.get(0).tree.length][];

				for(int j = 1; j < client.clients.get(0).tree.length; ++j){
					idens[j] = new int[client.clients.get(0).tree[j].length];
					for(int i = 0; i < client.clients.get(0).tree[j].length; ++i)
						idens[j][i]=Utils.toInt(client.clients.get(0).tree[j][i].iden);
					}

				for(int j = 1; j < client.clients.get(0).tree.length; ++j){
					du[j] = new boolean[client.clients.get(0).tree[j].length];
					for(int i = 0; i < client.clients.get(0).tree[j].length; ++i)
						du[j][i]=client.clients.get(0).tree[j][i].isDummy;
				}
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
		public int[][] idens;
		public boolean[][] du;
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
				RecursiveSwapOramServer<GCSignal> server = new RecursiveSwapOramServer<GCSignal>(is, os, N, dataSize, cutoff, recurFactor, capacity, Mode.REAL, 80);
				Flag.bandwidth = 0;
				for(int i = 0; i < writeCount; ++i) {
					
					long t1 = System.currentTimeMillis();
					server.access();
					long t2 = System.currentTimeMillis() - t1;
					Flag.TotalTime += t2;
					System.out.println("time: "+t2/1000.0);

					
					
				}

				for(int i = 0; i < readCount; ++i){
					server.access();
				}
				

				os.flush();
				
				idens = new int[server.servers.get(0).tree.length][];
				du = new boolean[server.servers.get(0).tree.length][];
				for(int j = 1; j < server.servers.get(0).tree.length; ++j){
					idens[j] = new int[server.servers.get(0).tree[j].length];
					for(int i = 0; i < server.servers.get(0).tree[j].length; ++i)
						idens[j][i]=Utils.toInt(server.servers.get(0).tree[j][i].iden);
					}

				for(int j = 1; j < server.servers.get(0).tree.length; ++j){
					du[j] = new boolean[server.servers.get(0).tree[j].length];
					for(int i = 0; i < server.servers.get(0).tree[j].length; ++i)
						du[j][i]=server.servers.get(0).tree[j][i].isDummy;
				}

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
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
	
	public void printTree(GenRunnable gen, EvaRunnable eva) {
		int k = 1;
		int i = 1;
		for(int j = 1; j < gen.idens.length; ++j) {
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
	
}