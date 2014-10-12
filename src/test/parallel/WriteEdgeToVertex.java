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

public abstract class WriteEdgeToVertex<T> extends Gadget<T> {

	private GraphNode<T>[] nodes;
	private boolean isEdgeIncoming;

	public WriteEdgeToVertex(CompEnv<T> env, Machine machine, boolean isEdgeIncoming) {
		super(env, machine);
		this.isEdgeIncoming = isEdgeIncoming;
	}

	public WriteEdgeToVertex<T> setInputs(GraphNode<T>[] nodes) {
		this.nodes = nodes;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (isEdgeIncoming) {
			for (int j = 0; j < nodes.length; j++) {
				nodes[j].swapEdgeDirections();
			}
		}

		new SortGadget<T>(env, machine)
			.setInputs(nodes, nodes[0].getComparator(env, true /* isVertexLast */))
			.compute();
		IntegerLib<T> lib = new IntegerLib<>(env);

		Class<?> componentType = nodes.getClass().getComponentType();
		Constructor<?> constructor = componentType.getConstructor(new Class[]{CompEnv.class});
		GraphNode<T> graphNodeVal = (GraphNode<T>) constructor.newInstance(env);
		GraphNode<T> zeroNode = (GraphNode<T>) constructor.newInstance(env);

		for (int i = 0; i < nodes.length; i++) {
			GraphNode<T> tempAgg = aggFunc(graphNodeVal, nodes[i]);
			graphNodeVal = zeroNode.mux(tempAgg, nodes[i].isVertex, env);
		}

		GraphNode<T> nodeValForLaterComp = (GraphNode<T>) constructor.newInstance(env);

		int noOfIncomingConnections = machine.numberOfIncomingConnections;
		int noOfOutgoingConnections = machine.numberOfOutgoingConnections;
		for (int k = 0; k < machine.logMachines; k++) {
			if (noOfIncomingConnections > 0) {
				NetworkUtil.send(machine.peerOsDown[k], nodes[nodes.length - 1].u, env);
				graphNodeVal.send(machine.peerOsDown[k], env);
				((BufferedOutputStream) machine.peerOsDown[k]).flush();
				noOfIncomingConnections--;
			}
			if (noOfOutgoingConnections > 0) {
				T[] prevU = NetworkUtil.read(machine.peerIsUp[k], nodes[0].u.length, env);
				GraphNode<T> graphNodeRead = (GraphNode<T>) constructor.newInstance(env);
				graphNodeRead.read(machine.peerIsUp[k], env);
				T sameU = lib.eq(prevU, nodes[0].u);
				GraphNode<T> tempAgg = aggFunc(nodeValForLaterComp, graphNodeRead);
				nodeValForLaterComp = tempAgg.mux(nodeValForLaterComp, sameU, env);
				noOfOutgoingConnections--;
			}
		}

		for (int i = 0; i < nodes.length; i++) {
			GraphNode<T> tempAgg = aggFunc(nodeValForLaterComp, nodes[i]);
			writeToVertex(nodeValForLaterComp, nodes[i]);
			nodeValForLaterComp = zeroNode.mux(tempAgg, nodes[i].isVertex, env);
		}

		if (isEdgeIncoming) {
			for (int j = 0; j < nodes.length; j++) {
				nodes[j].swapEdgeDirections();
			}
		}
		return null;
	}

	public abstract GraphNode<T> aggFunc(GraphNode<T> agg, GraphNode<T> b);

	public abstract void writeToVertex(GraphNode<T> agg, GraphNode<T> b);
}
