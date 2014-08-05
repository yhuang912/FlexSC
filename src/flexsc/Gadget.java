package flexsc;

import java.util.concurrent.Callable;

import network.Machine;

public abstract class Gadget<T> extends Machine implements Callable<Object> {
	Object[] inputs;
	CompEnv<T> env;
	int port;

	abstract public Object secureCompute(CompEnv<T> e, Object[] o, int port) throws Exception;

	@Override
	public Object call() throws Exception {
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