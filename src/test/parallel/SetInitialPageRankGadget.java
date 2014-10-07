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

	private T[][] u;
	private T[][] v;
	private T[][] pr;
	private T[][] l;

	public SetInitialPageRankGadget(CompEnv<T> env,
			Machine machine) {
		super(env, machine);
	}

	public SetInitialPageRankGadget<T> setInputs(T[][] u, T[][] v, T[][] pr, T[][] l) {
		this.u = u;
		this.v = v;
		this.pr = pr;
		this.l = l;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		IntegerLib<T> intLib = new IntegerLib<T>(env);
		T[] one = env.inputOfAlice(Utils.fromFloat(1, PageRank.FLOAT_P, PageRank.FLOAT_V));
		T[] zero = env.inputOfAlice(Utils.fromFloat(0, PageRank.FLOAT_P, PageRank.FLOAT_V));
		T[] vertex = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
		T[] intOne = env.inputOfAlice(Utils.fromInt(1, PageRank.INT_LEN));
		T[] intZero = env.inputOfAlice(Utils.fromInt(0, PageRank.INT_LEN));
		for (int i = 0; i < u.length; i++) {
			T isVertex = intLib.eq(v[i], vertex);
			pr[i] = intLib.mux(zero, one, isVertex);
			l[i] = intLib.mux(intOne, intZero, isVertex);
		}
		/*for (int i = 0; i < pr.length; i++) {
			int a = Utils.toInt(env.outputToAlice(u[i]));
			int b = Utils.toInt(env.outputToAlice(v[i]));
			double c = Utils.toFloat(env.outputToAlice(pr[i]), PageRank.FLOAT_P, 12);
			if (Party.Alice.equals(env.party)) {
				System.out.println(" " + a + ", " + b + " " + c);
			}
		}*/
		return null;
	}

}
