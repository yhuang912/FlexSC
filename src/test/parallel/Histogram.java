package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

import circuits.SimpleComparator;
import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;
import test.Utils;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;
import gc.GCSignal;

public class Histogram<T> implements ParallelGadget<T> {

	private boolean[][] getInput(int inputLength) {
		int[] aa = new int[inputLength];
		boolean[][] a = new boolean[aa.length][];
		int limit = 20;
		Random rn = new Random();
		int[] freq = new int[limit + 1];
		for (int i = 0; i < limit + 1; i++)
			freq[i] = 0;
		for (int i = 0; i < a.length; ++i) {
			aa[i] = rn.nextInt(limit);
			freq[aa[i]]++;
		}
		for(int i = 0; i < aa.length; ++i)
			a[i] = Utils.fromInt(aa[i], 32);
		System.out.println("Frequencies");
		for (int i = 0; i < limit + 1; i++)
			System.out.println(i + ": " + freq[i]);
		return a;
	}

	private Object[] performOTAndReturnMachineInputs(int inputLength,
			int machines, boolean isGen, CompEnv<GCSignal> env)
			throws IOException {
		GCSignal[][] Ta = env.newTArray(inputLength /* number of entries in the input */, 0);
		if (isGen) {
			for(int i = 0; i < Ta.length; ++i)
				Ta[i] = env.inputOfBob(new boolean[32]);
		} else {
			boolean[][] a = getInput(inputLength);
			for(int i = 0; i < Ta.length; ++i)
				Ta[i] = env.inputOfBob(a[i]);
		}
		Object[] input = new Object[machines];
	
		for(int i = 0; i < machines; ++i)
			input[i] = Arrays.copyOfRange(Ta, i * Ta.length / machines, (i + 1) * Ta.length / machines);
		return input;
	}

	public void sendInputToMachines(int inputLength,
			int machines,
			boolean isGen, 
			CompEnv<GCSignal> env,
			OutputStream[] os) throws IOException {
		Object[] input = performOTAndReturnMachineInputs(inputLength, machines, isGen, env);
		for (int i = 0; i < machines; i++) {
			GCSignal[][] gcInput = (GCSignal[][]) input[i];
			NetworkUtil.writeInt(os[i], gcInput.length);
			NetworkUtil.writeInt(os[i], gcInput[0].length);
			for (int j = 0; j < gcInput.length; j++)
				for (int k = 0; k < gcInput[j].length; k++)
					gcInput[j][k].send(os[i]);
			os[i].flush();
		}
	}

	public Object readInputFromMaster(int inputLength, int inputSize, InputStream masterIs) {
		GCSignal[][] gcInput = new GCSignal[inputLength][inputSize];
		 for (int j = 0; j < inputLength; j++)
				for (int k = 0; k < inputSize; k++)
					gcInput[j][k] = GCSignal.receive(masterIs);
		 return gcInput;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void compute(int machineId, Machine machine, CompEnv env)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, IOException,
			BadCommandException, BadLabelException {
		T[][] input = (T[][]) machine.input;
		T[][] freq = (T[][]) env.newTArray(input.length, input[0].length);
		new HistogramMapper<T>(env, machine)
				.setInputs(freq)
				.compute();

		new SortGadget<T>(env, machine)
				.setInputs(input, freq, new SimpleComparator<T>(env))
				.compute();

		new PrefixSumGadget<T>(env, machine)
				.setInputs(freq)
				.compute();

		T[][] marker = (T[][]) env.newTArray(input.length, input[0].length);
		new MarkerWithLastValueGadget<T>(env, machine)
				.setInputs(input, marker)
				.compute();

		new SubtractGadgetForHistogram<T>(env, machine)
				.setInputs(marker, input, freq)
				.compute();
	}

}
