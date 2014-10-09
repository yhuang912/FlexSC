package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;

import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;
import circuits.ArithmeticLib;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;

public class PrefixSumGadget<T> extends Gadget<T> {

	private T[][] x;
	private ArithmeticLib<T> lib;

	public PrefixSumGadget(CompEnv<T> env, Machine machine) {
		super(env, machine);
	}

	public PrefixSumGadget<T> setInputs(T[][] x, ArithmeticLib<T> lib) {
		this.x = x;
		this.lib = lib;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException, BadCommandException, BadLabelException {

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
		return null;
	}

	private T[] prefixSum(T[] prefixSum, ArithmeticLib<T> lib) throws IOException, BadLabelException {
		int noOfIncomingConnections = machine.numberOfIncomingConnections;
		int noOfOutgoingConnections = machine.numberOfOutgoingConnections;
		for (int k = 0; k < machine.logMachines; k++) {
			if (noOfIncomingConnections > 0) {
				NetworkUtil.send(machine.peerOsDown[k], prefixSum, env);
				((BufferedOutputStream) machine.peerOsDown[k]).flush();
				noOfIncomingConnections--;
			}
			if (noOfOutgoingConnections > 0) {
				T[] read = NetworkUtil.read(machine.peerIsUp[k], prefixSum.length, env);
				prefixSum = lib.add(prefixSum, read);
				noOfOutgoingConnections--;
			}
		}
		// System.out.println(machineId + ": Sum = " + Utils.toInt(lib.getBooleans(prefixSum)));
		return prefixSum;
	}
}
