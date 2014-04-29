package oram.treeoram;

import java.security.SecureRandom;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import oram.OramParty.Mode;
import oram.OramParty.Party;
import test.Utils;


public class TestTreeOram {
	final int N =7;
	final int capacity = 10;
	int[] posMap = new int[N+1];
	int writeCount = N*2;
	int readCount = N*2;
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
		public boolean[][] du;
		public void run() {
			try {
				listen(54321);

				int data[] = new int[N+1];
				//TreeOramClient<GCSignal> client = new TreeOramClient<GCSignal>(is, os, N, dataSize, Party.CLIENT, capacity, Mode.REAL);
				TreeOramClient<Boolean> client = new TreeOramClient<Boolean>(is, os, N, dataSize, Party.CLIENT, capacity, Mode.TEST);
				System.out.println("logN:"+client.logN+", N:"+client.N);

				idens = new int[client.tree.length][capacity];
				du = new boolean[client.tree.length][capacity];

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
					Assert.assertTrue(Utils.toInt(b) == data[element]);
					if(Utils.toInt(b) != data[element])
						System.out.println("inconsistent: "+element+" "+Utils.toInt(b) + " "+data[element]);
					posMap[element] = newValue;
				}
				

				for(int j = 1; j < client.tree.length; ++j)
					for(int i = 0; i < capacity; ++i)
						idens[j][i]=Utils.toInt(client.tree[j][i].iden);

				for(int j = 1; j < client.tree.length; ++j)
					for(int i = 0; i < capacity; ++i)
						du[j][i]=client.tree[j][i].isDummy;
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
				//TreeOramServer<GCSignal> server = new TreeOramServer<GCSignal>(is, os, N, dataSize, Party.SERVER, capacity, Mode.REAL);
				TreeOramServer<Boolean> server = new TreeOramServer<Boolean>(is, os, N, dataSize, Party.SERVER, capacity, Mode.TEST);

				idens = new int[server.tree.length][capacity];
				du = new boolean[server.tree.length][capacity];
				
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

				for(int j = 1; j < server.tree.length; ++j)
					for(int i = 0; i < capacity; ++i)
						idens[j][i]=Utils.toInt(server.tree[j][i].iden);

				for(int j = 1; j < server.tree.length; ++j)
					for(int i = 0; i < capacity; ++i)
						du[j][i]=server.tree[j][i].isDummy;


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