package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;
import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.Gadget;
import flexsc.Party;
import gc.BadLabelException;

public abstract class WriteEdgeToVertex<T> extends Gadget<T> {

	private GraphNode<T>[] nodes;

	public WriteEdgeToVertex(CompEnv<T> env, Machine machine) {
		super(env, machine);
	}

	public WriteEdgeToVertex<T> setInputs(GraphNode<T>[] nodes) {
		this.nodes = nodes;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		IntegerLib<T> lib = new IntegerLib<>(env);
		T[] intZero = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
		T[] val = lib.zeros(PageRank.INT_LEN);

		Class<?> componentType = nodes.getClass().getComponentType();
		Constructor<?> constructor = componentType.getConstructor(new Class[]{CompEnv.class});
		GraphNode<T> graphNodeVal = (GraphNode<T>) constructor.newInstance(env);
		GraphNode<T> zeroNode = (GraphNode<T>) constructor.newInstance(env);

		for (int i = 0; i < nodes.length; i++) {
			GraphNode<T> tempAgg = aggFunc(graphNodeVal, nodes[i]);
//			T[] temp = lib.add(val, nodes[i].l);
//			val = lib.mux(((PageRankNode<T>) tempAgg).l, intZero, nodes[i].isVertex);
			graphNodeVal = zeroNode.mux(tempAgg, nodes[i].isVertex, env);
		}

//		val = ((PageRankNode<T>) graphNodeVal).l;
		T[] initValForLaterComp = lib.zeros(PageRank.INT_LEN);
		GraphNode<T> nodeValForLaterComp = (GraphNode<T>) constructor.newInstance(env);

		int noOfIncomingConnections = machine.numberOfIncomingConnections;
		int noOfOutgoingConnections = machine.numberOfOutgoingConnections;
		for (int k = 0; k < machine.logMachines; k++) {
			if (noOfIncomingConnections > 0) {
				NetworkUtil.send(machine.peerOsDown[k], nodes[nodes.length - 1].u, env);

//				NetworkUtil.send(machine.peerOsDown[k], val, env);
				graphNodeVal.send(machine.peerOsDown[k], env);
//				print(machine.machineId, env, (PageRankNode<T>) graphNodeVal);
//				nodes[0].send(machine.peerOsDown[k], env);
				((BufferedOutputStream) machine.peerOsDown[k]).flush();
				noOfIncomingConnections--;
			}
			if (noOfOutgoingConnections > 0) {
				T[] prevU = NetworkUtil.read(machine.peerIsUp[k], nodes[0].u.length, env);
				GraphNode<T> graphNodeRead = (GraphNode<T>) constructor.newInstance(env);
				graphNodeRead.read(machine.peerIsUp[k], env);
				T[] prevVal = ((PageRankNode<T>) graphNodeRead).l;
//				T[] prevVal = NetworkUtil.read(machine.peerIsUp[k], val.length, env);
				T sameU = lib.eq(prevU, nodes[0].u);
				GraphNode<T> tempAgg = aggFunc(nodeValForLaterComp, graphNodeRead);
//				T[] temp = lib.add(initValForLaterComp, prevVal);
//				T[] temp = ((PageRankNode<T>) tempAgg).l;
//				initValForLaterComp = lib.mux(initValForLaterComp, temp, sameU);
				nodeValForLaterComp = tempAgg.mux(nodeValForLaterComp, sameU, env);
//				initValForLaterComp = ((PageRankNode<T>) nodeValForLaterComp).l;
				noOfOutgoingConnections--;
			}
		}

		for (int i = 0; i < nodes.length; i++) {
//			T[] temp = lib.add(initValForLaterComp, nodes[i].l);
			GraphNode<T> tempAgg = aggFunc(nodeValForLaterComp, nodes[i]);
			writeToVertex(nodeValForLaterComp, nodes[i]);
//	     		nodes[i] = nodeValForLaterComp.mux(nodes[i], nodes[i].isVertex, env);
//			nodes[i].l = lib.mux(nodes[i].l, initValForLaterComp, nodes[i].isVertex);
			nodeValForLaterComp = zeroNode.mux(tempAgg, nodes[i].isVertex, env);
//			initValForLaterComp = lib.mux(temp, intZero, nodes[i].isVertex);
		}
		return null;
	}

	public <T> void print(int machineId, final CompEnv<T> env, PageRankNode<T> pr) throws IOException, BadLabelException {
		int a = Utils.toInt(env.outputToAlice(pr.u));
		int b = Utils.toInt(env.outputToAlice(pr.v));
		double c2 = Utils.toFloat(env.outputToAlice(pr.pr), PageRank.FLOAT_V, PageRank.FLOAT_P);
		int d = Utils.toInt(env.outputToAlice(pr.l));
		if (Party.Alice.equals(env.party)) {
			System.out.println("----" + machineId + ": " + a + ", " + b + "\t" + c2 + "\t" + d);
		}
	}

	public abstract GraphNode<T> aggFunc(GraphNode<T> agg, GraphNode<T> b);

	public abstract void writeToVertex(GraphNode<T> agg, GraphNode<T> b);
}
