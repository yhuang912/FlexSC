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
import flexsc.Party;
import gc.BadLabelException;

public class MarkerWithLastValueGadget<T> extends Gadget<T> {

	private int numberOfIncomingConnections;
	private int numberOfOutgoingConnections;
	private int totalMachines;

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		T[][] x = (T[][]) inputs[0];
		if (numberOfOutgoingConnections > 0) {
			send(peerOsUp[0], x[0]);
			((BufferedOutputStream) peerOsUp[0]).flush();
		}
		T[] last = env.inputOfAlice(Utils.fromInt(-1, 32));
		if (numberOfIncomingConnections > 0) {
			last = read(peerIsDown[0], x[0].length);
		}
		IntegerLib<T> lib =  new IntegerLib<T>(env);
		T[][] marker = env.newTArray(x.length, x[0].length);
		int unwanted = totalMachines * x.length + x.length;
		int wanted = machineId * x.length;
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

	public void setInputs(Object[] inputs, 
			CompEnv<T> env,
			int machineId,
			InputStream[] peerIsUp,
			OutputStream[] peerOsUp,
			InputStream[] peerIsDown,
			OutputStream[] peerOsDown,
			int logMachines,
			int numberOfIncomingConnections,
			int numberOfOutgoingConnections,
			int totalMachines) {
		setInputs(inputs, env, machineId, peerIsUp, peerOsUp, peerIsDown, peerOsDown, logMachines);
		this.numberOfIncomingConnections = numberOfIncomingConnections;
		this.numberOfOutgoingConnections = numberOfOutgoingConnections;
		this.totalMachines = totalMachines;
	}
}
