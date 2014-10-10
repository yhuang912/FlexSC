package test.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import network.BadCommandException;
import network.Machine;
import ot.IncorrectOtUsageException;
import flexsc.CompEnv;
import gc.BadLabelException;

public interface ParallelGadget<T> {

	/*public Object[] performOTAndReturnMachineInputs(int inputLength,
			int machines, boolean isGen, CompEnv<GCSignal> env)
			throws IOException;*/

	public void sendInputToMachines(int inputLength,
			int machines,
			boolean isGen, 
			CompEnv<T> env,
			OutputStream[] os) throws IOException, IncorrectOtUsageException;

	public Object readInputFromMaster(int inputLength, int inputSize, InputStream masterIs, CompEnv<T> env) throws IOException ;

	public <T> void compute(int machineId, Machine machine, CompEnv<T> env)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException, IOException,
			BadCommandException, BadLabelException;
}
