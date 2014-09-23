package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import network.BadCommandException;
import network.Master;
import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.Gadget;
import flexsc.Party;
import gc.BadLabelException;

public class PrefixSumGadget<T> extends Gadget<T> {

	private int numberOfIncomingConnections;
	private int numberOfOutgoingConnections;

	@Override
	public Object secureCompute() throws InterruptedException, IOException, BadCommandException, BadLabelException {

		T[][] x = (T[][]) inputs[0];

		IntegerLib<T> lib =  new IntegerLib<T>(env);

		T[] result = x[0];
		for(int i = 1; i < x.length; ++i) {
			x[i] = lib.add(result, x[i]);
			result = x[i];
		}
		// System.out.println(machineId + ": Sum = " + Utils.toInt(lib.getBooleans(result)));
		T[] localSum = result;

		T[] prefixSum = prefixSum(result, lib);
		T[] otherSum = lib.sub(prefixSum, localSum);
		for (int i = 0; i < x.length; i++) {
			x[i] = lib.add(otherSum, x[i]);
		}
		T[][][] output = env.newTArray(1, x.length, x[0].length);
		output[0] = x;
		/*for (int i = 0; i < x.length; i++) {
			int int1 = Utils.toInt(lib.getBooleans(x[i]));
			if (machineId == 3 && Party.Alice.equals(env.party)) {
				System.out.println(machineId + ", " + int1);
			}
		}*/	
		return output;
	}

	private T[] prefixSum(T[] prefixSum, IntegerLib<T> lib) throws IOException, BadLabelException {
		int noOfIncomingConnections = this.numberOfIncomingConnections;
		int noOfOutgoingConnections = this.numberOfOutgoingConnections;
		for (int k = 0; k < logMachines; k++) {
			/* if (noOfOutgoingConnections > 0) {
				send(peerOsUp[k], prefixSum);
				((BufferedOutputStream) peerOsUp[k]).flush();
				noOfOutgoingConnections--;
			}
			if (noOfIncomingConnections > 0) {
				T[] read = read(peerIsDown[k], prefixSum.length);
				prefixSum = lib.add(prefixSum, read);
				noOfIncomingConnections--;
			} */
			if (noOfIncomingConnections > 0) {
				send(peerOsDown[k], prefixSum);
				((BufferedOutputStream) peerOsDown[k]).flush();
				noOfIncomingConnections--;
			}
			if (noOfOutgoingConnections > 0) {
				T[] read = read(peerIsUp[k], prefixSum.length);
				prefixSum = lib.add(prefixSum, read);
				noOfOutgoingConnections--;
			}
		}
		// System.out.println(machineId + ": Sum = " + Utils.toInt(lib.getBooleans(prefixSum)));
		return prefixSum;
	}

	public void setInputs(Object[] inputs, 
			CompEnv<T> env,
			int machineId, 
			InputStream[] peerIsUp,
			OutputStream[] peerOsUp,
			InputStream[] peerIsDown,
			OutputStream[] peerOsDown,
			int logMachines,
			int inputLength,
			int numberOfIncomingConnections,
			int numberOfOutgoingConnections) {
		setInputs(inputs, env, machineId, peerIsUp, peerOsUp, peerIsDown, peerOsDown, logMachines, inputLength);
		this.numberOfIncomingConnections = numberOfIncomingConnections;
		this.numberOfOutgoingConnections = numberOfOutgoingConnections;
	}
}
