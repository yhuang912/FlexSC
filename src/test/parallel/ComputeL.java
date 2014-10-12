package test.parallel;

import circuits.IntegerLib;
import network.Machine;
import flexsc.CompEnv;

public class ComputeL<T> extends WriteToVertex<T> {

	public ComputeL(CompEnv<T> env, Machine machine, boolean isEdgeIncoming) {
		super(env, machine, isEdgeIncoming, new PageRankNode<T>(env));
	}

	@Override
	public GraphNode<T> aggFunc(GraphNode<T> aggNode, GraphNode<T> bNode) {
		PageRankNode<T> agg = (PageRankNode<T>) aggNode;
		PageRankNode<T> b = (PageRankNode<T>) bNode;

		IntegerLib<T> lib = new IntegerLib<>(env);
		PageRankNode<T> ret = new PageRankNode<T>(env);
		ret.l = lib.add(agg.l, b.l);
		return ret;
	}

	@Override
	public void writeToVertex(GraphNode<T> aggNode, GraphNode<T> bNode) {
		PageRankNode<T> agg = (PageRankNode<T>) aggNode;
		PageRankNode<T> b = (PageRankNode<T>) bNode;
		IntegerLib<T> lib = new IntegerLib<>(env);
		b.l = lib.mux(b.l, agg.l, b.isVertex);
	}
}
