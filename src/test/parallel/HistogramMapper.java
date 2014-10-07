package test.parallel;

import java.io.IOException;

import network.BadCommandException;
import network.Machine;
import test.Utils;
import flexsc.CompEnv;
import flexsc.Gadget;
import gc.BadLabelException;

public class HistogramMapper<T> extends Gadget<T> {

	private T[][] freq;

	public HistogramMapper(CompEnv<T> env, Machine machine) {
		super(env, machine);
	}

	public HistogramMapper<T> setInputs(T[][] freq) {
		this.freq = freq;
		return this;
	}

	@Override
	public Object secureCompute() throws InterruptedException, IOException,
			BadCommandException, BadLabelException {
		for (int i = 0; i < freq.length; i++) {
			freq[i] = env.inputOfAlice(Utils.fromInt(1, 32));
		}
		/*for (int i = 0; i < freq.length; i++) {
			System.out.println(" " + Utils.toInt(lib.getBooleans(input[i])) + ", " + Utils.toInt(lib.getBooleans(freq[i])));
		}*/
		return null;
	}
}
