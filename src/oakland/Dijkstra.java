package oakland;

import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.Party;
import gc.GCSignal;
import harness.TestHarness;

import java.util.Arrays;
import java.util.Random;

import oram.CircuitOram;
import oram.SecureArray;

import org.junit.Test;

import util.Utils;
import circuits.arithmetic.IntegerLib;

import compiledlib.priority_queue.BoolArray;
import compiledlib.priority_queue.PriorityQueue;
import compiledlib.priority_queue.PriorityQueueNode;


public class Dijkstra extends TestHarness {

	static double[][] a;
	static int finalRes = 0;
	static Random rng = new Random(123);

	static int v = 5;
	static int e = 8;
	static int bitLength = 16;
	static int logoramsize = (int) (Math.log((v+e))/Math.log(2));

	public static void insertEdge(SecureArray<GCSignal> graph, IntegerLib<GCSignal> lib, int index, int a, int b, int c) throws Exception {
		GCSignal[] aa = lib.publicValue(a);
		GCSignal[] bb = lib.publicValue(b);
		GCSignal[] cc = lib.publicValue(c);
		GCSignal[] ret = new GCSignal[bitLength*3];
		System.arraycopy(aa, 0, ret, 0, bitLength);
		System.arraycopy(bb, 0, ret, bitLength, bitLength);
		System.arraycopy(cc, 0, ret, 2*bitLength, bitLength);
		graph.write(lib.publicValue(index), ret);
	}

	public static SecureArray<GCSignal> makegraph(IntegerLib<GCSignal> lib) throws Exception {
		SecureArray<GCSignal> graph = new SecureArray<GCSignal>(lib.getEnv(), v+e, 3*bitLength);
		insertEdge(graph, lib, 0, 1, 6, 10);
		insertEdge(graph, lib, 1, 3, 8, 1);
		insertEdge(graph, lib, 2, 1, 9, 1);
		insertEdge(graph, lib, 3, 4, 11, 1);
		insertEdge(graph, lib, 4, -1, -1, -1);
		insertEdge(graph, lib, 6, 2, 7, 1);
		insertEdge(graph, lib, 7, -1, -1, -1);
		insertEdge(graph, lib, 8, -1, -1, -1);
		insertEdge(graph, lib, 9, 3, -1, -1);
		insertEdge(graph, lib, 11, -1, -1, -1);
		return graph;
	}

	public static void secureCompute(IntegerLib<GCSignal> lib) throws Exception {
		SecureArray<GCSignal> graph = makegraph(lib);
		SecureArray<GCSignal> dis = new SecureArray<GCSignal>(lib.getEnv(), v, bitLength);
		dis.write(lib.publicValue(0), lib.publicValue(0));
		dis.setInitialValue(2000);

		BoolArray ba = new BoolArray(lib.getEnv(), lib);
		PriorityQueueNode<BoolArray> node = new PriorityQueueNode<BoolArray>(lib.getEnv(), lib, logoramsize, ba);
		CircuitOram<GCSignal> oram = new CircuitOram<GCSignal>(lib.getEnv(), v+e, node.numBits());
//System.out.println(node.keyvalue.value.numBits());
		PriorityQueue<BoolArray> pq = new PriorityQueue<BoolArray>(lib.getEnv(), lib, logoramsize, 
				new BoolArray(lib.getEnv(),lib), oram);

		BoolArray tmp1 = new BoolArray(lib.getEnv(), lib);
		pq.pqueue_op(lib.getEnv().inputOfAlice(Utils.fromInt(15, bitLength)), 
				tmp1, lib.SIGNAL_ZERO);
		pq.pqueue_op(lib.getEnv().inputOfAlice(Utils.fromInt(15, bitLength)), 
				tmp1, lib.SIGNAL_ONE);
		BoolArray tmp = new BoolArray(lib.getEnv(), lib);tmp.data = lib.toSignals(0, bitLength);
		pq.push(lib.toSignals(1000, bitLength), tmp, lib.SIGNAL_ONE);

		if(Flag.mode == Mode.COUNT) {
//			((PMCompEnv) lib.getEnv()).statistic.flush();
		}
		GCSignal traversingNode = lib.SIGNAL_ZERO;
		GCSignal[] next = lib.toSignals(0, bitLength);
		GCSignal[] currentV = lib.toSignals(0, bitLength);
		Flag.sw.ands = 0;
		for(int i = 0; i < 4; ++i) {
			double a = System.nanoTime();
			GCSignal traNd = traversingNode;
			GCSignal NtraNd = lib.not(traversingNode);
			compiledlib.priority_queue.KeyValue<BoolArray> t = pq.pop(NtraNd);
			t.key = lib.sub(lib.toSignals(1000, bitLength), t.key);
			GCSignal[] disvalue = dis.read(t.value.data);
			GCSignal newNode = lib.geq(disvalue, t.key);
			GCSignal nested = lib.and(newNode, NtraNd);
			traversingNode = lib.mux(traversingNode, lib.SIGNAL_ONE, nested);
			next = lib.mux(next, t.value.data, nested);
			dis.write(t.value.data, lib.mux(disvalue,t.key,nested));
			currentV = lib.mux(currentV	, t.key, nested);
			GCSignal[][]e = nextEdge(next, graph);
			next = lib.mux(next, e[1], traNd);
			GCSignal nodeEnds = lib.eq(e[1], lib.toSignals(-1, e[1].length));
			traversingNode = lib.mux(traversingNode, lib.SIGNAL_ZERO,lib.and(traNd, nodeEnds));
			GCSignal secondNest = lib.and(traNd, lib.not(nodeEnds));
			GCSignal[] dist = lib.add(currentV, e[2]);
			BoolArray tmp2 = new BoolArray(lib.getEnv(), lib);tmp2.data = e[0];
			
			pq.push(lib.sub(lib.toSignals(1000, bitLength), dist), tmp2, secondNest);
			lib.getEnv().os.flush();
			System.out.println(Flag.sw.ands+" "+(System.nanoTime()-a)/1000000000);
		}
//		if(Flag.mode != Mode.COUNT && false){
//			if(lib.getEnv().getParty() == Party.Alice) {
//				for(int l = 0; l < v; ++l)
//					System.out.print(Utils.toInt(lib.getEnv().outputToAlice(dis.read(lib.publicValue(l)))) + "\t");
//				System.out.print("\n");
//			}
//			else {
//				for(int l = 0; l < v; ++l)
//					lib.outputToAlice(dis.read(lib.publicValue(l)));
//			}
//		}
//		else  if (Flag.mode == Mode.COUNT) {
////			((PMCompEnv) lib.getEnv()).statistic.andGate *= (v+e);
//		}

	}

	public static GCSignal[][]nextEdge (GCSignal[] next, SecureArray<GCSignal> graph) {
		GCSignal[] res = graph.read(next);
		return new GCSignal[][] {Arrays.copyOfRange(res, 0, bitLength), 
				Arrays.copyOfRange(res, bitLength, bitLength*2),
				Arrays.copyOfRange(res,bitLength*2, bitLength*3)};
	} 

	public static class GenRunnable extends network.Server implements Runnable {
		IntegerLib lib;
		double z;

		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv gen = CompEnv.getEnv(Party.Alice, is, os);

				lib = new IntegerLib(gen);

				secureCompute(lib);

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
				CompEnv env = CompEnv.getEnv(Party.Bob, is, os);

				lib = new IntegerLib(env);

				if (Flag.mode == Mode.COUNT) {
					((PMCompEnv) env).statistic.flush();
				}

				double a = System.nanoTime();
				secureCompute(lib);

				if (Flag.mode == Mode.COUNT) {
					((PMCompEnv) env).statistic.finalize();
					andgates = ((PMCompEnv) env).statistic.andGate;
					encs = ((PMCompEnv) env).statistic.NumEncAlice;
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
		GenRunnable gen = new GenRunnable();
		EvaRunnable env = new EvaRunnable();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(env);
		tGen.start();
		Thread.sleep(1);
		tEva.start();
		tGen.join();

		if (Flag.mode == Mode.COUNT) {
			System.out.println(env.andgates + "\t" + env.encs);
		} else {
		}
	}

	public  static void main(String args[]) throws Exception {
		for(int i = 10; i <=10; i++) {
			Dijkstra.v = 1<<i;
			Dijkstra.e = 3*v;

			Dijkstra.logoramsize = (int) (Math.log((v+e))/Math.log(2));
			
			new Dijkstra().runThreads();
		}
	}
}