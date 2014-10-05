package test.parallel;

import java.io.IOException;

import network.BadCommandException;
import test.Utils;
import circuits.IntegerLib;
import flexsc.Gadget;
import flexsc.Party;
import gc.BadLabelException;

public class SetInitialPageRankGadget<T> extends Gadget<T> {

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		T[][] u = (T[][]) inputs[0];
		T[][] v = (T[][]) inputs[1];
		T[][][] output = env.newTArray(3, u.length, u[0].length);
		output[0] = u;
		output[1] = v;
		T[][] pr = env.newTArray(u.length, u[0].length);
		IntegerLib<T> intLib = new IntegerLib<T>(env);
		T[] one = env.inputOfAlice(Utils.fromFloat(1, 20, 12));
		T[] zero = env.inputOfAlice(Utils.fromFloat(0, 20, 12));
		for (int i = 0; i < u.length; i++) {
			T isVertex = intLib.eq(u[i], v[i]);
			pr[i] = intLib.mux(zero, one, isVertex);
		}
		output[2] = pr;
		for (int i = 0; i < pr.length; i++) {
			int a = Utils.toInt(env.outputToAlice(u[i]));
			int b = Utils.toInt(env.outputToAlice(v[i]));
			double c = Utils.toFloat(env.outputToAlice(pr[i]), 20, 12);
			if (Party.Alice.equals(env.party)) {
				System.out.println(" " + a + ", " + b + " " + c);
			}
		}
		return output;
	}

}
