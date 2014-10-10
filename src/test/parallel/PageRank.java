package test.parallel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;

import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;
import test.Utils;
import circuits.Comparator;
import circuits.IntegerLib;
import circuits.arithmetic.FloatLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;
import gc.BadLabelException;

public class PageRank<T> implements ParallelGadget<T> {
	static int FLOAT_V = 20;
	static int FLOAT_P = 11;
	static int INT_LEN = 32;
	static int ITERATIONS = 3;

	private boolean[][][] getInput(int inputLength) throws IOException {
		int[] u = new int[inputLength];
		int[] v = new int[inputLength];
		BufferedReader br = new BufferedReader(new FileReader("PageRank.in"));
		for (int i = 0; i < inputLength; i++) {
			String readLine = br.readLine();
			String[] split = readLine.split(" ");
			u[i] = Integer.parseInt(split[0]);
			v[i] = Integer.parseInt(split[1]);
		}
		boolean[][] a = new boolean[u.length][];
		boolean[][] b = new boolean[v.length][];
		for(int i = 0; i < u.length; ++i) {
			a[i] = Utils.fromInt(u[i], INT_LEN);
			b[i] = Utils.fromInt(v[i], INT_LEN);
		}
		boolean[][][] ret = new boolean[2][][];
		ret[0] = a;
		ret[1] = b;
		return ret;
	}

	private Object[] performOTAndReturnMachineInputs(int inputLength,
			int machines, boolean isGen, CompEnv<T> env)
			throws IOException {
		T[][] tu = env.newTArray(inputLength /* number of entries in the input */, 0);
		T[][] tv = env.newTArray(inputLength /* number of entries in the input */, 0);
		if (isGen) {
			for(int i = 0; i < tu.length; ++i)
				tu[i] = env.inputOfBob(new boolean[INT_LEN]);
			for(int i = 0; i < tv.length; ++i)
				tv[i] = env.inputOfBob(new boolean[INT_LEN]);
		} else {
			boolean[][][] input = getInput(inputLength);
			for(int i = 0; i < tu.length; ++i)
				tu[i] = env.inputOfBob(input[0][i]);
			for(int i = 0; i < tv.length; ++i)
				tv[i] = env.inputOfBob(input[1][i]);
		}
		Object[] inputU = new Object[machines];
		Object[] inputV = new Object[machines];

		for(int i = 0; i < machines; ++i) {
			inputU[i] = Arrays.copyOfRange(tu, i * tu.length / machines, (i + 1) * tu.length / machines);
			inputV[i] = Arrays.copyOfRange(tv, i * tv.length / machines, (i + 1) * tv.length / machines);
		}
		Object[] input = new Object[2][];
		input[0] = inputU;
		input[1] = inputV;
		return input;
	}

	@Override
	public void sendInputToMachines(int inputLength,
			int machines,
			boolean isGen, 
			CompEnv<T> env,
			OutputStream[] os) throws IOException {
		Object[] input = performOTAndReturnMachineInputs(inputLength, machines, isGen, env);
		Object[] inputU = (Object[]) input[0];
		Object[] inputV = (Object[]) input[1];
		for (int i = 0; i < machines; i++) {
			T[][] gcInputU = (T[][]) inputU[i];
			T[][] gcInputV = (T[][]) inputV[i];
			NetworkUtil.writeInt(os[i], gcInputU.length);
			NetworkUtil.writeInt(os[i], gcInputU[0].length);
			for (int j = 0; j < gcInputU.length; j++)
				for (int k = 0; k < gcInputU[j].length; k++)
					NetworkUtil.send(os[i], gcInputU[j][k], env);
					// gcInputU[j][k].send(os[i]);
	
			for (int j = 0; j < gcInputV.length; j++)
				for (int k = 0; k < gcInputV[j].length; k++)
					NetworkUtil.send(os[i], gcInputV[j][k], env);
					// gcInputV[j][k].send(os[i]);
			os[i].flush();
		}
	}

	@Override
	public Object readInputFromMaster(int inputLength, int inputSize,
			InputStream masterIs,
			CompEnv<T> env) throws IOException {
		T[][] gcInputU = env.newTArray(inputLength, inputSize);// 'new GCSignal[inputLength][inputSize];
		T[][] gcInputV = env.newTArray(inputLength, inputSize);// new GCSignal[inputLength][inputSize];
		for (int j = 0; j < inputLength; j++)
			for (int k = 0; k < inputSize; k++)
				gcInputU[j][k] = NetworkUtil.read(masterIs, env);// GCSignal.receive(masterIs);
		for (int j = 0; j < inputLength; j++)
			for (int k = 0; k < inputSize; k++)
				gcInputV[j][k] = NetworkUtil.read(masterIs, env);// GCSignal.receive(masterIs);
		Object[] ret = new Object[2];
		ret[0] = gcInputU;
		ret[1] = gcInputV;
	    return ret;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> void compute(int machineId, Machine machine, final CompEnv<T> env)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, IOException,
			BadCommandException, BadLabelException {

		
		
		
		// TODO(kartiknayak): introduce isVertex
		
		
		
		
		
		
		
		
		T[][] u = (T[][]) ((Object[]) machine.input)[0];
		T[][] v = (T[][]) ((Object[]) machine.input)[1];
		T[][] pr = (T[][]) env.newTArray(u.length, u[0].length);
		T[][] l = (T[][]) env.newTArray(u.length, u[0].length);

		PageRankNode<T>[] aa = (PageRankNode<T>[]) Array.newInstance(PageRankNode.class, u.length);
		for (int i = 0; i < aa.length; i++) {
			aa[i] = new PageRankNode<T>(u[i], v[i], env);
		}

		// set initial pagerank
		new SetInitialPageRankGadget<T>(env, machine)
				.setInputs(aa)
				.compute();

		T[][] data;
		// 1. Compute number of neighbors for each vertex

        // print(machineId, env, aa);
		// Sort to get edges followed by the vertex
		new SortGadget<T>(env, machine)
				.setInputs(aa, PageRankNode.getComparator(env, true /* isVertexLast */))
				.compute();
		// Utils.unflatten(data, v, pr, l);

		/*new ComputeL<>(env, machine)
			.setInputs(u, v, l)
			.compute();*/



//		// Compute prefixSum for L
//		new PrefixSumGadget<T>(env, machine)
//				.setInputs(l, new IntegerLib<T>(env))
//				.compute();
//
//		// Weird sort
//		// key is v
//		data = (T[][]) new SortGadget<T>(env, machine)
//				.setInputs(v, firstSortComparator, u, pr, l)
//				.compute();
//		Utils.unflatten(data, u, pr, l);
//
//		// Subtract to obtain l
//		new SubtractGadgetForPageRank<>(env, machine)
//				.setInputs(l)
//				.compute();

		/*for (int i = 0; i < ITERATIONS; i++) {
			// 2. Write weighted PR to edges
			// Sort so that all vertices are followed by edges
			data = (T[][]) new SortGadget<T>(env, machine)
					.setInputs(u, secondSortComparator, v, pr, l)
					.compute();
			Utils.unflatten(data, v, pr, l);
	
			// Write PR to edge
			new WritePrPartToEdge<>(env, machine)
					.setInputs(u, v, pr, l)
					.compute();



			// 3. Compute PR based on edges
			new SwapNonVertexEdges<T>(env, machine)
					.setInputs(u, v, pr, true /* setVertexPrToZero )
					.compute();
	
			data = (T[][]) new SortGadget<T>(env, machine)
				.setInputs(u, firstSortComparator, v, pr, l)
				.compute();
			Utils.unflatten(data, v, pr, l);
	
			// Compute prefixSum for pr
			new PrefixSumGadget<T>(env, machine)
				.setInputs(pr, new FloatLib<T>(env, FLOAT_V, FLOAT_P))
				.compute();

			// get all vertices together to subtract
			data = (T[][]) new SortGadget<T>(env, machine)
					.setInputs(v, secondSortComparator, u, pr, l)
					.compute();
			Utils.unflatten(data, u, pr, l);
	
			new SubtractGadgetForHistogram<T>(env, machine)
				.setInputs(pr, new FloatLib<T>(env, FLOAT_V, FLOAT_P))
				.compute();
	
			new SwapNonVertexEdges<T>(env, machine)
				.setInputs(u, v, pr, false /* setVertexPrToZero )
				.compute();
		}*/

		if (Mode.COUNT.equals(env.getMode())) {
			Statistics a = ((PMCompEnv) env).statistic;
			a.finalize();
			System.out.println(machineId + ": " + a.andGate + " " + a.NumEncAlice);
			System.out.println("ENVS " + PMCompEnv.ENVS_USED);
		} else {
			if (machineId == 0) {
				print(machineId, env, aa);
			}
		}
	}

	private <T> void print(int machineId, final CompEnv<T> env, T[][] u, T[][] v,
			T[][] pr, T[][] l) throws IOException, BadLabelException {
		for (int i = 0; i < pr.length; i++) {
			int a = Utils.toInt(env.outputToAlice(u[i]));
			int b = Utils.toInt(env.outputToAlice(v[i]));
			double c2 = Utils.toFloat(env.outputToAlice(pr[i]), FLOAT_V, PageRank.FLOAT_P);
			int d = Utils.toInt(env.outputToAlice(l[i]));
			if (Party.Alice.equals(env.party)) {
				System.out.println(machineId + ": " + a + ", " + b + "\t" + c2 + "\t" + d);
			}
	    }
	}

	public static <T> void print(int machineId, final CompEnv<T> env, PageRankNode<T>[] pr) throws IOException, BadLabelException {
		for (int i = 0; i < pr.length; i++) {
			int a = Utils.toInt(env.outputToAlice(pr[i].u));
			int b = Utils.toInt(env.outputToAlice(pr[i].v));
			double c2 = Utils.toFloat(env.outputToAlice(pr[i].pr), FLOAT_V, PageRank.FLOAT_P);
			int d = Utils.toInt(env.outputToAlice(pr[i].l));
			if (Party.Alice.equals(env.party)) {
				System.out.println(machineId + ": " + a + ", " + b + "\t" + c2 + "\t" + d);
			}
	    }
	}

}