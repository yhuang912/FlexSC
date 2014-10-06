package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;

import network.BadCommandException;
import network.Machine;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;

public class PrefixSumGadget<T> extends Gadget<T> {

	public PrefixSumGadget(Object[] inputs, CompEnv<T> env, Machine machine) {
		super(inputs, env, machine);
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException, BadCommandException, BadLabelException {

		T[][] x = (T[][]) inputs[0];

		IntegerLib<T> lib =  new IntegerLib<T>(env);

		T[] result = x[0];
		for(int i = 1; i < x.length; ++i) {
			x[i] = lib.add(result, x[i]);
			result = x[i];
		}
		T[] localSum = result;

		T[] prefixSum = prefixSum(result, lib);
		T[] otherSum = lib.sub(prefixSum, localSum);
		for (int i = 0; i < x.length; i++) {
			x[i] = lib.add(otherSum, x[i]);
		}
		T[][][] output = env.newTArray(1, x.length, x[0].length);
		output[0] = x;
		return output;
	}

	private T[] prefixSum(T[] prefixSum, IntegerLib<T> lib) throws IOException, BadLabelException {
		int noOfIncomingConnections = machine.numberOfIncomingConnections;
		int noOfOutgoingConnections = machine.numberOfOutgoingConnections;
		for (int k = 0; k < machine.logMachines; k++) {
			if (noOfIncomingConnections > 0) {
				send(machine.peerOsDown[k], prefixSum);
				((BufferedOutputStream) machine.peerOsDown[k]).flush();
				noOfIncomingConnections--;
			}
			if (noOfOutgoingConnections > 0) {
				T[] read = read(machine.peerIsUp[k], prefixSum.length);
				prefixSum = lib.add(prefixSum, read);
				noOfOutgoingConnections--;
			}
		}
		// System.out.println(machineId + ": Sum = " + Utils.toInt(lib.getBooleans(prefixSum)));
		return prefixSum;
	}
}
