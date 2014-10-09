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

public class MarkerWithLastValueGadget<T> extends Gadget<T> {

	private T[][] x;
	private T[][] marker;

	public MarkerWithLastValueGadget(CompEnv<T> env,
			Machine machine) {
		super(env, machine);
	}

	public MarkerWithLastValueGadget<T> setInputs(T[][] x, T[][] marker) {
		this.x = x;
		this.marker = marker;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		if (machine.numberOfOutgoingConnections > 0) {
			NetworkUtil.send(machine.peerOsUp[0], x[0], env);
			((BufferedOutputStream) machine.peerOsUp[0]).flush();
		}
		T[] last = env.inputOfAlice(Utils.fromInt(-1, 32));
		if (machine.numberOfIncomingConnections > 0) {
			last = NetworkUtil.read(machine.peerIsDown[0], x[0].length, env);
		}
		IntegerLib<T> lib =  new IntegerLib<T>(env);
		int unwanted = machine.totalMachines * x.length + x.length;
		int wanted = machine.machineId * x.length;
		for (int i = 1; i < x.length; i++) {
			T[] one = env.inputOfAlice(Utils.fromInt(unwanted, 32));
			T[] zero = env.inputOfAlice(Utils.fromInt(wanted, 32));
			T eq = lib.eq(x[i], x[i - 1]);
			marker[i - 1] = lib.mux(zero, one, eq);
			wanted++;
			unwanted++;
		}
		T[] one = env.inputOfAlice(Utils.fromInt(unwanted, 32));
		T[] zero = env.inputOfAlice(Utils.fromInt(wanted, 32));
		T eq = lib.eq(x[x.length - 1], last);
		marker[x.length - 1] = lib.mux(zero, one, eq);
		/* for (int i = 0; i < x.length; i++) {
    		int int1 = Utils.toInt(lib.getBooleans(x[i]));
			int int2 = Utils.toInt(lib.getBooleans(marker[i]));
			if (Party.Alice.equals(env.party)) {
				System.out.println(machineId + ": " + int1 + ", " + int2);
			}
		} */
		return marker;
	}
}
