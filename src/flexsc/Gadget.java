package flexsc;

import gc.BadLabelException;

import java.io.IOException;
import java.util.concurrent.Callable;

import network.BadCommandException;
import network.Machine;

public abstract class Gadget<T> extends Machine implements Callable<Object> {
	Object[] inputs;
	CompEnv<T> env;
	int port;

	abstract public Object secureCompute(CompEnv<T> e, Object[] o, int port) throws InterruptedException, IOException, BadCommandException, BadLabelException;

	@Override
	public Object call() {
		Object res = null;
		try {
			res = secureCompute(env, inputs, port);
			env.flush();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return res;
	}
}