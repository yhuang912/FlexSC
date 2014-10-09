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

public class WritePrPartToEdge<T> extends Gadget<T> {

	private T[][] u;
	private T[][] v;
	private T[][] pr;
	private T[][] l;

	public WritePrPartToEdge(CompEnv<T> env, Machine machine) {
		super(env, machine);
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
		T[] intZero = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
		T isVertex;
		T[] val = lib.zeros(PageRank.INT_LEN);
		T _true = env.newT(true);

		T foundToSend = env.newT(false);
		T[] lastPrToSend = lib.zeros(PageRank.INT_LEN);
		T[] lastLToSend = lib.zeros(PageRank.INT_LEN);
		for (int i = 0; i < u.length; i++) {
			isVertex = lib.eq(v[i], intZero);
			foundToSend = lib.mux(foundToSend, _true, isVertex); // always sent
			lastPrToSend = lib.mux(lastPrToSend, pr[i], isVertex); // specify what needs to be sent

			lastLToSend = lib.mux(lastLToSend, l[i], isVertex);
		}

		T found = env.newT(false);
		T[] lastPr = lib.zeros(32);
		T[] lastL = lib.zeros(32);

		int noOfIncomingConnections = machine.numberOfIncomingConnections;
		int noOfOutgoingConnections = machine.numberOfOutgoingConnections;
		for (int k = 0; k < machine.logMachines; k++) {
			if (noOfIncomingConnections > 0) {
				// send corresponding values
				NetworkUtil.send(machine.peerOsDown[k], foundToSend, env);
				NetworkUtil.send(machine.peerOsDown[k], lastPrToSend, env);
				NetworkUtil.send(machine.peerOsDown[k], lastLToSend, env);
				((BufferedOutputStream) machine.peerOsDown[k]).flush();
				noOfIncomingConnections--;
			}
			if (noOfOutgoingConnections > 0) {
				// read corresponding values
				T foundRead = NetworkUtil.read(machine.peerIsUp[k], env);
				T[] lastPrRead = NetworkUtil.read(machine.peerIsUp[k], pr[0].length, env);
				T[] lastLRead = NetworkUtil.read(machine.peerIsUp[k], l[0].length, env);

				// compute the value for the last vertex
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
