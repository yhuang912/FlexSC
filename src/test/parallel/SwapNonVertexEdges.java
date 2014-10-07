package test.parallel;

import java.io.IOException;

import test.Utils;
import circuits.IntegerLib;
import network.BadCommandException;
import network.Machine;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;

public class SwapNonVertexEdges<T> extends Gadget<T> {

	T[][] u;
	T[][] v;

	public SwapNonVertexEdges(CompEnv<T> env, Machine machine) {
		super(env, machine);
	}

	public SwapNonVertexEdges<T> setInputs(T[][] u, T[][] v) {
		this.u = u;
		this.v = v;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		IntegerLib<T> lib = new IntegerLib<>(env);
		for (int i = 0; i < u.length; i++) {
			T[] u1 = u[i];
			T[] v1 = v[i];
			T[] intZero = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
			T swap = lib.not(lib.eq(v1, intZero));
			T[] s = lib.mux(v1, u1, swap);
			s = lib.xor(s, u1);
			u[i] = lib.xor(v1, s);
			v[i] = lib.xor(u1, s);
		}
		return null;
	}
}
