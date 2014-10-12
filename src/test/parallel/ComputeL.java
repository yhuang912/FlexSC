package test.parallel;

import circuits.IntegerLib;
import network.Machine;
import flexsc.CompEnv;

public class ComputeL<T> extends WriteEdgeToVertex<T> {

	public ComputeL(CompEnv<T> env, Machine machine) {
		super(env, machine);
	}

	@Override
	public GraphNode<T> aggFunc(GraphNode<T> aggNode, GraphNode<T> bNode) {
		PageRankNode<T> agg = (PageRankNode<T>) aggNode;
		PageRankNode<T> b = (PageRankNode<T>) bNode;

		IntegerLib<T> lib = new IntegerLib<>(env);
		PageRankNode<T> ret = new PageRankNode<T>(env);
//		ret.u = agg.u; // don't care
//		ret.v = agg.v; // don't care
//		ret.pr = agg.pr; // don't care
		ret.l = lib.add(agg.l, b.l);
//		ret.isVertex = agg.isVertex; // don't care
		return ret;
	}

	@Override
	public void writeToVertex(GraphNode<T> aggNode, GraphNode<T> bNode) {
		PageRankNode<T> agg = (PageRankNode<T>) aggNode;
		PageRankNode<T> b = (PageRankNode<T>) bNode;

		IntegerLib<T> lib = new IntegerLib<>(env);
		b.l = lib.mux(b.l, agg.l, b.isVertex);
//		return null;
	}

}
