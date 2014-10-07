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

	private T[][] u;
	private T[][] v;
	private T[][] pr;
	private boolean setVertexPrToZero;

	public SwapNonVertexEdges(CompEnv<T> env, Machine machine) {
		super(env, machine);
	}

	public SwapNonVertexEdges<T> setInputs(T[][] u, T[][] v, T[][] pr, boolean setVertexPrToZero) {
		this.u = u;
		this.v = v;
		this.pr = pr;
		this.setVertexPrToZero = setVertexPrToZero;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		IntegerLib<T> lib = new IntegerLib<>(env);
		T[] intZero = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
		T[] floatZero = env.inputOfAlice(Utils.fromFloat(0, PageRank.FLOAT_P, PageRank.FLOAT_V));
		for (int i = 0; i < u.length; i++) {
			T[] u1 = u[i];
			T[] v1 = v[i];
			T swap = lib.not(lib.eq(v1, intZero)); // isNotVertex
			T[] s = lib.mux(v1, u1, swap);
			s = lib.xor(s, u1);
			u[i] = lib.xor(v1, s);
			v[i] = lib.xor(u1, s);
			if (setVertexPrToZero) {
				pr[i] = lib.mux(floatZero, pr[i], swap); // if is vertex, change pr[i] to 0
			}
		}
		return null;
	}
}
