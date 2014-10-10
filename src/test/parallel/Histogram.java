package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;
import test.Utils;
import circuits.IntegerLib;
import circuits.TestComparator;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;
import gc.BadLabelException;

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
			int machines, boolean isGen, CompEnv<T> env)
			throws IOException {
		T[][] Ta = env.newTArray(inputLength /* number of entries in the input */, 0);
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
			CompEnv<T> env,
			OutputStream[] os) throws IOException {
		Object[] input = performOTAndReturnMachineInputs(inputLength, machines, isGen, env);
		for (int i = 0; i < machines; i++) {
			T[][] gcInput = (T[][]) input[i];
			NetworkUtil.writeInt(os[i], gcInput.length);
			NetworkUtil.writeInt(os[i], gcInput[0].length);
			for (int j = 0; j < gcInput.length; j++) {
				for (int k = 0; k < gcInput[j].length; k++) {
					NetworkUtil.send(os[i], gcInput[j][k], env);
//					gcInput[j][k].send(os[i]);
				}
			}
			os[i].flush();
		}
	}

	@Override
	public Object readInputFromMaster(int inputLength, int inputSize, InputStream masterIs, CompEnv<T> env) throws IOException {
		T[][] gcInput = env.newTArray(inputLength, inputSize);//new T[inputLength][inputSize];
		 for (int j = 0; j < inputLength; j++)
				for (int k = 0; k < inputSize; k++)
					gcInput[j][k] = NetworkUtil.read(masterIs, env);// env.ZERO();//new Boolean(true);//Boolean.receive(masterIs);
		 return gcInput;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <T> void compute(int machineId, Machine machine, CompEnv<T> env)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, IOException,
			BadCommandException, BadLabelException {
		T[][] input = (T[][]) machine.input;
		T[][] freq = (T[][]) env.newTArray(input.length, input[0].length);
		new HistogramMapper<T>(env, machine)
				.setInputs(freq)
				.compute();

		new SortGadget<T>(env, machine)
				.setInputs(input, new TestComparator<T>(env), freq)
				.compute();

		new PrefixSumGadget<T>(env, machine)
				.setInputs(freq, new IntegerLib<T>(env))
				.compute();

		T[][] flag = (T[][]) env.newTArray(input.length, input[0].length);
		new MarkerWithLastValueGadget<T>(env, machine)
				.setInputs(input, flag)
				.compute();

		T[][] data = (T[][]) Utils.flatten(env, input, freq);
		data = (T[][]) new SortGadget<T>(env, machine)
				.setInputs(flag, new TestComparator<T>(env), input, freq)
				.compute();
		Utils.unflatten(data, input, freq);

		new SubtractGadgetForHistogram<T>(env, machine)
				.setInputs(freq, new IntegerLib<T>(env))
				.compute();

		if (Mode.COUNT.equals(env.getMode())) {
			Statistics a = ((PMCompEnv) env).statistic;
			a.finalize();
			System.out.println(machineId + ": " + a.andGate + " " + a.NumEncAlice);
			System.out.println("ENVS " + PMCompEnv.ENVS_USED);
		} else if (machine.machineId == 0) {
			for (int i = 0; i < 4; i++) {
				int int1 = Utils.toInt(env.outputToAlice(flag[i]));
				int int2 = Utils.toInt(env.outputToAlice(input[i]));
				int int3 = Utils.toInt(env.outputToAlice(freq[i]));
				if (Party.Alice.equals(env.party)) {
					System.out.println(machine.machineId + ": " + int2 + ", " + int3);
				}
			}
		}
	}
}
