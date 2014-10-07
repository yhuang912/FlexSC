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
	private T[][] x;
	private T[][] prefixSum;

	public SubtractGadgetForHistogram(Object[] inputs, CompEnv<T> env,
			Machine machine) {
		super(inputs, env, machine);
	}

	public SubtractGadgetForHistogram<T> setInputs(T[][] flag, T[][] x, T[][] prefixSum) {
		this.flag = flag;
		this.x = x;
		this.prefixSum = prefixSum;
		return this;
	}
	@SuppressWarnings("unchecked")
	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {

		IntegerLib<T> lib = new IntegerLib<>(env);

		T[][] data = Utils.flatten(env, x, prefixSum);
		SortGadget<T> gadge = new SortGadget<T>(inputs, env, machine)
				.setInputs(flag, data, new SimpleComparator<T>(env));
		Object[] sortOutput = (Object[]) gadge.secureCompute();

		
		flag = (T[][]) sortOutput[0];
		data = (T[][]) sortOutput[1];
		Utils.unflatten(data, x, prefixSum);

		// send and receive values
		if (machine.numberOfIncomingConnections > 0) {
			send(machine.peerOsDown[0], prefixSum[prefixSum.length - 1]);
			((BufferedOutputStream) machine.peerOsDown[0]).flush();
		}
		T[] last = env.inputOfAlice(Utils.fromInt(-1, 32));
		if (machine.numberOfOutgoingConnections > 0) {
			last = read(machine.peerIsUp[0], prefixSum[0].length);
		}
		for (int i = x.length - 1; i > 0; i--) {
			prefixSum[i] = lib.sub(prefixSum[i], prefixSum[i - 1]);
		}
		if (machine.numberOfOutgoingConnections > 0) {
			prefixSum[0] = lib.sub(prefixSum[0], last);
		}
		if (machine.machineId == 0) {
				for (int i = 0; i < 4; i++) {
					int int1 = Utils.toInt(env.outputToAlice(flag[i]));
					int int2 = Utils.toInt(env.outputToAlice(x[i]));
					int int3 = Utils.toInt(env.outputToAlice(prefixSum[i]));
					if (Party.Alice.equals(env.party)) {
						System.out.println(machine.machineId + ": " + int2 + ", " + int3);
					}
				}
			}
		env.os.flush();
		return null;
	}
}
