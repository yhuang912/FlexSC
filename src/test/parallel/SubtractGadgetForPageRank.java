package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import test.Utils;
import circuits.IntegerLib;
import network.BadCommandException;
import network.Machine;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;

public class SubtractGadgetForPageRank<T> extends Gadget<T> {

	public SubtractGadgetForPageRank(Object[] inputs, CompEnv<T> env,
			Machine machine) {
		super(inputs, env, machine);
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		IntegerLib<T> lib = new IntegerLib<>(env);
		T[][] l = (T[][]) inputs[0];
		if (machine.numberOfOutgoingConnections > 0) {
			send(machine.peerOsUp[0], l[0]);
			((BufferedOutputStream) machine.peerOsUp[0]).flush();
		}
		T[] first = env.inputOfAlice(Utils.fromInt(-1, 32));
		if (machine.numberOfIncomingConnections > 0) {
			first = read(machine.peerIsDown[0], l[0].length);
		}
		for (int i = 0; i < l.length - 1; i++) {
			l[i] = lib.sub(l[i], l[i + 1]);
		}
		if (machine.numberOfIncomingConnections > 0) {
			l[l.length - 1] = lib.sub(l[l.length - 1], first);
		}
		return l;
	}
}
