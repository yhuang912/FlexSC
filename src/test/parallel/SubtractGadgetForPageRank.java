package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import test.Utils;
import circuits.IntegerLib;
import network.BadCommandException;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;

public class SubtractGadgetForPageRank<T> extends Gadget<T> {

	private int numberOfIncomingConnections;
	private int numberOfOutgoingConnections;

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		IntegerLib<T> lib = new IntegerLib<>(env);
		T[][] l = (T[][]) inputs[0];
		if (numberOfOutgoingConnections > 0) {
			send(peerOsUp[0], l[0]);
			((BufferedOutputStream) peerOsUp[0]).flush();
		}
		T[] first = env.inputOfAlice(Utils.fromInt(-1, 32));
		if (numberOfIncomingConnections > 0) {
			first = read(peerIsDown[0], l[0].length);
		}
		for (int i = 0; i < l.length - 1; i++) {
			l[i] = lib.sub(l[i], l[i + 1]);
		}
		if (numberOfIncomingConnections > 0) {
			l[l.length - 1] = lib.sub(l[l.length - 1], first);
		}
		return l;
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
