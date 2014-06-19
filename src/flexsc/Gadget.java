package flexsc;

import java.util.concurrent.Callable;


public abstract class Gadget<T> implements Callable<Object>{
	Object[] inputs;
	CompEnv<T> env;

	abstract public  Object secureCompute(CompEnv<T> e, Object[] o) throws Exception;

	@Override
	public Object call() throws Exception {
		Object res = null;
		try {
			res = secureCompute(env, inputs);
			env.flush();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return res;
	}

	protected abstract Gadget<T> getGadget();

}