package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;

import network.BadCommandException;
import network.Machine;
import test.Utils;
import circuits.ArithmeticLib;
import circuits.IntegerLib;
import circuits.SimpleComparator;
import flexsc.CompEnv;
import flexsc.Gadget;
import flexsc.Party;
import gc.BadLabelException;

public class SubtractGadgetForHistogram<T> extends Gadget<T> {

	private T[][] freq;
	private ArithmeticLib<T> lib;

	public SubtractGadgetForHistogram(CompEnv<T> env,
			Machine machine) {
		super(env, machine);
	}

	public SubtractGadgetForHistogram<T> setInputs(T[][] freq, ArithmeticLib<T> lib) {
		this.freq = freq;
		this.lib = lib;
		return this;
	}
	@SuppressWarnings("unchecked")
	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		// send and receive values
		if (machine.numberOfIncomingConnections > 0) {
			send(machine.peerOsDown[0], freq[freq.length - 1]);
			((BufferedOutputStream) machine.peerOsDown[0]).flush();
		}
		T[] last = env.inputOfAlice(Utils.fromInt(-1, 32));
		if (machine.numberOfOutgoingConnections > 0) {
			last = read(machine.peerIsUp[0], freq[0].length);
		}
		for (int i = freq.length - 1; i > 0; i--) {
			freq[i] = lib.sub(freq[i], freq[i - 1]);
		}
		if (machine.numberOfOutgoingConnections > 0) {
			freq[0] = lib.sub(freq[0], last);
		}
		return null;
	}
}
