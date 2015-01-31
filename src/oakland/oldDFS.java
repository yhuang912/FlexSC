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
//
//import compiledlib.stack.BoolArray;
//import compiledlib.stack.Stack;
//import compiledlib.stack.StackNode;
//
//
//public class oldDFS extends TestHarness {
//
//	static double[][] a;
//	static int finalRes = 0;
//	static Random rng = new Random(123);
//
//	static int v = 5;
//	static int e = 8;
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
//		SecureArray<Boolean> dis = new SecureArray<Boolean>(lib.getEnv(), v, 1);
//		dis.setInitialValue(0);
//
//		BoolArray ba = new BoolArray(lib.getEnv());
//		StackNode<BoolArray> node = new StackNode<BoolArray>(lib.getEnv(), logoramsize, ba);
//		CircuitOram<Boolean> oram = new CircuitOram<Boolean>(lib.getEnv(), logoramsize, node.numBits());
//		Stack<BoolArray> stack = new Stack<BoolArray>(lib.getEnv(), logoramsize, 
//				new BoolArray(lib.getEnv()), oram);
//		BoolArray tmp = new BoolArray(lib.getEnv());tmp.data = lib.toSignals(0, logoramsize);
//		stack.push(tmp, lib.SIGNAL_ONE);
//		if(m == Mode.COUNT) {
//			((PMCompEnv) lib.getEnv()).statistic.flush();
//		}
//		Boolean traversingNode = lib.SIGNAL_ZERO;
//		Boolean[] next = lib.toSignals(0, bitLength);
//		for(int i = 0; i < 2*v+e; ++i) {
//			Boolean traNd = traversingNode;
//			Boolean ntvrsing = lib.not(traNd);
//			BoolArray t = stack.pop(ntvrsing);
//			Boolean[] res = dis.read(t.data);
//			Boolean explored = res[0];
////			System.out.println(Utils.toInt(lib.getEnv().outputToAlice(t.data))+" "+lib.getEnv().outputToAlice(explored));
//			Boolean firstguard = lib.and(ntvrsing, lib.not(explored));
//			dis.write(t.data, lib.mux(res, lib.toSignals(1, 1), firstguard));
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
//
//	public static Boolean[][]nextEdge (Boolean[] next, SecureArray<Boolean> graph) {
//		Boolean[] res = graph.read(next);
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
//					andgates = ((PMCompEnv) env).statistic.andGate;
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
//		for(int i = 7; i <=20; i++) {
//			oldDFS.v = 1<<i;
//			oldDFS.e = 3*v;
//			oldDFS.bitLength = 16;
//			oldDFS.logoramsize = 16;//(int) (Math.log((v+e))/Math.log(2));
//			
//			new oldDFS().runThreads();
//		}
//	}
//}