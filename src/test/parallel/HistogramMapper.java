package test.parallel;

import java.io.IOException;

import test.Utils;
import circuits.IntegerLib;
import network.BadCommandException;
import flexsc.Gadget;
import gc.BadLabelException;

public class HistogramMapper<T> extends Gadget<T> {

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		T[][] input = (T[][]) inputs[0];
		T[][][] output = env.newTArray(2, input.length, input[0].length);
		output[0] = input;
		T[][] freq = env.newTArray(input.length, input[0].length);
		for (int i = 0; i < freq.length; i++) {
			freq[i] = env.inputOfAlice(Utils.fromInt(1, 32));
		}
		output[1] = freq;
		/*for (int i = 0; i < freq.length; i++) {
			System.out.println(" " + Utils.toInt(lib.getBooleans(input[i])) + ", " + Utils.toInt(lib.getBooleans(freq[i])));
		}*/
		return output;
	}
}
