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

public abstract class WriteToEdge<T> extends Gadget<T> {

	private GraphNode<T>[] nodes;

	public WriteToEdge(CompEnv<T> env, Machine machine) {
		super(env, machine);
	}

	public WriteToEdge<T> setInputs(GraphNode<T>[] prNodes) {
		this.nodes = prNodes;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		IntegerLib<T> lib = new IntegerLib<>(env);
//		T[] intZero = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
		T _true = env.newT(true);

		T foundToSend = env.newT(false);
//		T[] lastPrToSend = lib.zeros(PageRank.INT_LEN);
//		T[] lastLToSend = lib.zeros(PageRank.INT_LEN);
		Class<?> componentType = nodes.getClass().getComponentType();
		Constructor<?> constructor = componentType.getConstructor(new Class[]{CompEnv.class});
		GraphNode<T> graphNode = (GraphNode<T>) constructor.newInstance(env);//componentType.newInstance();

//		double a1 = Utils.toFloat(env.outputToAlice(prNodes[0].pr), PageRank.FLOAT_V, PageRank.FLOAT_P);
//		int a2 = Utils.toInt(env.outputToAlice(prNodes[0].l));
//		if (Party.Alice.equals(env.getParty())) {
//			System.out.println("a1 " + a1);
//			System.out.println("a2 " + a2);
//		}
		for (int i = 0; i < nodes.length; i++) {
			foundToSend = lib.mux(foundToSend, _true, nodes[i].isVertex); // always sent

//			lastPrToSend = lib.mux(lastPrToSend, prNodes[i].pr, prNodes[i].isVertex); // specify what needs to be sent
//			lastLToSend = lib.mux(lastLToSend, prNodes[i].l, prNodes[i].isVertex);
			graphNode = nodes[i].mux(graphNode, nodes[i].isVertex, env);
		}
//
//		double b = Utils.toFloat(env.outputToAlice(((PageRankNode<T>) graphNode).pr), PageRank.FLOAT_V, PageRank.FLOAT_P);
//		int c = Utils.toInt(env.outputToAlice(((PageRankNode<T>) graphNode).l));
//		if (Party.Alice.equals(env.getParty())) {
//			System.out.println("b " + b);
//			System.out.println("C " + c);
//		}
//		lastPrToSend = ((PageRankNode<T>) graphNode).pr;
//		lastLToSend = ((PageRankNode<T>) graphNode).l;

		T found = env.newT(false);
		GraphNode<T> graphNodeLast = (GraphNode<T>) constructor.newInstance(env);
//		T[] lastPr = lib.zeros(PageRank.INT_LEN);
//		T[] lastL = lib.zeros(PageRank.INT_LEN);

		int noOfIncomingConnections = machine.numberOfIncomingConnections;
		int noOfOutgoingConnections = machine.numberOfOutgoingConnections;
		for (int k = 0; k < machine.logMachines; k++) {
			if (noOfIncomingConnections > 0) {
				// send corresponding values
				NetworkUtil.send(machine.peerOsDown[k], foundToSend, env);

//				NetworkUtil.send(machine.peerOsDown[k], lastPrToSend, env);
//				NetworkUtil.send(machine.peerOsDown[k], lastLToSend, env);
				graphNode.send(machine.peerOsDown[k], env);
				((BufferedOutputStream) machine.peerOsDown[k]).flush();
				noOfIncomingConnections--;
			}
			if (noOfOutgoingConnections > 0) {
				// read corresponding values
				T foundRead = NetworkUtil.read(machine.peerIsUp[k], env);

				GraphNode<T> graphNodeRead = (GraphNode<T>) constructor.newInstance(env);
				graphNodeRead.read(machine.peerIsUp[k], env);

//				T[] lastPrRead = ((PageRankNode<T>) graphNodeRead).pr;
//				T[] lastLRead = ((PageRankNode<T>) graphNodeRead).l;
//				T[] lastPrRead = NetworkUtil.read(machine.peerIsUp[k], prNodes[0].pr.length, env);
//				T[] lastLRead = NetworkUtil.read(machine.peerIsUp[k], prNodes[0].l.length, env);

				// compute the value for the last vertex
				graphNodeLast = graphNodeLast.mux(graphNodeRead, found, env);
//				lastPr = lib.mux(lastPrRead, lastPr, found);
//				lastL = lib.mux(lastLRead, lastL, found);
				found = lib.mux(found, _true, foundRead);
				noOfOutgoingConnections--;
			}
		}

//		lastPr = ((PageRankNode<T>) graphNodeLast).pr;
//		lastL = ((PageRankNode<T>) graphNodeLast).l;
		// found will always be true in the end!
		for (int i = 0; i < nodes.length; i++) {
//			prNodes[i].pr = lib.mux(lastPr, prNodes[i].pr, prNodes[i].isVertex);

			writeToEdge(graphNodeLast, nodes[i], nodes[i].isVertex);
			// l[i] = lib.mux(lastL, l[i], isVertex);
			graphNodeLast = nodes[i].mux(graphNodeLast, nodes[i].isVertex, env);
//			lastL = lib.mux(lastL, prNodes[i].l, prNodes[i].isVertex);
//			lastPr = lib.mux(lastPr, prNodes[i].pr, prNodes[i].isVertex);

//			lastPr = ((PageRankNode<T>) graphNodeLast).pr;
		}
		return null;
	}

	public abstract void writeToEdge(GraphNode<T> vertexNode, GraphNode<T> edgeNode, T cond);
}
