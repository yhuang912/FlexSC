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
//		T[] one = env.inputOfAlice(Utils.fromFloat(1, PageRank.FLOAT_V, PageRank.FLOAT_P));
//		T[] zero = env.inputOfAlice(Utils.fromFloat(0.000001, PageRank.FLOAT_V, PageRank.FLOAT_P));

		T[] one = env.inputOfAlice(Utils.fromFixPoint(1, PageRank.WIDTH, PageRank.OFFSET));
		T[] zero = env.inputOfAlice(Utils.fromFixPoint(0, PageRank.WIDTH, PageRank.OFFSET));
//		T[] intOne = env.inputOfAlice(Utils.fromInt(1, PageRank.INT_LEN));
//		T[] intZero = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
		for (int i = 0; i < prNodes.length; i++) {
			prNodes[i].pr = intLib.mux(zero, one, prNodes[i].isVertex);
//			prNodes[i].l = intLib.mux(intOne, intZero, prNodes[i].isVertex);
			prNodes[i].l = intLib.mux(one, zero, prNodes[i].isVertex);
		}
		return null;
	}

}
