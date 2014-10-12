package test.parallel;

import circuits.IntegerLib;
import network.Machine;
import flexsc.CompEnv;

public class WritePrPartToEdge<T> extends WriteToEdge<T> {

	public WritePrPartToEdge(CompEnv<T> env, Machine machine) {
		super(env, machine);
	}

	@Override
	public void writeToEdge(GraphNode<T> vertexNode, GraphNode<T> edgeNode, T cond) {
		PageRankNode<T> vertex = (PageRankNode<T>) vertexNode;
		PageRankNode<T> edge = (PageRankNode<T>) edgeNode;
		IntegerLib<T> lib = new IntegerLib<>(env);
		edge.pr = lib.mux(vertex.pr, edge.pr, cond);
	}
}
