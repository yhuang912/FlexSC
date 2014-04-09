package oramgc.pathoram;

import java.security.SecureRandom;
import java.util.Arrays;

import oramgc.OramParty.BlockInBinary;
import oramgc.OramParty.Party;
import test.Utils;


public class TestPathOram {
	
	final int N = 8;
	final int capacity = 4;
	int[] posMap = new int[N+3];
	int ll = 0;
	int llread = 8;
	public TestPathOram(){
		SecureRandom rng = new SecureRandom();
		for(int i = 0; i < posMap.length; ++i)
			posMap[i] = rng.nextInt(N);
	}
	SecureRandom rng = new SecureRandom();
	
	final int PATHORAMCAPACITY = 4;
	class GenRunnable extends network.Server implements Runnable {
		GenRunnable () {
		}
		public int[][] idens;
		public int[] stash;
		public void run() {
			try {
				listen(54321);
				int data[] = new int[N+1];

				PathOramClient client = new PathOramClient(is, os, N, 4, Party.CLIENT);
				System.out.println("logN:"+client.logN+", N:"+client.N);
				
				
				for(int i = 0; i < ll; ++i) {
					int element = Math.abs(i % (N-1)) +1;

					int oldValue = posMap[element];
					int newValue = rng.nextInt(1<<client.lengthOfPos);
					System.out.println(element+" "+oldValue+" "+newValue);
					client.write(element, oldValue, newValue, Utils.fromInt(element, client.lengthOfData));
					data[element] = element;
					posMap[element] = newValue;
				}
				
				for(int i = 1; i < N; ++i) {
					int element = i;
					int oldValue = posMap[element];
					int newValue = rng.nextInt(1<<client.lengthOfPos);
					
					BlockInBinary b = client.read(element, oldValue, newValue);
					
					if(Utils.toInt(b.data) != data[element])
						System.out.println("inconsistent: "+element+" "+Utils.toInt(b.data) + " "+data[element]+" "+Utils.toInt(b.iden));
					posMap[element] = newValue;
				}

				
				idens = new int[client.tree.length][PATHORAMCAPACITY];
				for(int j = 1; j < client.tree.length; ++j)
					for(int i = 0; i < PATHORAMCAPACITY; ++i)
						idens[j][i]=Utils.toInt(client.tree[j][i].iden);
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
		public int[] stash;
		EvaRunnable () {
		}

		public void run() {
			try {
				connect("localhost", 54321);				
				PathOramServer server = new PathOramServer(is, os, N, 4, Party.SERVER);
				
				for(int i = 0; i < ll; ++i) {
					int element = Math.abs(i % (N-1)) +1;
					int oldValue = posMap[element];
					server.write(oldValue);
				}
				
				
				for(int i = 1; i < N; ++i){
					int element = i;
					int oldValue = posMap[element];
					server.read(oldValue);
				}
				
				idens = new int[server.tree.length][PATHORAMCAPACITY];
				for(int j = 1; j < server.tree.length; ++j)
					for(int i = 0; i < PATHORAMCAPACITY; ++i)
						idens[j][i]=Utils.toInt(server.tree[j][i].iden);
				
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

	
	public void runThreads() throws Exception {
		GenRunnable gen = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(10);
		tEva.start();
		tGen.join();
		
		System.out.println(" ");
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
		System.out.print(Arrays.toString(xor(gen.stash, eva.stash)));
		
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
	
	public static void main(String [ ] args) throws Exception{
		TestPathOram t = new TestPathOram();
		t.runThreads();
	}

	

}