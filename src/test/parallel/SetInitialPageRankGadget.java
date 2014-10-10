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

	private PageRankNode<T>[] prNodes;

	public SetInitialPageRankGadget(CompEnv<T> env,
			Machine machine) {
		super(env, machine);
	}

	public SetInitialPageRankGadget<T> setInputs(PageRankNode<T>[] prNodes) {
		this.prNodes = prNodes;
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
		for (int i = 0; i < prNodes.length; i++) {
			T isVertex = intLib.eq(prNodes[i].v, vertex);
			prNodes[i].pr = intLib.mux(zero, one, isVertex);
			prNodes[i].l = intLib.mux(intOne, intZero, isVertex);
		}
		return null;
	}

}
