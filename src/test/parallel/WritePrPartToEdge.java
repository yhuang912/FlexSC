package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;

import test.Utils;
import circuits.IntegerLib;
import circuits.arithmetic.FloatLib;
import network.BadCommandException;
import network.Machine;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;

public class WritePrPartToEdge<T> extends Gadget<T> {

	private T[][] u;
	private T[][] v;
	private T[][] pr;
	private T[][] l;

	public WritePrPartToEdge(Object[] inputs, CompEnv<T> env, Machine machine) {
		super(inputs, env, machine);
	}

	public WritePrPartToEdge<T> setInputs(T[][] u, T[][] v, T[][] pr, T[][] l) {
		this.u = u;
		this.v = v;
		this.pr = pr;
		this.l = l;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		IntegerLib<T> lib = new IntegerLib<>(env);
		FloatLib<T> floatLib = new FloatLib<>(env, 20, 12);
		T[] floatZero = env.inputOfAlice(Utils.fromFloat(0, 20, 12));
		T[] intZero = env.inputOfAlice(Utils.fromInt(0, 32));
		T isVertex;
		T[] val = lib.zeros(32);
		T _true = env.newT(true);

		T foundToSend = env.newT(false);
		T[] lastPrToSend = lib.zeros(32);
		T[] lastLToSend = lib.zeros(32);
		for (int i = 0; i < u.length; i++) {
			isVertex = lib.eq(v[i], intZero);
			foundToSend = lib.mux(foundToSend, _true, isVertex);
			lastPrToSend = lib.mux(lastPrToSend, pr[i], isVertex);
			lastLToSend = lib.mux(lastLToSend, l[i], isVertex);
		}

		T found = env.newT(false);
		T[] lastPr = lib.zeros(32);
		T[] lastL = lib.zeros(32);

		int noOfIncomingConnections = machine.numberOfIncomingConnections;
		int noOfOutgoingConnections = machine.numberOfOutgoingConnections;
		for (int k = 0; k < machine.logMachines; k++) {
			if (noOfIncomingConnections > 0) {
				send(machine.peerOsDown[k], foundToSend);
				send(machine.peerOsDown[k], lastPrToSend);
				send(machine.peerOsDown[k], lastLToSend);
				((BufferedOutputStream) machine.peerOsDown[k]).flush();
				noOfIncomingConnections--;
			}
			if (noOfOutgoingConnections > 0) {
				T foundRead = read(machine.peerIsUp[k]);
				T[] lastPrRead = read(machine.peerIsUp[k], pr[0].length);
				T[] lastLRead = read(machine.peerIsUp[k], l[0].length);
				lastPr = lib.mux(lastPrRead, lastPr, found);
				lastL = lib.mux(lastLRead, lastL, found);
				found = lib.mux(found, _true, foundRead);
				noOfOutgoingConnections--;
			}
		}

		// found will always be true in the end!
		for (int i = 0; i < u.length; i++) {
			isVertex = lib.eq(v[i], intZero);
			pr[i] = lib.mux(lastPr, pr[i], isVertex);
			// l[i] = lib.mux(lastL, l[i], isVertex);
			lastL = lib.mux(lastL, l[i], isVertex);
			lastPr = lib.mux(lastPr, pr[i], isVertex);
		}
		return null;
	}

}
