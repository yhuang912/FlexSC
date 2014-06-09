package test.oram.cscoram;

import java.security.SecureRandom;

import oram.CSCOram.CSCOramClient;
import oram.CSCOram.CSCOramServer;

import org.junit.Test;
import org.junit.Assert;

import flexsc.*;
import gc.GCGen;
import gc.GCSignal;
import test.Utils;


public class TestNewOramBasic {
	final int N = 1<<5;
	final int capacity = 6;
	int[] posMap = new int[N];
	int writecount = N;
	int readcount = N;
	int dataSize = 32;
	public TestNewOramBasic() {
		SecureRandom rng = new SecureRandom();
		for(int i = 0; i < posMap.length; ++i)
			posMap[i] = rng.nextInt(N);
	}
	SecureRandom rng = new SecureRandom();
	boolean breaksignal = false;
	
	class GenRunnable extends network.Server implements Runnable {
		int port;
		GenRunnable (int port) {
			this.port = port;
		}
		public int[][] idens;
		public boolean[][] du;

		public void run() {
			try {
				listen(port);

				int data[] = new int[N+1];
//				CSCOramClient<GCSignal> client = new CSCOramClient<GCSignal>(is, os, N, dataSize, Party.Alice, capacity, Mode.REAL, 80);
				CSCOramClient<GCSignal> client = new CSCOramClient<GCSignal>(is, os, N, dataSize, Party.Alice, capacity, Mode.VERIFY, 80);
				System.out.println("logN:"+client.logN+", N:"+client.N);
				
				
				for(int i = 0; i < writecount; ++i) {
					int element = i%N;

					int oldValue = posMap[element];
					int newValue = rng.nextInt(1<<client.lengthOfPos);
					System.out.println(element+" "+oldValue+" "+newValue);
					data[element] = 2*element+1;
					long t1 = System.currentTimeMillis();
					client.write(element, oldValue, newValue, Utils.fromInt(data[element], client.lengthOfData));
					posMap[element] = newValue;

					long t2 = System.currentTimeMillis() - t1;
					System.out.println("time: "+t2/1000.0);
					Runtime rt = Runtime.getRuntime(); 
				    double usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024.0 / 1024.0;
				    System.out.println("mem: "+usedMB);

					posMap[element] = newValue;
				}

				for(int i = 0; i < readcount; ++i){
					int element = i%N;
					int oldValue = posMap[element];
					int newValue = rng.nextInt(1<<client.lengthOfPos);
					System.out.println(element+" "+oldValue+" "+newValue);

					boolean[] b = client.read(element, oldValue, newValue);
//					os.write(0);
					posMap[element] = newValue;
					if(Utils.toInt(b) != data[element]) {
						System.out.println("inconsistent: "+element+" "+Utils.toInt(b) + " "+data[element]+" "+posMap[element]);
					}
//					Assert.assertTrue(Utils.toInt(b) == data[element]);					
				}
				
				idens = new int[client.tree.length][];
				du = new boolean[client.tree.length][];

				for(int j = 1; j < client.tree.length; ++j){
					idens[j] = new int[client.tree[j].length];
					for(int i = 0; i < client.tree[j].length; ++i)
						idens[j][i]=Utils.toInt(client.tree[j][i].iden);
					}

				for(int j = 1; j < client.tree.length; ++j){
					du[j] = new boolean[client.tree[j].length];
					for(int i = 0; i < client.tree[j].length; ++i)
						du[j][i]=client.tree[j][i].isDummy;
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
				System.out.print("!!");
				
//				CSCOramServer<GCSignal> server = new CSCOramServer<GCSignal>(is, os, N, dataSize, Party.Bob, capacity, Mode.REAL, 80);
				CSCOramServer<GCSignal> server = new CSCOramServer<GCSignal>(is, os, N, dataSize, Party.Bob, capacity, Mode.VERIFY, 80);
				
				for(int i = 0; i < writecount; ++i) {
					int element = i%N;
					int oldValue = posMap[element];
					server.access(oldValue);
//					is.read();
				}

				for(int i = 0; i < readcount; ++i){
					int element = i%N;
					int oldValue = posMap[element];
					server.access(oldValue);
//					is.read();
				}
				
				idens = new int[server.tree.length][];
				du = new boolean[server.tree.length][];
				for(int j = 1; j < server.tree.length; ++j){
					idens[j] = new int[server.tree[j].length];
					for(int i = 0; i < server.tree[j].length; ++i)
						idens[j][i]=Utils.toInt(server.tree[j][i].iden);
					}

				for(int j = 1; j < server.tree.length; ++j){
					du[j] = new boolean[server.tree[j].length];
					for(int i = 0; i < server.tree[j].length; ++i)
						du[j][i]=server.tree[j][i].isDummy;
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
//		printTree(gen,eva);
		System.out.print("\n");

		System.out.println();
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