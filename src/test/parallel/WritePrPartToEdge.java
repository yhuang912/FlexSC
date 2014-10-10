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

	private PageRankNode<T>[] prNodes;

	public WritePrPartToEdge(CompEnv<T> env, Machine machine) {
		super(env, machine);
	}

	public WritePrPartToEdge<T> setInputs(PageRankNode<T>[] prNodes) {
		this.prNodes = prNodes;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		IntegerLib<T> lib = new IntegerLib<>(env);
		T[] intZero = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
		T _true = env.newT(true);

		T foundToSend = env.newT(false);
		T[] lastPrToSend = lib.zeros(PageRank.INT_LEN);
		T[] lastLToSend = lib.zeros(PageRank.INT_LEN);
		for (int i = 0; i < prNodes.length; i++) {
			foundToSend = lib.mux(foundToSend, _true, prNodes[i].isVertex); // always sent

			lastPrToSend = lib.mux(lastPrToSend, prNodes[i].pr, prNodes[i].isVertex); // specify what needs to be sent
			lastLToSend = lib.mux(lastLToSend, prNodes[i].l, prNodes[i].isVertex);
		}

		T found = env.newT(false);
		T[] lastPr = lib.zeros(PageRank.INT_LEN);
		T[] lastL = lib.zeros(PageRank.INT_LEN);

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

				T[] lastPrRead = NetworkUtil.read(machine.peerIsUp[k], prNodes[0].pr.length, env);
				T[] lastLRead = NetworkUtil.read(machine.peerIsUp[k], prNodes[0].l.length, env);

				// compute the value for the last vertex
				lastPr = lib.mux(lastPrRead, lastPr, found);
				lastL = lib.mux(lastLRead, lastL, found);
				found = lib.mux(found, _true, foundRead);
				noOfOutgoingConnections--;
			}
		}

		// found will always be true in the end!
		for (int i = 0; i < prNodes.length; i++) {
			prNodes[i].pr = lib.mux(lastPr, prNodes[i].pr, prNodes[i].isVertex);
			// l[i] = lib.mux(lastL, l[i], isVertex);
			lastL = lib.mux(lastL, prNodes[i].l, prNodes[i].isVertex);
			lastPr = lib.mux(lastPr, prNodes[i].pr, prNodes[i].isVertex);
		}
		return null;
	}

}
