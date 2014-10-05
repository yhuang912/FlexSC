package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import network.BadCommandException;
import network.Machine;
import flexsc.CompEnv;
import gc.BadLabelException;
import gc.GCSignal;

public interface ParallelGadget<T> {

	/*public Object[] performOTAndReturnMachineInputs(int inputLength,
			int machines, boolean isGen, CompEnv<GCSignal> env)
			throws IOException;*/

	public void sendInputToMachines(int inputLength,
			int machines,
			boolean isGen, 
			CompEnv<GCSignal> env,
			OutputStream[] os) throws IOException;

	public Object readInputFromMaster(int inputLength, int inputSize, InputStream masterIs);

	public <T> void compute(int machineId, Machine machine, CompEnv env)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, IOException,
			BadCommandException, BadLabelException;
}
