package test.oram;

import java.security.SecureRandom;
import java.util.Arrays;

import oram.pathoram.PathOramClient;
import oram.pathoram.PathOramServer;

import org.junit.Assert;
import org.junit.Test;

import flexsc.*;
import test.Utils;


public class TestPathOram {
	
	final int N = 1<<10;
	final int capacity = 4;
	int[] posMap = new int[N+1];
	int writeCount = N;
	int readCount = N;
	int dataSize = 13;
	int[] writeIndex = new int[writeCount];
	int[] readIndex = new int[readCount];
	public TestPathOram() {
		SecureRandom rng = new SecureRandom();
		for(int i = 0; i < posMap.length; ++i)
			posMap[i] = rng.nextInt(N);
		for(int i = 0; i < writeCount; ++i)
			writeIndex[i] = i%N;//rng.nextInt(N);
		for(int i = 0; i < readCount; ++i)
			readIndex[i] = i%N;//rng.nextInt(N);
	}
	
	SecureRandom rng = new SecureRandom();
	
	final int PATHORAMCAPACITY = 4;
	class GenRunnable extends network.Server implements Runnable {
		GenRunnable () {
		}
		public int[][] idens;
		public boolean[][] du;
		public int[] stash;
		public void run() {
			try {
				listen(54321);
				int data[] = new int[N+1];

				PathOramClient<Boolean> client = new PathOramClient<Boolean>(is, os, N, dataSize, Party.Alice, Mode.VERIFY);
				System.out.println("logN:"+client.logN+", N:"+client.N);
				
				for(int i = 0; i < writeCount; ++i) {
					int element = writeIndex[i];

					int oldValue = posMap[element];
					int newValue = rng.nextInt(1<<client.lengthOfPos);
					System.out.println(element+" "+oldValue+" "+newValue);
					data[element] = element;
					client.write(element, oldValue, newValue, Utils.fromInt(data[element], client.lengthOfData));
					posMap[element] = newValue;
				}
				
				for(int i = 0; i < readCount; ++i) {
					int element = readIndex[i];
					int oldValue = posMap[element];
					int newValue = rng.nextInt(1<<client.lengthOfPos);
					
					boolean[] b = client.read(element, oldValue, newValue);
					
					//Assert.assertTrue(Utils.toInt(b) == data[element]);
					if(Utils.toInt(b) != data[element])
					System.out.println("inconsistent: "+element+" "+Utils.toInt(b) + " "+data[element]);

					posMap[element] = newValue;
				}

				
				idens = new int[client.tree.length][PATHORAMCAPACITY];
				du = new boolean[client.tree.length][PATHORAMCAPACITY];
				for(int j = 1; j < client.tree.length; ++j)
					for(int i = 0; i < PATHORAMCAPACITY; ++i)
						idens[j][i]=Utils.toInt(client.tree[j][i].iden);
				
				for(int j = 1; j < client.tree.length; ++j)
					for(int i = 0; i < PATHORAMCAPACITY; ++i)
						du[j][i]=client.tree[j][i].isDummy;
				
				stash = new int[client.stash.length];
				for(int j = 0; j < client.stash.length; ++j)
						stash[j]=Utils.toInt(client.stash[j].iden);
				
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
		public int[] stash;
		EvaRunnable () {
		}

		public void run() {
			try {
				connect("localhost", 54321);				
				PathOramServer<Boolean> server = new PathOramServer<Boolean>(is, os, N, dataSize, Party.Bob, Mode.VERIFY);
				
				for(int i = 0; i < writeCount; ++i) {
					int element = writeIndex[i];
					int oldValue = posMap[element];
					server.access(oldValue);
				}
				
				
				for(int i = 0; i < readCount; ++i){
					int element = readIndex[i];
					int oldValue = posMap[element];
					server.access(oldValue);
				}
				
				idens = new int[server.tree.length][PATHORAMCAPACITY];
				du = new boolean[server.tree.length][PATHORAMCAPACITY];
				for(int j = 1; j < server.tree.length; ++j)
					for(int i = 0; i < PATHORAMCAPACITY; ++i)
						idens[j][i]=Utils.toInt(server.tree[j][i].iden);
				
				for(int j = 1; j < server.tree.length; ++j)
					for(int i = 0; i < PATHORAMCAPACITY; ++i)
						du[j][i]=server.tree[j][i].isDummy;
				
				stash = new int[server.stash.length];
				for(int j = 0; j < server.stash.length; ++j)
					stash[j]=Utils.toInt(server.stash[j].iden);


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
		System.out.println(Arrays.toString(xor(gen.stash, eva.stash)));
		//System.out.print("\n");
		//System.out.println(Arrays.toString(posMap));
		
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