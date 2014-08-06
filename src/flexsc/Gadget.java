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

	abstract public Object secureCompute(CompEnv<T> e, Object[] o) throws InterruptedException, IOException, BadCommandException, BadLabelException;

	@Override
	public Object call() throws InterruptedException, IOException, BadCommandException, BadLabelException {
		connect(port);
		Object res = secureCompute(env, inputs);
		env.flush();
		disconnect();
		return res;
	}
}