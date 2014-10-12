package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import network.BadCommandException;
import network.Machine;
import network.NetworkUtil;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;

public abstract class WriteToEdge<T> extends Gadget<T> {

	private GraphNode<T>[] nodes;
	private boolean isEdgeIncoming;

	public WriteToEdge(CompEnv<T> env, Machine machine, boolean isEdgeIncoming) {
		super(env, machine);
		this.isEdgeIncoming = isEdgeIncoming;
	}

	public WriteToEdge<T> setInputs(GraphNode<T>[] prNodes) {
		this.nodes = prNodes;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		if (isEdgeIncoming) {
			for (int j = 0; j < nodes.length; j++) {
				nodes[j].swapEdgeDirections();
			}
		}

		new SortGadget<T>(env, machine)
			.setInputs(nodes, nodes[0].getComparator(env, false /* isVertexLast */))
			.compute();

		IntegerLib<T> lib = new IntegerLib<>(env);
		T _true = env.newT(true);

		T foundToSend = env.newT(false);
		Class<?> componentType = nodes.getClass().getComponentType();
		Constructor<?> constructor = componentType.getConstructor(new Class[]{CompEnv.class});
		GraphNode<T> graphNode = (GraphNode<T>) constructor.newInstance(env);

		for (int i = 0; i < nodes.length; i++) {
			foundToSend = lib.mux(foundToSend, _true, nodes[i].isVertex); // always sent
			graphNode = nodes[i].mux(graphNode, nodes[i].isVertex, env);
		}

		T found = env.newT(false);
		GraphNode<T> graphNodeLast = (GraphNode<T>) constructor.newInstance(env);

		int noOfIncomingConnections = machine.numberOfIncomingConnections;
		int noOfOutgoingConnections = machine.numberOfOutgoingConnections;
		for (int k = 0; k < machine.logMachines; k++) {
			if (noOfIncomingConnections > 0) {
				NetworkUtil.send(machine.peerOsDown[k], foundToSend, env);
				graphNode.send(machine.peerOsDown[k], env);
				((BufferedOutputStream) machine.peerOsDown[k]).flush();
				noOfIncomingConnections--;
			}
			if (noOfOutgoingConnections > 0) {
				T foundRead = NetworkUtil.read(machine.peerIsUp[k], env);
				GraphNode<T> graphNodeRead = (GraphNode<T>) constructor.newInstance(env);
				graphNodeRead.read(machine.peerIsUp[k], env);

				// compute the value for the last vertex
				graphNodeLast = graphNodeLast.mux(graphNodeRead, found, env);
				found = lib.mux(found, _true, foundRead);
				noOfOutgoingConnections--;
			}
		}

		// found will always be true in the end!
		for (int i = 0; i < nodes.length; i++) {
			writeToEdge(graphNodeLast, nodes[i], nodes[i].isVertex);
			graphNodeLast = nodes[i].mux(graphNodeLast, nodes[i].isVertex, env);
		}

		if (isEdgeIncoming) {
			for (int j = 0; j < nodes.length; j++) {
				nodes[j].swapEdgeDirections();
			}
		}
		return null;
	}

	public abstract void writeToEdge(GraphNode<T> vertexNode, GraphNode<T> edgeNode, T cond);
}