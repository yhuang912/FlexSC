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

public class ComputeL<T> extends Gadget<T> {

	private PageRankNode<T>[] prNodes;

	public ComputeL(CompEnv<T> env, Machine machine) {
		super(env, machine);
	}

	public ComputeL<T> setInputs(PageRankNode<T>[] prNodes) {
		this.prNodes = prNodes;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		IntegerLib<T> lib = new IntegerLib<>(env);
		T[] intZero = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
		T[] val = lib.zeros(PageRank.INT_LEN);

		for (int i = 0; i < prNodes.length; i++) {
			T[] temp = lib.add(val, prNodes[i].l);
			val = lib.mux(temp, intZero, prNodes[i].isVertex);
		}

		T[] initValForLaterComp = lib.zeros(PageRank.INT_LEN);

		int noOfIncomingConnections = machine.numberOfIncomingConnections;
		int noOfOutgoingConnections = machine.numberOfOutgoingConnections;
		for (int k = 0; k < machine.logMachines; k++) {
			if (noOfIncomingConnections > 0) {
				NetworkUtil.send(machine.peerOsDown[k], prNodes[prNodes.length - 1].u, env);
				NetworkUtil.send(machine.peerOsDown[k], val, env);
				((BufferedOutputStream) machine.peerOsDown[k]).flush();
				noOfIncomingConnections--;
			}
			if (noOfOutgoingConnections > 0) {
				T[] prevU = NetworkUtil.read(machine.peerIsUp[k], prNodes[0].u.length, env);
				T[] prevVal = NetworkUtil.read(machine.peerIsUp[k], val.length, env);
				T sameU = lib.eq(prevU, prNodes[0].u);
				T[] temp = lib.add(initValForLaterComp, prevVal);
				initValForLaterComp = lib.mux(initValForLaterComp, temp, sameU);
				noOfOutgoingConnections--;
			}
		}

		for (int i = 0; i < prNodes.length; i++) {
			T[] temp = lib.add(initValForLaterComp, prNodes[i].l);
			prNodes[i].l = lib.mux(prNodes[i].l, initValForLaterComp, prNodes[i].isVertex);
			initValForLaterComp = lib.mux(temp, intZero, prNodes[i].isVertex);
		}
		return null;
	}

}
