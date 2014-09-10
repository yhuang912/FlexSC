package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import network.BadCommandException;
import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.Gadget;
import flexsc.Party;
import gc.BadLabelException;

public class SubtractGadget<T> extends Gadget<T> {

	private int numberOfIncomingConnections;
	private int numberOfOutgoingConnections;

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {

		IntegerLib<T> lib = new IntegerLib<>(env);
		T[][] flag = (T[][]) inputs[0];
		T[][] x = (T[][]) inputs[1];
		T[][] prefixSum = (T[][]) inputs[2];
		T[][] data = env.newTArray(x.length, x[0].length + prefixSum[0].length);
		for (int i = 0; i < data.length; i++) {
			System.arraycopy(x[i], 0, data[i], 0, x[i].length);
			System.arraycopy(prefixSum[i], 0, data[i], x[i].length, prefixSum[i].length);
		}
		/* for (int i = 0; i < x.length; i++) {
			int int1 = Utils.toInt(lib.getBooleans(flag[i]));
			int int2 = Utils.toInt(lib.getBooleans(x[i]));
			int int3 = Utils.toInt(lib.getBooleans(prefixSum[i]));
			if (Party.Alice.equals(env.party)) {
				System.out.println(machineId + ": " + int1 + ", " + int2 + ", " + int3);
			}
		}
		System.out.println("**");
		Thread.sleep(10000);
		System.out.println("-------------"); */
		Class c;
		Gadget gadge;
		try {
			c = Class.forName("test.parallel.AnotherSortGadget");
			gadge = (Gadget) c.newInstance();
			Object[] inputs = new Object[2];
			inputs[0] = flag;
			inputs[1] = data;
			gadge.setInputs(inputs, env, machineId,
					peerIsUp,
					peerOsUp,
					peerIsDown,
					peerOsDown);
			Object[] sortOutput = (Object[]) gadge.secureCompute();
			
			flag = (T[][]) sortOutput[0];
			data = (T[][]) sortOutput[1];
			for (int i = 0; i < data.length; i++) {
				System.arraycopy(data[i], 0, x[i], 0, x[i].length);
				System.arraycopy(data[i], x[i].length, prefixSum[i], 0, prefixSum[i].length);
			}
			

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
			for (int i = 0; i < x.length; i++) {
				int int1 = Utils.toInt(lib.getBooleans(flag[i]));
				int int2 = Utils.toInt(lib.getBooleans(x[i]));
				int int3 = Utils.toInt(lib.getBooleans(prefixSum[i]));
				if (Party.Alice.equals(env.party)) {
					System.out.println(machineId + ": " + int2 + ", " + int3);
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
			int numberOfIncomingConnections,
			int numberOfOutgoingConnections) {
		setInputs(inputs, env, machineId, peerIsUp, peerOsUp, peerIsDown, peerOsDown);
		this.numberOfIncomingConnections = numberOfIncomingConnections;
		this.numberOfOutgoingConnections = numberOfOutgoingConnections;
	}
}
