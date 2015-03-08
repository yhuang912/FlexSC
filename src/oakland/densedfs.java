package oakland;

import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.Party;
import harness.TestHarness;

import java.util.Arrays;

import org.junit.Test;

import circuits.BitonicSortLib;
import circuits.arithmetic.IntegerLib;

public class densedfs  extends TestHarness {

	static public class stack
	{
		public stack(CompEnv<Boolean> env, int bitlength, int cap) {
			this.bitlength = bitlength;
			this.keysize = (int) (Math.log(cap)/Math.log(2))+4;
			this.env = env;
			sortLib = new BitonicSortLib<Boolean>(env);
			data = new Boolean[cap][];
			dummy = sortLib.zeros(cap);
			for(int i = 0; i < cap; ++i)
				data[i] = sortLib.zeros(bitlength);
		}
		
		public int keysize;
		public int bitlength;
		CompEnv<Boolean> env;
		BitonicSortLib<Boolean> sortLib;
		public Boolean[][] data;
		public Boolean[] dummy;
		public void push(Boolean[][] newData, Boolean[] newDummy) {
			
			preprocess(newData, newDummy);
			Boolean[][] groupedData = new Boolean[data.length+newData.length][];
			System.arraycopy(data, 0, groupedData, 0, data.length);
			System.arraycopy(newData, 0, groupedData, data.length, newData.length);
			
			Boolean[] groupedDummy = new Boolean[dummy.length+newDummy.length];
			System.arraycopy(dummy, 0, groupedDummy, 0, dummy.length);
			System.arraycopy(newDummy, 0, groupedDummy, dummy.length, newDummy.length);
			
			Boolean[][]key = new Boolean[data.length+newData.length][];
			for(int i = 0; i < key.length; ++i)
				key[i] = sortLib.toSignals(i, keysize);
			for(int i = 0; i < data.length; ++i)
				key[i][keysize-2] = sortLib.not(dummy[i]);
			for(int i = 0; i < newData.length; ++i) {
				key[i+data.length][keysize-2] = sortLib.not(newDummy[i]);
				key[i+data.length][keysize-3] = sortLib.SIGNAL_ONE;//sortLib.not(newDummy[i]);
			}
			Boolean[][]key1 = new Boolean[data.length+newData.length][];
			for(int i = 0; i < key1.length; ++i)
				key1[i] = sortLib.xor(sortLib.zeros(key[i].length), key[i]);
			
			
			sortLib.sortWithPayload(key, groupedData, sortLib.SIGNAL_ONE);//assume it is stable for now.
			sortLib.sortWithPayload(key1, groupedDummy, sortLib.SIGNAL_ONE);//assume it is stable for now.
			data = Arrays.copyOf(groupedData, data.length);
			dummy = Arrays.copyOf(groupedDummy, dummy.length);

//			for(int i = 0; i < 32; ++i)
//				if(env.getParty() == Party.Alice)
//					if(dummy[i])
//						System.out.print(Utils.toInt(env.outputToAlice(data[i])) +" ");
//				else if(dummy[i]) env.outputToAlice(data[i]);
//			if(env.getParty() == Party.Alice)
//			System.out.println("\n");
		}
		
		public void preprocess(Boolean[][] newData, Boolean[] newDummy) {
			Boolean[][] localData = new Boolean[data.length][];
			for(int i = 0; i < localData.length; ++i)
				localData[i] = sortLib.xor(data[i], sortLib.zeros(data[i].length));
			
			Boolean[] groupedDummy = new Boolean[dummy.length+newDummy.length];
			Boolean[] localDummy = new Boolean[data.length];
			localDummy = sortLib.xor(dummy, sortLib.zeros(dummy.length));
			System.arraycopy(localDummy, 0, groupedDummy, 0, localDummy.length);
			System.arraycopy(newDummy, 0, groupedDummy, localDummy.length, newDummy.length);
		
		
			
			Boolean[][] groupedData = new Boolean[localData.length+newData.length][];
			System.arraycopy(data, 0, groupedData, 0, localData.length);
			System.arraycopy(newData, 0, groupedData, localData.length, newData.length);
			for(int i = 0; i < groupedData.length; ++i){
				groupedData[i] = sortLib.leftPublicShift(groupedData[i], 1);
				if(i < localData.length)
					groupedData[i][0] = sortLib.SIGNAL_ONE;
				else groupedData[i][0] = sortLib.SIGNAL_ZERO;
			}
			
			Boolean[][]key = new Boolean[data.length+newData.length][];
			for(int i = 0; i < key.length; ++i)
				key[i] = sortLib.toSignals(i, keysize);
			
//			for(int i = 0; i < 32; ++i)
//				if(env.getParty() == Party.Alice)
//					if(groupedDummy[i])
//						System.out.print(Utils.toInt(env.outputToAlice(groupedData[i])) +" ");
//				else if(groupedDummy[i]) env.outputToAlice(data[i]);
//			if(env.getParty() == Party.Alice)System.out.println("|1\n");
			Boolean[][]groupedData1 = new Boolean[data.length+newData.length][];
			for(int i = 0; i < groupedData.length; ++i)
				groupedData1[i] = sortLib.xor(sortLib.zeros(groupedData[i].length), groupedData[i]);
			sortLib.sortWithPayload(groupedData, key, sortLib.SIGNAL_ZERO);
			sortLib.sortWithPayload(groupedData1, groupedDummy, sortLib.SIGNAL_ZERO);
			for(int i = 0; i < groupedData.length; ++i) {
//				Boolean same = sortLib.eq(Arrays.copyOfRange(groupedData[i], 1, groupedData[i].length),
//						Arrays.copyOfRange(groupedData[i+1], 1, groupedData[i+1].length));
				
			}
			
			
//			for(int i = 0; i < 32; ++i)
//				if(env.getParty() == Party.Alice)
//					if(groupedDummy[i])
//						System.out.print(Utils.toInt(env.outputToAlice(groupedData[i])) +" ");
//				else if(groupedDummy[i]) env.outputToAlice(data[i]);
//			if(env.getParty() == Party.Alice)System.out.println("|2\n");
			
		}
		public Boolean[] pop() {
			Boolean[] ret = sortLib.zeros(bitlength);
			Boolean poped = sortLib.SIGNAL_ZERO;
			for(int i = data.length-1; i >=0; --i) {
				Boolean toPop = sortLib.and(sortLib.not(poped), dummy[i]);
				dummy[i] = sortLib.mux(dummy[i], sortLib.SIGNAL_ZERO, toPop);
				ret = sortLib.mux(ret, data[i], toPop);
				poped = sortLib.or(poped, toPop);
			}
			return ret;
		}
	}
	
	
	static public void secureCompute(CompEnv<Boolean> env){
		IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
		stack s = new stack(env, 32, 32);
//		Boolean[][] data = new Boolean[4][];
//		for(int i = 0; i < 4; ++i)
//			data[i] = lib.toSignals(i+1, 32);
//		s.push(data, new Boolean[]{lib.SIGNAL_ONE,lib.SIGNAL_ZERO,lib.SIGNAL_ONE,lib.SIGNAL_ONE});
//		
//		for(int i = 0; i < 4; ++i)
//			data[i] = lib.toSignals(i+2, 32);
//		s.push(data, new Boolean[]{lib.SIGNAL_ONE,lib.SIGNAL_ZERO,lib.SIGNAL_ONE,lib.SIGNAL_ONE});
//		
//
//		for(int i = 0; i < 4; ++i)
//			data[i] = lib.toSignals(i+3, 32);
//		s.push(data, new Boolean[]{lib.SIGNAL_ONE,lib.SIGNAL_ZERO,lib.SIGNAL_ONE,lib.SIGNAL_ONE});

		
//		for(int i = 0; i < 7; ++i)
//			if(env.getParty() == Party.Alice)
//				System.out.println(lib.outputToAlice(s.pop()));
//			else lib.outputToAlice(s.pop());
		
		

		Boolean[][] graph = new Boolean[1][V];
		for(int i = 0; i < 1; ++i)
			for(int j = 0; j < V; ++j)
				graph[i][j] = env.inputOfAlice(false);
//		graph[0][1] = lib.SIGNAL_ONE;
//		graph[1][2] = lib.SIGNAL_ONE;
//		graph[1][3] = lib.SIGNAL_ONE;
//		graph[2][4] = lib.SIGNAL_ONE;
		
		int next = 0;
		for(int i = 0; i < 1; ++i) {
			Boolean[][] edges = new Boolean[V][];
			for(int j = 0; j < edges.length; ++j)
				edges[j] = lib.toSignals(j, 32);
			Boolean[] isdummy = new Boolean[V];
			s.push(edges, graph[next]);
			Boolean[] res = hash(s.pop(), env);
			next = (int) lib.outputToAlice(res);
//			if(env.getParty() == Party.Alice)
//				System.out.println(next);
		}
	}
	public static int V = 5;
	static Boolean[] hash(Boolean[] item, CompEnv<Boolean> env) {
		return item;
	}
	
	
	public static class GenRunnable extends network.Server implements Runnable {
		IntegerLib lib;
		double z;

		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv gen = CompEnv.getEnv(Flag.mode, Party.Alice, is, os);

				secureCompute(gen);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static class EvaRunnable extends network.Client implements Runnable {
		IntegerLib lib;
		public double andgates;
		public double encs;

		public void run() {
			try {
				connect("localhost", 54321);
				@SuppressWarnings("unchecked")
				CompEnv env = CompEnv.getEnv(Flag.mode, Party.Bob, is, os);

				if (Flag.mode == Mode.COUNT) {
					((PMCompEnv) env).statistic.flush();
				}

				double a = System.nanoTime();
				secureCompute(env);

				if (Flag.mode == Mode.COUNT) {
					((PMCompEnv) env).statistic.finalize();
					andgates =  ((PMCompEnv) env).statistic.andGate*V;
				}
				else if (Flag.mode == Mode.REAL){
					System.out.println((System.nanoTime()-a)/1000000000);
				}

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	@Test
	public void runThreads() throws Exception {
		for(int i = 9; i < 17; ++i) {
			V = 1<<i;
			GenRunnable gen = new GenRunnable();
			EvaRunnable env = new EvaRunnable();
			Thread tGen = new Thread(gen);
			Thread tEva = new Thread(env);
			tGen.start();
			Thread.sleep(1);
			tEva.start();
			tGen.join();

			
			if (Flag.mode == Mode.COUNT) {
				System.out.println(i+"\t"+env.andgates);
			} else {
			}
		}

	}
}
