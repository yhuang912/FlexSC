package test.parallel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import circuits.Comparator;
import circuits.IntegerLib;
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
			a[i] = Utils.fromInt(u[i], 32);
			b[i] = Utils.fromInt(v[i], 32);
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
				tu[i] = env.inputOfBob(new boolean[32]);
			for(int i = 0; i < tv.length; ++i)
				tv[i] = env.inputOfBob(new boolean[32]);
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
		Class c = Class.forName("test.parallel.SetInitialPageRankGadget");
		Gadget initialPageRank = (Gadget) c.newInstance();
		initialPageRank.setInputs((Object[]) machine.input, env, machineId,
				machine.peerIsUp,
				machine.peerOsUp,
				machine.peerIsDown,
				machine.peerOsDown,
				machine.logMachines,
				machine.inputLength);
		Object[] output = (Object[]) initialPageRank.compute();
		T[][] u = (T[][]) output[0];
		T[][] v = (T[][]) output[1];
		T[][] pr = (T[][]) output[2];
		T[][] l = (T[][]) output[3];

		T[][] data = (T[][]) Utils.flatten(env, v, pr, l);
		c = Class.forName("test.parallel.SortGadget");
		SortGadget sortGadget = (SortGadget) c.newInstance();
		Object[] inputs = new Object[2];
		inputs[0] = output[0];
		inputs[1] = data;
		sortGadget.setInputs(inputs, env, machineId,
				machine.peerIsUp,
				machine.peerOsUp,
				machine.peerIsDown,
				machine.peerOsDown,
				machine.logMachines,
				machine.inputLength);
		sortGadget.setComparator(new Comparator<T>() {

			@Override
			public T leq(T[] ui, T[] uj, T[] datai, T[] dataj) {
				T[] vi = (T[]) env.newTArray(32);
				T[] pri = (T[]) env.newTArray(32);
				T[] vj = (T[]) env.newTArray(32);
				T[] prj = (T[]) env.newTArray(32);
				Utils.unflatten(datai, vi, pri);
				Utils.unflatten(dataj, vj, prj);
				IntegerLib<T> lib = new IntegerLib<>(env);
				T v = lib.geq(vi, vj);
				T eq = lib.eq(ui, uj);
				T u = lib.leq(ui, uj);
				return lib.mux(u, v, eq);
			}
		});
		Object[] output2 = (Object[]) sortGadget.compute();

		u = (T[][]) output2[0];
		Utils.unflatten((T[][]) output2[1], v, pr, l);

		c = Class.forName("test.parallel.PrefixSumGadget");
		PrefixSumGadget prefixSumGadget = (PrefixSumGadget) c.newInstance();
		Object[] prefixSumInputs = new Object[1];
		prefixSumInputs[0] = l;
		prefixSumGadget.setInputs(prefixSumInputs, env, machineId,
				machine.peerIsUp,
				machine.peerOsUp,
				machine.peerIsDown,
				machine.peerOsDown,
				machine.logMachines,
				machine.inputLength,
				machine.numberOfIncomingConnections,
				machine.numberOfOutgoingConnections);
		Object[] prefixSumDataResult = (Object[]) prefixSumGadget.compute();

//		for (int i = 0; i < pr.length; i++) {
//		int a = Utils.toInt(env.outputToAlice(u[i]));
//		int b = Utils.toInt(env.outputToAlice(v[i]));
//		double c2 = Utils.toFloat(env.outputToAlice(pr[i]), 20, 12);
//		int d = Utils.toInt(env.outputToAlice(l[i]));
//		if (Party.Alice.equals(env.party)) {
//			System.out.println(machineId + ": " + a + ", " + b + "\t" + c2 + "\t" + d);
//		}
//    }
	}

}