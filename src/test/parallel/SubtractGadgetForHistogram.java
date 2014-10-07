package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;

import network.BadCommandException;
import network.Machine;
import test.Utils;
import circuits.IntegerLib;
import circuits.SimpleComparator;
import flexsc.CompEnv;
import flexsc.Gadget;
import flexsc.Party;
import gc.BadLabelException;

public class SubtractGadgetForHistogram<T> extends Gadget<T> {

	private T[][] flag;
	private T[][] input;
	private T[][] freq;

	public SubtractGadgetForHistogram(CompEnv<T> env,
			Machine machine) {
		super(env, machine);
	}

	public SubtractGadgetForHistogram<T> setInputs(T[][] flag, T[][] input, T[][] freq) {
		this.flag = flag;
		this.input = input;
		this.freq = freq;
		return this;
	}
	@SuppressWarnings("unchecked")
	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {

		IntegerLib<T> lib = new IntegerLib<>(env);

		// send and receive values
		if (machine.numberOfIncomingConnections > 0) {
			send(machine.peerOsDown[0], freq[freq.length - 1]);
			((BufferedOutputStream) machine.peerOsDown[0]).flush();
		}
		T[] last = env.inputOfAlice(Utils.fromInt(-1, 32));
		if (machine.numberOfOutgoingConnections > 0) {
			last = read(machine.peerIsUp[0], freq[0].length);
		}
		for (int i = input.length - 1; i > 0; i--) {
			freq[i] = lib.sub(freq[i], freq[i - 1]);
		}
		if (machine.numberOfOutgoingConnections > 0) {
			freq[0] = lib.sub(freq[0], last);
		}
		if (machine.machineId == 0) {
				for (int i = 0; i < 4; i++) {
					int int1 = Utils.toInt(env.outputToAlice(flag[i]));
					int int2 = Utils.toInt(env.outputToAlice(input[i]));
					int int3 = Utils.toInt(env.outputToAlice(freq[i]));
					if (Party.Alice.equals(env.party)) {
						System.out.println(machine.machineId + ": " + int2 + ", " + int3);
					}
				}
			}
		env.os.flush();
		return null;
	}
}
