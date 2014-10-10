package test.parallel;

import java.io.IOException;

import network.BadCommandException;
import network.Machine;
import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;

public class SetInitialPageRankGadget<T> extends Gadget<T> {

	private PageRankNode<T>[] prNode;

	public SetInitialPageRankGadget(CompEnv<T> env,
			Machine machine) {
		super(env, machine);
	}

	public SetInitialPageRankGadget<T> setInputs(GraphNode<T>[] graphNodes) {
		this.prNode = (PageRankNode<T>[]) graphNodes;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		IntegerLib<T> intLib = new IntegerLib<T>(env);
		T[] one = env.inputOfAlice(Utils.fromFloat(1, PageRank.FLOAT_V, PageRank.FLOAT_P));
		T[] zero = env.inputOfAlice(Utils.fromFloat(0, PageRank.FLOAT_V, PageRank.FLOAT_P));
		T[] vertex = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
		T[] intOne = env.inputOfAlice(Utils.fromInt(1, PageRank.INT_LEN));
		T[] intZero = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
		for (int i = 0; i < prNode.length; i++) {
			T isVertex = intLib.eq(prNode[i].v, vertex);
			prNode[i].pr = intLib.mux(zero, one, isVertex);
			prNode[i].l = intLib.mux(intOne, intZero, isVertex);
		}
		return null;
	}

}
