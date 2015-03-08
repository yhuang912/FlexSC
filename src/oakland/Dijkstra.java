package oakland;

import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.Party;
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

	public static void insertEdge(SecureArray<Boolean> graph, IntegerLib<Boolean> lib, int index, int a, int b, int c) throws Exception {
		Boolean[] aa = lib.publicValue(a);
		Boolean[] bb = lib.publicValue(b);
		Boolean[] cc = lib.publicValue(c);
		Boolean[] ret = new Boolean[bitLength*3];
		System.arraycopy(aa, 0, ret, 0, bitLength);
		System.arraycopy(bb, 0, ret, bitLength, bitLength);
		System.arraycopy(cc, 0, ret, 2*bitLength, bitLength);
		graph.write(lib.publicValue(index), ret);
	}

	public static SecureArray<Boolean> makegraph(IntegerLib<Boolean> lib) throws Exception {
		SecureArray<Boolean> graph = new SecureArray<Boolean>(lib.getEnv(), v+e, 3*bitLength);
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

	public static void secureCompute(IntegerLib<Boolean> lib) throws Exception {
		SecureArray<Boolean> graph = makegraph(lib);
		SecureArray<Boolean> dis = new SecureArray<Boolean>(lib.getEnv(), v, bitLength);
		dis.write(lib.publicValue(0), lib.publicValue(0));
		dis.setInitialValue(2000);

		BoolArray ba = new BoolArray(lib.getEnv());
		PriorityQueueNode<BoolArray> node = new PriorityQueueNode<BoolArray>(lib.getEnv(), logoramsize, ba);
		CircuitOram<Boolean> oram = new CircuitOram<Boolean>(lib.getEnv(), v+e, node.numBits());
//System.out.println(node.keyvalue.value.numBits());
		PriorityQueue<BoolArray> pq = new PriorityQueue<BoolArray>(lib.getEnv(), logoramsize, 
				new BoolArray(lib.getEnv()), oram);

		BoolArray tmp1 = new BoolArray(lib.getEnv());
		pq.pqueue_op(lib.getEnv().inputOfAlice(Utils.fromInt(15, bitLength)), 
				tmp1, lib.SIGNAL_ZERO);
		pq.pqueue_op(lib.getEnv().inputOfAlice(Utils.fromInt(15, bitLength)), 
				tmp1, lib.SIGNAL_ONE);
		BoolArray tmp = new BoolArray(lib.getEnv());tmp.data = lib.toSignals(0, bitLength);
		pq.push(lib.toSignals(1000, bitLength), tmp, lib.SIGNAL_ONE);

		if(Flag.mode== Mode.COUNT) {
			((PMCompEnv) lib.getEnv()).statistic.flush();
		}
		Boolean traversingNode = lib.SIGNAL_ZERO;
		Boolean[] next = lib.toSignals(0, bitLength);
		Boolean[] currentV = lib.toSignals(0, bitLength);
		for(int i = 0; i < 1; ++i) {
			Boolean traNd = traversingNode;
			Boolean NtraNd = lib.not(traversingNode);
			compiledlib.priority_queue.KeyValue<BoolArray> t = pq.pop(NtraNd);
			t.key = lib.sub(lib.toSignals(1000, bitLength), t.key);
			Boolean[] disvalue = dis.read(t.value.data);
			Boolean newNode = lib.geq(disvalue, t.key);
			Boolean nested = lib.and(newNode, NtraNd);
			traversingNode = lib.mux(traversingNode, lib.SIGNAL_ONE, nested);
			next = lib.mux(next, t.value.data, nested);
			dis.write(t.value.data, lib.mux(disvalue,t.key,nested));
			currentV = lib.mux(currentV	, t.key, nested);
			Boolean[][]e = nextEdge(next, graph);
			next = lib.mux(next, e[1], traNd);
			Boolean nodeEnds = lib.eq(e[1], lib.toSignals(-1, e[1].length));
			traversingNode = lib.mux(traversingNode, lib.SIGNAL_ZERO,lib.and(traNd, nodeEnds));
			Boolean secondNest = lib.and(traNd, lib.not(nodeEnds));
			
			Boolean[] dist = lib.add(currentV, e[2]);
			BoolArray tmp2 = new BoolArray(lib.getEnv());tmp2.data = e[0];
			pq.push(lib.sub(lib.toSignals(1000, bitLength), dist), tmp2, secondNest);	
		}
		if(Flag.mode!= Mode.COUNT){
			if(lib.getEnv().getParty() == Party.Alice) {
				for(int l = 0; l < v; ++l)
					System.out.print(Utils.toInt(lib.getEnv().outputToAlice(dis.read(lib.publicValue(l)))) + "\t");
				System.out.print("\n");
			}
			else {
				for(int l = 0; l < v; ++l)
					lib.outputToAlice(dis.read(lib.publicValue(l)));
			}
		}
		else  if (Flag.mode== Mode.COUNT) {
			((PMCompEnv) lib.getEnv()).statistic.andGate *= (v+e);
		}

	}

	public static Boolean[][]nextEdge (Boolean[] next, SecureArray<Boolean> graph) {
		Boolean[] res = graph.read(next);
		return new Boolean[][] {Arrays.copyOfRange(res, 0, bitLength), 
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

				if (Flag.mode== Mode.COUNT) {
					((PMCompEnv) env).statistic.flush();
				}

				double a = System.nanoTime();
				secureCompute(lib);

				if (Flag.mode== Mode.COUNT) {
					((PMCompEnv) env).statistic.finalize();
					andgates = ((PMCompEnv) env).statistic.andGate;
					encs = ((PMCompEnv) env).statistic.NumEncAlice;
				}
				else if (Flag.mode== Mode.REAL){
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

		if (Flag.mode== Mode.COUNT) {
			System.out.println(env.andgates + "\t" + env.encs);
		} else {
		}
	}

	public  static void main(String args[]) throws Exception {
		for(int i = 7; i <=20; i++) {
			Dijkstra.v = 1<<i;
			Dijkstra.e = 4*v;

			Dijkstra.logoramsize = (int) (Math.log((v+e))/Math.log(2));
			
			new Dijkstra().runThreads();
		}
	}
}