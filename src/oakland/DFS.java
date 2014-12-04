//package oakland;
//
//import flexsc.CompEnv;
//import flexsc.Mode;
//import flexsc.PMCompEnv;
//import flexsc.Party;
//import harness.TestHarness;
//
//import java.util.Arrays;
//import java.util.Random;
//
//import oram.CircuitOram;
//import oram.SecureArray;
//
//import org.junit.Test;
//
//import circuits.arithmetic.IntegerLib;
//import compiledlib.stack.BoolArray;
//import compiledlib.stack.Stack;
//import compiledlib.stack.StackNode;
//
//
//public class DFS extends TestHarness {
//
//	static double[][] a;
//	static int finalRes = 0;
//	static Random rng = new Random(123);
//
//	static int v = 1<<15;
//	static int e = 3*v;
//	static int bitLength = 16;
//	static int logoramsize = 16;//(int) (Math.log((v+e))/Math.log(2));
//
//	public static void insertEdge(SecureArray<Boolean> graph, IntegerLib<Boolean> lib, int index, int a, int b, int c) throws Exception {
//		Boolean[] aa = lib.publicValue(a);
//		Boolean[] bb = lib.publicValue(b);
//		Boolean[] cc = lib.publicValue(c);
//		Boolean[] ret = new Boolean[bitLength*3];
//		System.arraycopy(aa, 0, ret, 0, bitLength);
//		System.arraycopy(bb, 0, ret, bitLength, bitLength);
//		System.arraycopy(cc, 0, ret, 2*bitLength, bitLength);
//		graph.write(lib.publicValue(index), ret);
//	}
//
//	public static SecureArray<Boolean> makegraph(IntegerLib<Boolean> lib) throws Exception {
//		SecureArray<Boolean> graph = new SecureArray<Boolean>(lib.getEnv(), v+e, 3*bitLength);
//		insertEdge(graph, lib, 0, 1, 6, 10);
//		insertEdge(graph, lib, 1, 3, 8, 1);
//		insertEdge(graph, lib, 2, 1, 9, 1);
//		insertEdge(graph, lib, 3, 4, 11, 1);
//		insertEdge(graph, lib, 4, -1, -1, -1);
//		insertEdge(graph, lib, 6, 2, 7, 1);
//		insertEdge(graph, lib, 7, -1, -1, -1);
//		insertEdge(graph, lib, 8, -1, -1, -1);
//		insertEdge(graph, lib, 9, 3, -1, -1);
//		insertEdge(graph, lib, 11, -1, -1, -1);
//		return graph;
//	}
//
//	public static void secureCompute(IntegerLib<Boolean> lib) throws Exception {
//		SecureArray<Boolean> graph = makegraph(lib);
////		SecureArray<Boolean> dis = new SecureArray<Boolean>(lib.getEnv(), v, 1);
////		dis.setInitialValue(0);
//
//		BoolArray ba = new BoolArray(lib.getEnv());
//		StackNode<BoolArray> node = new StackNode<BoolArray>(lib.getEnv(), logoramsize, ba);
//		CircuitOram<Boolean> oram = new CircuitOram<Boolean>(lib.getEnv(), logoramsize, node.numBits());
//		Stack<BoolArray> stack = new Stack<BoolArray>(lib.getEnv(), logoramsize, 
//				new BoolArray(lib.getEnv()), oram);
//		BoolArray tmp = new BoolArray(lib.getEnv());tmp.data = lib.toSignals(0, bitLength);
//		stack.push(tmp, lib.SIGNAL_ONE);
//		if(m == Mode.COUNT) {
//			((PMCompEnv) lib.getEnv()).statistic.flush();
//		}
//		Boolean traversingNode = lib.SIGNAL_ZERO;
//		
//		CircuitOram<Boolean> dis = new CircuitOram<Boolean>(lib.getEnv(), logoramsize, bitLength*2);
//		Oramstate s = new Oramstate();
//		s.logN =logoramsize;
//		s.level = lib.toSignals(0, dis.lengthOfIden);
//		s.pos = lib.randBools(dis.lengthOfPos);
//		s.preassignedPos = lib.randBools(dis.lengthOfPos);
//		
//		Boolean[] next = lib.toSignals(0, bitLength);
//		Boolean finishedr = lib.SIGNAL_ZERO;
//		Boolean finishedw = lib.SIGNAL_ZERO;
//		for(int i = 0; i < 1; ++i) {
//			Boolean traNd = traversingNode;
//			Boolean ntvrsing = lib.not(traNd);
//			BoolArray t = stack.pop(ntvrsing);
//			Boolean[][]ret = read(dis, s, t.data, lib.getEnv(), lib.and(finishedw, lib.and(ntvrsing, lib.not(finishedr))));
//			Boolean[] res = ret[0];
//			finishedr = ret[1][0];
//					
//			Boolean explored = res[0];
////			System.out.println(Utils.toInt(lib.getEnv().outputToAlice(t.data))+" "+lib.getEnv().outputToAlice(explored));
//			Boolean firstguard = lib.and(ntvrsing, lib.not(explored));
//			firstguard = lib.and(firstguard, finishedr);
////			dis.write(t.data, lib.mux(res, lib.toSignals(1, 1), firstguard));
//			finishedw = write(dis, s, t.data, lib.mux(res, lib.toSignals(1, res.length), firstguard), lib.getEnv(), firstguard);
//			traversingNode = lib.mux(traNd, lib.SIGNAL_ONE, firstguard);
//			next = lib.mux(next, t.data, firstguard);
//			
//			Boolean[][] e = nextEdge(next, graph);
//			next = lib.mux(next, e[1], traNd);
//			Boolean terminate = lib.eq(lib.toSignals(-1, next.length), next);
//			traversingNode = lib.mux(traversingNode, lib.SIGNAL_ZERO, lib.and(terminate, traNd));
//			BoolArray tmp2 = new BoolArray(lib.getEnv());tmp2.data = e[0];
//			
//			stack.push(tmp2, lib.and(traNd,lib.not(terminate)));
//		}
//	}	
//	
//	
//	public static class Oramstate
//	{
//		int logN;
//		public Boolean[] level;
//		public Boolean[] pos;
//		public Boolean[] preassignedPos;
//	}
//	
//	static Boolean[][] read(CircuitOram<Boolean> oram, Oramstate state, Boolean[] iden, CompEnv<Boolean> env, Boolean condition) {
//		IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
//		
//		Boolean[] targetIden = lib.rightPrivateShift(iden, lib.sub(lib.toSignals(state.logN, state.level.length), state.level));
//		Boolean[] res = oram.conditionalReadAndRemove(targetIden, state.pos, condition);
//		Boolean[] parent = lib.rightPrivateShift(iden, lib.sub(lib.toSignals(state.logN-1, state.level.length), state.level));
//		Boolean goRight = parent[0];
//		Boolean[] tmpStatePos = lib.mux(state.pos, Arrays.copyOf(res, state.logN), lib.not(goRight));
//		tmpStatePos = lib.mux(tmpStatePos, Arrays.copyOfRange(res, state.logN, 2*state.logN), goRight);
//		Boolean[] tmpos = lib.randBools(state.logN);
//		Boolean[] forLeft = lib.mux(Arrays.copyOf(res, state.logN), tmpos, lib.not(goRight));
//		Boolean[] forRight = lib.mux(Arrays.copyOfRange(res, state.logN, state.logN*2), tmpos, goRight);
//		System.arraycopy(forLeft, 0, res, 0, state.logN);
//		System.arraycopy(forRight, 0, res, state.logN, state.logN);
////		oram.putBack(targetIden, state.preassignedPos, res);
//		oram.conditionalPutBack(targetIden, state.preassignedPos, res, condition);
////		state.preassignedPos = tmpos;
//		Boolean[][] ret = new Boolean[2][];
//		ret[0] = res;
//		ret[1] = new Boolean[]{lib.eq(state.level, lib.toSignals(state.logN, state.logN))};
//		
//		
//		state.level = lib.mux(state.level, lib.incrementByOne(state.level), condition);
//		state.pos = lib.mux(state.pos, tmpStatePos, condition);
//		state.preassignedPos = lib.mux(state.preassignedPos, tmpos, condition);//tmpos;
//		
//		return ret;
//	}
//	
//	static Boolean write(CircuitOram<Boolean> oram, Oramstate state, Boolean[] iden, Boolean[] data, CompEnv<Boolean> env, Boolean condition) {
//		IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
//		Boolean ret = lib.eq(state.level, lib.toSignals(state.logN, state.logN));
//		Boolean[] targetIden = lib.rightPrivateShift(iden, lib.sub(lib.toSignals(state.logN, state.level.length), state.level));
//		Boolean[] res = oram.conditionalReadAndRemove(targetIden, state.pos, condition);
//		Boolean[] parent = lib.rightPrivateShift(iden, lib.sub(lib.toSignals(state.logN-1, state.level.length), state.level));
//		Boolean goRight = parent[0];
//		Boolean[] tmpStatePos = lib.mux(state.pos, Arrays.copyOf(res, state.logN), lib.not(goRight));
//		tmpStatePos = lib.mux(tmpStatePos, Arrays.copyOfRange(res, state.logN, 2*state.logN), goRight);
//		Boolean[] tmpos = lib.randBools(state.logN);
//		Boolean[] forLeft = lib.mux(Arrays.copyOf(res, state.logN), tmpos, lib.not(goRight));
//		Boolean[] forRight = lib.mux(Arrays.copyOfRange(res, state.logN, state.logN*2), tmpos, goRight);
//		System.arraycopy(forLeft, 0, res, 0, state.logN);
//		System.arraycopy(forRight, 0, res, state.logN, state.logN);
////		oram.putBack(targetIden, state.preassignedPos, res);
//		oram.conditionalPutBack(targetIden, state.preassignedPos, lib.mux(res, data, ret), condition);
////		state.preassignedPos = tmpos;
////		Boolean[][] ret = new Boolean[2][];
////		ret[0] = res;
////		ret[1] = new Boolean[]{lib.eq(state.level, lib.toSignals(state.logN, state.logN))};
//		
//		state.level = lib.mux(state.level, lib.incrementByOne(state.level), condition);
//		state.pos = lib.mux(state.pos, tmpStatePos, condition);
//		state.preassignedPos = lib.mux(state.preassignedPos, tmpos, condition);//tmpos;
//		
//		return ret;
//	}
//	
//
//	public static Boolean[][]nextEdge (Boolean[] next, SecureArray<Boolean> graph) {
//		Boolean[] res = graph.readAndRemove(next);
//		return new Boolean[][] {Arrays.copyOfRange(res, 0, bitLength), 
//				Arrays.copyOfRange(res, bitLength, bitLength*2),
//				Arrays.copyOfRange(res,bitLength*2, bitLength*3)};
//	} 
//
//	public static class GenRunnable extends network.Server implements Runnable {
//		IntegerLib lib;
//		double z;
//
//		public void run() {
//			try {
//				listen(54321);
//				@SuppressWarnings("unchecked")
//				CompEnv gen = CompEnv.getEnv(m, Party.Alice, is, os);
//
//				lib = new IntegerLib(gen);
//
//				secureCompute(lib);
//
//				disconnect();
//			} catch (Exception e) {
//				e.printStackTrace();
//				System.exit(1);
//			}
//		}
//	}
//
//	public static class EvaRunnable extends network.Client implements Runnable {
//		IntegerLib lib;
//		public double andgates;
//		public double encs;
//
//		public void run() {
//			try {
//				connect("localhost", 54321);
//				@SuppressWarnings("unchecked")
//				CompEnv env = CompEnv.getEnv(m, Party.Bob, is, os);
//
//				lib = new IntegerLib(env);
//
//				if (m == Mode.COUNT) {
//					((PMCompEnv) env).statistic.flush();
//				}
//
//				double a = System.nanoTime();
//				secureCompute(lib);
//
//				if (m == Mode.COUNT) {
//					((PMCompEnv) env).statistic.finalize();
//					andgates = ((PMCompEnv) env).statistic.andGate*(v*Math.log(v)/Math.log(2)+e);
//					encs = ((PMCompEnv) env).statistic.NumEncAlice;
//				}
//				else if (m == Mode.REAL){
//					System.out.println((System.nanoTime()-a)/1000000000);
//				}
//
//				disconnect();
//			} catch (Exception e) {
//				e.printStackTrace();
//				System.exit(1);
//			}
//		}
//	}
//
//	@Test
//	public void runThreads() throws Exception {
//		GenRunnable gen = new GenRunnable();
//		EvaRunnable env = new EvaRunnable();
//		m = Mode.COUNT;
//		Thread tGen = new Thread(gen);
//		Thread tEva = new Thread(env);
//		tGen.start();
//		Thread.sleep(1);
//		tEva.start();
//		tGen.join();
//
//		if (m == Mode.COUNT) {
//			System.out.println(env.andgates + "\t" + env.encs);
//		} else {
//		}
//	}
//
//	public  static void main(String args[]) throws Exception {
//		for(int i = 9; i <=16; i++) {
//			DFS.v = 1<<i;
//			DFS.e = i*v;
//			DFS.bitLength = i;
//			DFS.logoramsize = i;//(int) (Math.log((v+e))/Math.log(2));
//			System.out.print(i+"\t");
//			new DFS().runThreads();
//		}
//	}
//}