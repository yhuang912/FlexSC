package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import network.BadCommandException;
import test.Utils;
import circuits.IntegerLib;
import circuits.SimpleComparator;
import flexsc.CompEnv;
import flexsc.Gadget;
import flexsc.Party;
import gc.BadLabelException;

public class SubtractGadgetForHistogram<T> extends Gadget<T> {

	private int numberOfIncomingConnections;
	private int numberOfOutgoingConnections;

	@SuppressWarnings("unchecked")
	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {

		IntegerLib<T> lib = new IntegerLib<>(env);
		T[][] flag = (T[][]) inputs[0];
		T[][] x = (T[][]) inputs[1];
		T[][] prefixSum = (T[][]) inputs[2];

		T[][] data = Utils.flatten(env, x, prefixSum);
		Class c;
		SortGadget gadge;
		try {
			c = Class.forName("test.parallel.SortGadget");
			gadge = (SortGadget) c.newInstance();
			gadge.setComparator(new SimpleComparator<>(env));
			Object[] inputs = new Object[2];
			inputs[0] = flag;
			inputs[1] = data;
			gadge.setInputs(inputs, env, machineId,
					peerIsUp,
					peerOsUp,
					peerIsDown,
					peerOsDown,
					logMachines,
					inputLength);
			Object[] sortOutput = (Object[]) gadge.secureCompute();
			
			flag = (T[][]) sortOutput[0];
			data = (T[][]) sortOutput[1];
			Utils.unflatten(data, x, prefixSum);

			// send and receive values
			if (numberOfIncomingConnections > 0) {
				send(peerOsDown[0], prefixSum[prefixSum.length - 1]);
				((BufferedOutputStream) peerOsDown[0]).flush();
			}
			T[] last = env.inputOfAlice(Utils.fromInt(-1, 32));
			if (numberOfOutgoingConnections > 0) {
				last = read(peerIsUp[0], prefixSum[0].length);
			}
			for (int i = x.length - 1; i > 0; i--) {
				prefixSum[i] = lib.sub(prefixSum[i], prefixSum[i - 1]);
			}
			if (numberOfOutgoingConnections > 0) {
				prefixSum[0] = lib.sub(prefixSum[0], last);
			}
		if (machineId == 0) {
				for (int i = 0; i < 4; i++) {
					int int1 = Utils.toInt(env.outputToAlice(flag[i]));
					int int2 = Utils.toInt(env.outputToAlice(x[i]));
					int int3 = Utils.toInt(env.outputToAlice(prefixSum[i]));
					if (Party.Alice.equals(env.party)) {
						System.out.println(machineId + ": " + int2 + ", " + int3);
					}
				}
			}
			env.os.flush();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
