package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

import test.Utils;
import network.BadCommandException;
import network.Machine;
import network.Master;
import flexsc.CompEnv;
import gc.BadLabelException;
import gc.GCSignal;

public class PageRank implements ParallelGadget {

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
		/*System.out.println("Frequencies");
		for (int i = 0; i < limit + 1; i++)
			System.out.println(i + ": " + freq[i]);*/
		return a;
	}

	@Override
	public Object[] performOTAndReturnMachineInputs(int inputLength,
			int machines, Master master, CompEnv<GCSignal> env)
			throws IOException {
		GCSignal[][] Ta = env.newTArray(inputLength /* number of entries in the input */, 0);
		GCSignal[][] Tb = env.newTArray(inputLength /* number of entries in the input */, 0);
		if (master.isGen) {
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

	@Override
	public void sendInputToMachines(Object[] input, int i, OutputStream[] os)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object readInputFromMaster(int inputLength, int inputSize,
			InputStream masterIs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void compute(int machineId, Machine machine, CompEnv env)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, IOException,
			BadCommandException, BadLabelException {
		// TODO Auto-generated method stub
		
	}

}
