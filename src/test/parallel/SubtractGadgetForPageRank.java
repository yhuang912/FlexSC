package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;

import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;
import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;

public class SubtractGadgetForPageRank<T> extends Gadget<T> {

	private T[][] l;

	public SubtractGadgetForPageRank(CompEnv<T> env,
			Machine machine) {
		super(env, machine);
	}

	public SubtractGadgetForPageRank<T> setInputs(T[][] l) {
		this.l = l;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		IntegerLib<T> lib = new IntegerLib<>(env);
		if (machine.numberOfOutgoingConnections > 0) {
			NetworkUtil.send(machine.peerOsUp[0], l[0], env);
			((BufferedOutputStream) machine.peerOsUp[0]).flush();
		}
		T[] first = env.inputOfAlice(Utils.fromInt(0, 32 /* PageRank.INT_LEN */));
		if (machine.numberOfIncomingConnections > 0) {
			first = NetworkUtil.read(machine.peerIsDown[0], l[0].length, env);
		}
		for (int i = 0; i < l.length - 1; i++) {
			l[i] = lib.sub(l[i], l[i + 1]);
		}
		if (machine.numberOfIncomingConnections > 0) {
			l[l.length - 1] = lib.sub(l[l.length - 1], first);
		}
		return null;
	}
}
