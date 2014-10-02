package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import network.BadCommandException;
import network.Machine;
import network.Master;
import flexsc.CompEnv;
import gc.BadLabelException;
import gc.GCSignal;

public interface ParallelGadget {

	public Object[] performOTAndReturnMachineInputs(int inputLength,
			int machines, Master master, CompEnv<GCSignal> env)
			throws IOException;

	public void sendInputToMachines(Object[] input, int i, OutputStream[] os) throws IOException;

	public Object readInputFromMaster(int inputLength, int inputSize, InputStream masterIs);

	public void compute(int machineId, Machine machine, CompEnv env)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, IOException,
			BadCommandException, BadLabelException;
}
