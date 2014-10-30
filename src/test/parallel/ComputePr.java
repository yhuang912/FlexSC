package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;

import test.Utils;
import circuits.IntegerLib;
import circuits.arithmetic.FloatLib;
import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;

public class ComputePr<T> extends Gadget<T> {

	private PageRankNode<T>[] prNodes;

	public ComputePr(CompEnv<T> env, Machine machine) {
		super(env, machine);
	}

	public ComputePr<T> setInputs(PageRankNode<T>[] prNodes) {
		this.prNodes = prNodes;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
////		FloatLib<T> lib = new FloatLib<T>(env, PageRank.FLOAT_V, PageRank.FLOAT_P);
//		IntegerLib<T> intLib = new IntegerLib<>(env);
////		T[] zero = env.inputOfAlice(Utils.fromFloat(0, PageRank.FLOAT_V, PageRank.FLOAT_P));
//		T[] val = zero;
//
//		for (int i = 0; i < prNodes.length; i++) {
//			T[] temp = lib.add(val, prNodes[i].pr);
//			val = intLib.mux(temp, zero, prNodes[i].isVertex);
//		}
//
//		T[] initValForLaterComp = zero;
//
//		int noOfIncomingConnections = machine.numberOfIncomingConnections;
//		int noOfOutgoingConnections = machine.numberOfOutgoingConnections;
//		for (int k = 0; k < machine.logMachines; k++) {
//			if (noOfIncomingConnections > 0) {
//				NetworkUtil.send(machine.peerOsDown[k], prNodes[prNodes.length - 1].u, env);
//				NetworkUtil.send(machine.peerOsDown[k], val, env);
//				((BufferedOutputStream) machine.peerOsDown[k]).flush();
//				noOfIncomingConnections--;
//			}
//			if (noOfOutgoingConnections > 0) {
//				T[] prevU = NetworkUtil.read(machine.peerIsUp[k], prNodes[0].u.length, env);
//				T[] prevVal = NetworkUtil.read(machine.peerIsUp[k], val.length, env);
////				T sameU = lib.eq(prevU, prNodes[0].u);
////				T[] temp = lib.add(initValForLaterComp, prevVal);
//				initValForLaterComp = intLib.mux(initValForLaterComp, temp, sameU);
//				noOfOutgoingConnections--;
//			}
//		}
//
//		for (int i = 0; i < prNodes.length; i++) {
//			T[] temp = lib.add(initValForLaterComp, prNodes[i].pr);
//			prNodes[i].pr = intLib.mux(prNodes[i].pr, initValForLaterComp, prNodes[i].isVertex);
//			initValForLaterComp = intLib.mux(temp, zero, prNodes[i].isVertex);
//		}
		return null;
	}

}
