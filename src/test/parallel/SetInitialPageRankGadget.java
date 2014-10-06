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

	public SetInitialPageRankGadget(Object[] inputs, CompEnv<T> env,
			Machine machine) {
		super(inputs, env, machine);
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		T[][] u = (T[][]) inputs[0];
		T[][] v = (T[][]) inputs[1];
		T[][][] output = env.newTArray(4, u.length, u[0].length);
		output[0] = u;
		output[1] = v;
		T[][] pr = env.newTArray(u.length, u[0].length);
		T[][] l = env.newTArray(u.length, u[0].length);
		IntegerLib<T> intLib = new IntegerLib<T>(env);
		T[] one = env.inputOfAlice(Utils.fromFloat(1, 20, 12));
		T[] zero = env.inputOfAlice(Utils.fromFloat(0, 20, 12));
		T[] vertex = env.inputOfAlice(Utils.fromInt(0, 32));
		T[] intOne = env.inputOfAlice(Utils.fromInt(1, 32));
		T[] intZero = env.inputOfAlice(Utils.fromInt(0, 32));
		for (int i = 0; i < u.length; i++) {
			T isVertex = intLib.eq(v[i], vertex);
			pr[i] = intLib.mux(zero, one, isVertex);
			l[i] = intLib.mux(intOne, intZero, isVertex);
		}
		output[2] = pr;
		output[3] = l;
		/*for (int i = 0; i < pr.length; i++) {
			int a = Utils.toInt(env.outputToAlice(u[i]));
			int b = Utils.toInt(env.outputToAlice(v[i]));
			double c = Utils.toFloat(env.outputToAlice(pr[i]), 20, 12);
			if (Party.Alice.equals(env.party)) {
				System.out.println(" " + a + ", " + b + " " + c);
			}
		}*/
		return output;
	}

}
