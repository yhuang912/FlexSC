package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Random;

import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;
import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;
import gc.BadLabelException;

public class Histogram<T> implements ParallelGadget<T> {

	private Object[] getInput(int inputLength) {
		int[] u = new int[inputLength];
		int[] v = new int[inputLength];
		boolean[] isVertex = new boolean[inputLength];
		boolean[][] a = new boolean[u.length][];
		boolean[][] b = new boolean[v.length][];
		boolean[] c = new boolean[isVertex.length];
		int limit = 20;
		for (int i = 0; i < limit; i++) {
			u[i] = i + 1;
			v[i] = i + 1;
			isVertex[i] = true;
		}
		Random rn = new Random();
		int[] freq = new int[limit + 1];
		for (int i = 0; i < limit + 1; i++)
			freq[i] = 0;
		for (int i = limit; i < a.length; ++i) {
//			int temp = rn.nextInt(limit) + 1;
//			int temp2 = rn.nextInt(limit) + 1;
//			if (temp == temp2) {
//				i--;
//				continue;
//			}
			u[i] = rn.nextInt(limit) + 1;
			v[i] = u[i];
			isVertex[i] = false;
			freq[v[i]]++;
		}
		for(int i = 0; i < u.length; ++i) {
			a[i] = Utils.fromInt(u[i], GraphNode.VERTEX_LEN);
			b[i] = Utils.fromInt(v[i], GraphNode.VERTEX_LEN);
			c[i] = isVertex[i];
		}
//		System.out.println("Frequencies");
//		for (int i = 0; i < limit + 1; i++)
//			System.out.println(i + ": " + freq[i]);
		Object[] ret = new Object[3];
		ret[0] = a;
		ret[1] = b;
		ret[2] = c;
		return ret;
	}

	private Object[] performOTAndReturnMachineInputs(int inputLength,
			int machines, boolean isGen, CompEnv<T> env)
			throws IOException {
		T[][] tu = env.newTArray(inputLength /* number of entries in the input */, 0);
		T[][] tv = env.newTArray(inputLength /* number of entries in the input */, 0);
		T[] tIsV = env.newTArray(inputLength /* number of entries in the input */);
		if (isGen) {
			for(int i = 0; i < tu.length; ++i)
				tu[i] = env.inputOfBob(new boolean[GraphNode.VERTEX_LEN]);
			for(int i = 0; i < tv.length; ++i)
				tv[i] = env.inputOfBob(new boolean[GraphNode.VERTEX_LEN]);
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
			OutputStream[] os) throws IOException {
		Object[] input = performOTAndReturnMachineInputs(inputLength, machines, isGen, env);
		Object[] inputU = (Object[]) input[0];
		Object[] inputV = (Object[]) input[1];
		Object[] inputIsVertex = (Object[]) input[2];
		for (int i = 0; i < machines; i++) {
			T[][] gcInputU = (T[][]) inputU[i];
			T[][] gcInputV = (T[][]) inputV[i];
			T[] gcInputIsVertex = (T[]) inputIsVertex[i];
			NetworkUtil.writeInt(os[i], gcInputU.length);
//			NetworkUtil.writeInt(os[i], gcInputU[0].length);
			NetworkUtil.send(os[i], gcInputU, env);
			NetworkUtil.send(os[i], gcInputV, env);
			NetworkUtil.send(os[i], gcInputIsVertex, env);
			os[i].flush();
		}
	}

	@Override
	public Object readInputFromMaster(int inputLength, InputStream masterIs, CompEnv<T> env) throws IOException {
		T[][] gcInputU = NetworkUtil.read(masterIs, inputLength, GraphNode.VERTEX_LEN, env);
		T[][] gcInputV = NetworkUtil.read(masterIs, inputLength, GraphNode.VERTEX_LEN, env);
		T[] gcInputIsVertex = NetworkUtil.read(masterIs, inputLength, env);
		Object[] ret = new Object[3];
		ret[0] = gcInputU;
		ret[1] = gcInputV;
		ret[2] = gcInputIsVertex;
	    return ret;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> void compute(int machineId, Machine machine, CompEnv<T> env)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, IOException,
			BadCommandException, BadLabelException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		T[][] u = (T[][]) ((Object[]) machine.input)[0];
		T[][] v = (T[][]) ((Object[]) machine.input)[1];
		T[] isVertex = (T[]) ((Object[]) machine.input)[2];
		final IntegerLib<T> lib = new IntegerLib<>(env);
//		T[][] freq = (T[][]) env.newTArray(input.length, input[0].length);
		HistogramNode<T>[] aa = (HistogramNode<T>[]) Array.newInstance(HistogramNode.class, u.length);
		for (int i = 0; i < aa.length; i++) {
			aa[i] = new HistogramNode<T>(u[i], v[i], isVertex[i], env);
		}

		long startTime = System.nanoTime();
		long communicate = (long) new GatherFromEdges<T>(env, machine, true /* isEdgeIncoming */, new HistogramNode<>(env)) {

			@Override
			public GraphNode<T> aggFunc(GraphNode<T> agg, GraphNode<T> b) throws IOException {
//				T[] one = lib.publicValue(1);
				T[] one = env.inputOfAlice(Utils.fromInt(1, HistogramNode.LEN));
				HistogramNode<T> ret = new HistogramNode<T>(env);
				ret.count = lib.add(((HistogramNode<T>) agg).count, one);
				return ret;
			}

			@Override
			public void writeToVertex(GraphNode<T> aggNode, GraphNode<T> bNode) {
				HistogramNode<T> agg = (HistogramNode<T>) aggNode;
				HistogramNode<T> b = (HistogramNode<T>) bNode;
				b.count = lib.mux(b.count, agg.count, b.isVertex);
			}
		}.setInputs(aa).compute();

		long gatherTime = System.nanoTime();
		communicate += (long) new SortGadget<T>(env, machine)
			.setInputs(aa, GraphNode.vertexFirstComparator(env))
			.compute();
		long endTime = System.nanoTime();

		if (Mode.COUNT.equals(env.getMode())) {
			Statistics a = ((PMCompEnv) env).statistic;
			a.finalize();
			Thread.sleep(1000 * machineId);
			System.out.println(machineId + "," + machine.totalMachines + "," + machine.inputLength + "," + a.andGate + "," + a.NumEncAlice);
		} else if (Mode.REAL.equals(env.getMode())) {
//			for (int i = 0; i < 4; i++) {
//				int int2 = Utils.toInt(env.outputToAlice(aa[i].v));
//				int int3 = Utils.toInt(env.outputToAlice(aa[i].count));
//				if (Party.Alice.equals(env.party)) {
//					System.out.println(machine.machineId + ": " + int2 + ", " + int3);
//				}
//			}
			System.out.println(machineId + "," + machine.totalMachines + "," + machine.inputLength + "," + (gatherTime - startTime)/1000000000.0 + "," + "Gather" + "," + env.getParty().name());
			System.out.println(machineId + "," + machine.totalMachines + "," + machine.inputLength + "," + (endTime - gatherTime)/1000000000.0 + "," + "Final sort" + "," + env.getParty().name());
			System.out.println(machineId + "," + machine.totalMachines + "," + machine.inputLength + "," + (endTime - startTime)/1000000000.0 + "," + "Total time" + "," + env.getParty().name());
			System.out.println(machineId + "," + machine.totalMachines + "," + machine.inputLength + "," + communicate/1000000000.0 + "," + "Communication time" + "," + env.getParty().name());
		}
	}
}
