package test.parallel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import circuits.Comparator;
import circuits.IntegerLib;
import circuits.SimpleComparator;
import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;
import test.Utils;
import flexsc.CompEnv;
import flexsc.Gadget;
import flexsc.Party;
import gc.BadLabelException;
import gc.GCSignal;

public class PageRank<T> implements ParallelGadget<T> {
	static int FLOAT_V = 20;
	static int FLOAT_P = 11;
	static int INT_LEN = 32;

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
			int machines, boolean isGen, CompEnv<GCSignal> env)
			throws IOException {
		GCSignal[][] tu = env.newTArray(inputLength /* number of entries in the input */, 0);
		GCSignal[][] tv = env.newTArray(inputLength /* number of entries in the input */, 0);
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
			CompEnv<GCSignal> env,
			OutputStream[] os) throws IOException {
		Object[] input = performOTAndReturnMachineInputs(inputLength, machines, isGen, env);
		Object[] inputU = (Object[]) input[0];
		Object[] inputV = (Object[]) input[1];
		for (int i = 0; i < machines; i++) {
			GCSignal[][] gcInputU = (GCSignal[][]) inputU[i];
			GCSignal[][] gcInputV = (GCSignal[][]) inputV[i];
			NetworkUtil.writeInt(os[i], gcInputU.length);
			NetworkUtil.writeInt(os[i], gcInputU[0].length);
			for (int j = 0; j < gcInputU.length; j++)
				for (int k = 0; k < gcInputU[j].length; k++)
					gcInputU[j][k].send(os[i]);
	
			for (int j = 0; j < gcInputV.length; j++)
				for (int k = 0; k < gcInputV[j].length; k++)
					gcInputV[j][k].send(os[i]);
			os[i].flush();
		}
	}

	@Override
	public Object readInputFromMaster(int inputLength, int inputSize,
			InputStream masterIs) {
		GCSignal[][] gcInputU = new GCSignal[inputLength][inputSize];
		GCSignal[][] gcInputV = new GCSignal[inputLength][inputSize];
		for (int j = 0; j < inputLength; j++)
			for (int k = 0; k < inputSize; k++)
				gcInputU[j][k] = GCSignal.receive(masterIs);
		for (int j = 0; j < inputLength; j++)
			for (int k = 0; k < inputSize; k++)
				gcInputV[j][k] = GCSignal.receive(masterIs);
		Object[] ret = new Object[2];
		ret[0] = gcInputU;
		ret[1] = gcInputV;
	    return ret;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> void compute(int machineId, Machine machine, final CompEnv env)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, IOException,
			BadCommandException, BadLabelException {

		Comparator<T> firstSortComparator = new Comparator<T>() {

			@Override
			public T leq(T[] ui, T[] uj, T[] datai, T[] dataj) {
				T[] vi = (T[]) env.newTArray(INT_LEN);
				T[] pri = (T[]) env.newTArray(INT_LEN);
				T[] vj = (T[]) env.newTArray(INT_LEN);
				T[] prj = (T[]) env.newTArray(INT_LEN);
				Utils.unflatten(datai, vi, pri);
				Utils.unflatten(dataj, vj, prj);
				IntegerLib<T> lib = new IntegerLib<>(env);
//				T v = lib.geq(vi, vj);
//				T eq = lib.eq(ui, uj);
//				T u = lib.leq(ui, uj);
//				return lib.mux(u, v, eq);

				T[] ai = (T[]) env.newTArray(2 * INT_LEN);
				T[] aj = (T[]) env.newTArray(2 * INT_LEN);
				ai = (T[]) Utils.flatten(env, lib.not(vi), ui);
				aj = (T[]) Utils.flatten(env, lib.not(vj), uj);
				return lib.leq(ai, aj);
			}
		};
		Comparator<T> secondSortComparator = new Comparator<T>() {

			@Override
			public T leq(T[] ui, T[] uj, T[] datai, T[] dataj) {
				IntegerLib<T> lib = new IntegerLib<>(env);
				T[] pri = (T[]) env.newTArray(INT_LEN);
				T[] prj = (T[]) env.newTArray(INT_LEN);
				T[] li = (T[]) env.newTArray(INT_LEN);
				T[] lj = (T[]) env.newTArray(INT_LEN);

				T[] vi = (T[]) env.newTArray(INT_LEN);
				T[] vj = (T[]) env.newTArray(INT_LEN);
				Utils.unflatten(datai, vi, pri, li);
				Utils.unflatten(dataj, vj, prj, lj);
//				T v = lib.leq(vi, vj);
//				T eq = lib.eq(ui, uj);
//				T u = lib.leq(ui, uj);
//				return lib.mux(u, v, eq);

				T[] ai = (T[]) env.newTArray(2 * INT_LEN);
				T[] aj = (T[]) env.newTArray(2 * INT_LEN);
				ai = (T[]) Utils.flatten(env, vi, ui);
				aj = (T[]) Utils.flatten(env, vj, uj);
				return lib.leq(ai, aj);
			}
		};

		T[][] u = (T[][]) ((Object[]) machine.input)[0];
		T[][] v = (T[][]) ((Object[]) machine.input)[1];
		T[][] pr = (T[][]) env.newTArray(u.length, u[0].length);
		T[][] l = (T[][]) env.newTArray(u.length, u[0].length);
		
		// set initial pagerank
		new SetInitialPageRankGadget<T>(env, machine)
				.setInputs(u, v, pr, l)
				.compute();

		// Sort to get edges followed by the vertex
		T[][] data = (T[][]) Utils.flatten(env, v, pr, l);
		SortGadget sortGadget = new SortGadget<T>(env, machine)
				.setInputs(u, data, firstSortComparator);
		Object[] output2 = (Object[]) sortGadget.compute();
		u = (T[][]) output2[0];
		Utils.unflatten((T[][]) output2[1], v, pr, l);

		// Compute prefixSum for L
		new PrefixSumGadget<T>(env, machine)
				.setInputs(l)
				.compute();

		// Weird sort
		// key is v
		data = (T[][]) Utils.flatten(env, u, pr, l);
		sortGadget = new SortGadget<T>(env, machine)
				.setInputs(v, data, firstSortComparator);
		Object[] output3 = (Object[]) sortGadget.compute();
		v = (T[][]) output3[0];
		Utils.unflatten((T[][]) output3[1], u, pr, l);

		// Subtract to obtain l
		new SubtractGadgetForPageRank<>(env, machine)
				.setInputs(l)
				.compute();

		// Sort so that all vertices are followed by edges
		data = (T[][]) Utils.flatten(env, v, pr, l);
		sortGadget = new SortGadget<T>(env, machine)
				.setInputs(u, data, secondSortComparator);
		Object[] output4 = (Object[]) sortGadget.compute();
		u = (T[][]) output4[0];
		Utils.unflatten((T[][]) output4[1], v, pr, l);

		// Write PR to edge
		new WritePrPartToEdge<>(env, machine)
				.setInputs(u, v, pr, l)
				.compute();

		new SwapNonVertexEdges<T>(env, machine)
				.setInputs(u, v)
				.compute();

		print(machineId, env, u, v, pr, l);
	}

	private <T> void print(int machineId, final CompEnv env, T[][] u, T[][] v,
			T[][] pr, T[][] l) throws IOException, BadLabelException {
		for (int i = 0; i < pr.length; i++) {
			int a = Utils.toInt(env.outputToAlice(u[i]));
			int b = Utils.toInt(env.outputToAlice(v[i]));
			double c2 = Utils.toFloat(env.outputToAlice(pr[i]), FLOAT_P, PageRank.FLOAT_V);
			int d = Utils.toInt(env.outputToAlice(l[i]));
			if (Party.Alice.equals(env.party)) {
				System.out.println(machineId + ": " + a + ", " + b + "\t" + c2 + "\t" + d);
			}
	    }
	}

}