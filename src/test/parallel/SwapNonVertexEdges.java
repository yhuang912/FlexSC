package test.parallel;

import java.io.IOException;

import network.BadCommandException;
import network.Machine;
import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;

public class SwapNonVertexEdges<T> extends Gadget<T> {

	private PageRankNode<T>[] prNodes;
	private boolean setVertexPrToZero;

	public SwapNonVertexEdges(CompEnv<T> env, Machine machine) {
		super(env, machine);
	}

	public SwapNonVertexEdges<T> setInputs(PageRankNode<T>[] prNodes, boolean setVertexPrToZero) {
		this.prNodes = prNodes;
		this.setVertexPrToZero = setVertexPrToZero;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		IntegerLib<T> lib = new IntegerLib<>(env);
		T[] floatZero = env.inputOfAlice(Utils.fromFloat(0, PageRank.FLOAT_P, PageRank.FLOAT_V));
		for (int i = 0; i < prNodes.length; i++) {
			T[] u1 = prNodes[i].u;
			T[] v1 = prNodes[i].v;
			T swap = lib.not(prNodes[i].isVertex); // isNotVertex
			T[] s = lib.mux(v1, u1, swap);
			s = lib.xor(s, u1);
			prNodes[i].u = lib.xor(v1, s);
			prNodes[i].v = lib.xor(u1, s);
			if (setVertexPrToZero) {
				prNodes[i].pr = lib.mux(floatZero, prNodes[i].pr, swap); // if is vertex, change pr[i] to 0
			}
		}
		return null;
	}
}
