package test.parallel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import circuits.IntegerLib;
import circuits.arithmetic.FloatLib;
import ot.IncorrectOtUsageException;
import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;
import test.Utils;
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

	private Object[] getInput(int inputLength) throws IOException {
		int[] u = new int[inputLength];
		int[] v = new int[inputLength];
		boolean[] isVertex = new boolean[inputLength];
		BufferedReader br = new BufferedReader(new FileReader("PageRank.in"));
		for (int i = 0; i < inputLength; i++) {
			String readLine = br.readLine();
			String[] split = readLine.split(" ");
			u[i] = Integer.parseInt(split[0]);
			v[i] = Integer.parseInt(split[1]);
			isVertex[i] = (Integer.parseInt(split[2]) == 1);
		}
		br.close();
		boolean[][] a = new boolean[u.length][];
		boolean[][] b = new boolean[v.length][];
		boolean[] c = new boolean[isVertex.length];
		for(int i = 0; i < u.length; ++i) {
			a[i] = Utils.fromInt(u[i], INT_LEN);
			b[i] = Utils.fromInt(v[i], INT_LEN);
			c[i] = isVertex[i];
		}
		Object[] ret = new Object[3];
		ret[0] = a;
		ret[1] = b;
		ret[2] = c;
		return ret;
	}

	private Object[] performOTAndReturnMachineInputs(int inputLength,
			int machines, boolean isGen, CompEnv<T> env)
			throws IOException, IncorrectOtUsageException {
		T[][] tu = env.newTArray(inputLength /* number of entries in the input */, 0);
		T[][] tv = env.newTArray(inputLength /* number of entries in the input */, 0);
		T[] tIsV = env.newTArray(inputLength /* number of entries in the input */);
		if (isGen) {
			for(int i = 0; i < tu.length; ++i)
				tu[i] = env.inputOfBob(new boolean[INT_LEN]);
			for(int i = 0; i < tv.length; ++i)
				tv[i] = env.inputOfBob(new boolean[INT_LEN]);
			tIsV = env.inputOfBob(new boolean[tIsV.length]);
		} else {
			Object[] input = getInput(inputLength);
			boolean[][] u = (boolean[][]) input[0];
			boolean[][] v = (boolean[][]) input[1];
			boolean[] isV = (boolean[]) input[2];
			for(int i = 0; i < tu.length; ++i)
				tu[i] = env.inputOfBob((boolean[]) u[i]);
			for(int i = 0; i < tv.length; ++i)
				tv[i] = env.inputOfBob((boolean[]) v[i]);
			tIsV = env.inputOfBob(isV);
		}
		Object[] inputU = new Object[machines];
		Object[] inputV = new Object[machines];
		Object[] inputIsVertex = new Object[machines];

		for(int i = 0; i < machines; ++i) {
			inputU[i] = Arrays.copyOfRange(tu, i * tu.length / machines, (i + 1) * tu.length / machines);
			inputV[i] = Arrays.copyOfRange(tv, i * tv.length / machines, (i + 1) * tv.length / machines);
			inputIsVertex[i] = Arrays.copyOfRange(tIsV, i * tIsV.length / machines, (i + 1) * tIsV.length / machines);
		}
		Object[] input = new Object[3];
		input[0] = inputU;
		input[1] = inputV;
		input[2] = inputIsVertex;
		return input;
	}

	@Override
	public void sendInputToMachines(int inputLength,
			int machines,
			boolean isGen, 
			CompEnv<T> env,
			OutputStream[] os) throws IOException, IncorrectOtUsageException {
		Object[] input = performOTAndReturnMachineInputs(inputLength, machines, isGen, env);
		Object[] inputU = (Object[]) input[0];
		Object[] inputV = (Object[]) input[1];
		Object[] inputIsVertex = (Object[]) input[2];
		for (int i = 0; i < machines; i++) {
			T[][] gcInputU = (T[][]) inputU[i];
			T[][] gcInputV = (T[][]) inputV[i];
			T[] gcInputIsVertex = (T[]) inputIsVertex[i];
			NetworkUtil.writeInt(os[i], gcInputU.length);
			NetworkUtil.writeInt(os[i], gcInputU[0].length);
			for (int j = 0; j < gcInputU.length; j++)
				for (int k = 0; k < gcInputU[j].length; k++)
					NetworkUtil.send(os[i], gcInputU[j][k], env);
	
			for (int j = 0; j < gcInputV.length; j++)
				for (int k = 0; k < gcInputV[j].length; k++)
					NetworkUtil.send(os[i], gcInputV[j][k], env);
			NetworkUtil.send(os[i], gcInputIsVertex, env);
			os[i].flush();
		}
	}

	@Override
	public Object readInputFromMaster(int inputLength, int inputSize,
			InputStream masterIs,
			CompEnv<T> env) throws IOException {
		T[][] gcInputU = env.newTArray(inputLength, inputSize);
		T[][] gcInputV = env.newTArray(inputLength, inputSize);
		T[] gcInputIsVertex = env.newTArray(inputLength);
		for (int j = 0; j < inputLength; j++)
			for (int k = 0; k < inputSize; k++)
				gcInputU[j][k] = NetworkUtil.read(masterIs, env);
		for (int j = 0; j < inputLength; j++)
			for (int k = 0; k < inputSize; k++)
				gcInputV[j][k] = NetworkUtil.read(masterIs, env);
		gcInputIsVertex = NetworkUtil.read(masterIs, gcInputIsVertex.length, env);
		Object[] ret = new Object[3];
		ret[0] = gcInputU;
		ret[1] = gcInputV;
		ret[2] = gcInputIsVertex;
	    return ret;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> void compute(int machineId, Machine machine, final CompEnv<T> env)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, IOException,
			BadCommandException, BadLabelException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		T[][] u = (T[][]) ((Object[]) machine.input)[0];
		T[][] v = (T[][]) ((Object[]) machine.input)[1];
		T[] isVertex = (T[]) ((Object[]) machine.input)[2];

		PageRankNode<T>[] aa = (PageRankNode<T>[]) Array.newInstance(PageRankNode.class, u.length);
		for (int i = 0; i < aa.length; i++) {
			aa[i] = new PageRankNode<T>(u[i], v[i], isVertex[i], env);
		}

		// set initial pagerank
		new SetInitialPageRankGadget<T>(env, machine)
				.setInputs(aa)
				.compute();

		// 1. Compute number of neighbors for each vertex
		new GatherFromEdges<T>(env, machine, false /* isEdgeIncoming */, new PageRankNode<T>(env)) {

			@Override
			public GraphNode<T> aggFunc(GraphNode<T> aggNode, GraphNode<T> bNode) {
				PageRankNode<T> agg = (PageRankNode<T>) aggNode;
				PageRankNode<T> b = (PageRankNode<T>) bNode;

				IntegerLib<T> lib = new IntegerLib<>(env);
				PageRankNode<T> ret = new PageRankNode<T>(env);
				ret.l = lib.add(agg.l, b.l);
				return ret;
			}

			@Override
			public void writeToVertex(GraphNode<T> aggNode, GraphNode<T> bNode) {
				PageRankNode<T> agg = (PageRankNode<T>) aggNode;
				PageRankNode<T> b = (PageRankNode<T>) bNode;
				IntegerLib<T> lib = new IntegerLib<>(env);
				b.l = lib.mux(b.l, agg.l, b.isVertex);
			}
		};

		for (int i = 0; i < ITERATIONS; i++) {
			// 2. Write weighted PR to edges
			new ScatterToEdges<T>(env, machine, false /* isEdgeIncoming */) {

				@Override
				public void writeToEdge(GraphNode<T> vertexNode,
						GraphNode<T> edgeNode, T cond) {
					PageRankNode<T> vertex = (PageRankNode<T>) vertexNode;
					PageRankNode<T> edge = (PageRankNode<T>) edgeNode;
					IntegerLib<T> lib = new IntegerLib<>(env);
					edge.pr = lib.mux(vertex.pr, edge.pr, cond);
				}
			}.setInputs(aa).compute();

			// 3. Compute PR based on edges
			new GatherFromEdges<T>(env, machine, true /* isEdgeIncoming */, new PageRankNode<T>(env)) {

				@Override
				public GraphNode<T> aggFunc(GraphNode<T> aggNode, GraphNode<T> bNode) {
					PageRankNode<T> agg = (PageRankNode<T>) aggNode;
					PageRankNode<T> b = (PageRankNode<T>) bNode;

					FloatLib<T> lib = new FloatLib<T>(env, FLOAT_V, FLOAT_P);
					PageRankNode<T> ret = new PageRankNode<T>(env);
					ret.pr = lib.add(agg.pr, b.pr);
					return ret;
				}

				@Override
				public void writeToVertex(GraphNode<T> aggNode, GraphNode<T> bNode) {
					PageRankNode<T> agg = (PageRankNode<T>) aggNode;
					PageRankNode<T> b = (PageRankNode<T>) bNode;
					IntegerLib<T> lib = new IntegerLib<>(env);
					b.pr = lib.mux(b.pr, agg.pr, b.isVertex);
				}
			}.setInputs(aa).compute();
		}

		new SortGadget<T>(env, machine)
			.setInputs(aa, PageRankNode.vertexFirstComparator(env))
			.compute();

		output(machineId, env, aa);
	}

	private <T> void output(int machineId, final CompEnv<T> env,
			PageRankNode<T>[] aa) throws IOException, BadLabelException {
		if (Mode.COUNT.equals(env.getMode())) {
			Statistics a = ((PMCompEnv) env).statistic;
			a.finalize();
			System.out.println(machineId + ": " + a.andGate + " " + a.NumEncAlice);
			System.out.println("ENVS " + PMCompEnv.ENVS_USED);
		} else {
			print(machineId, env, aa);
		}
	}

	private <T> void print(int machineId, final CompEnv<T> env, PageRankNode<T>[] pr) throws IOException, BadLabelException {
		for (int i = 0; i < pr.length; i++) {
			int a = Utils.toInt(env.outputToAlice(pr[i].u));
			int b = Utils.toInt(env.outputToAlice(pr[i].v));
			double c2 = Utils.toFloat(env.outputToAlice(pr[i].pr), FLOAT_V, PageRank.FLOAT_P);
			int d = Utils.toInt(env.outputToAlice(pr[i].l));
			boolean e = env.outputToAlice(pr[i].isVertex);
			env.os.flush();
			if (Party.Alice.equals(env.party)) {
				System.out.println(machineId + ": " + a + ", " + b + "\t" + c2 + "\t" + d + "\t" + e);
			}
	    }
	}

}