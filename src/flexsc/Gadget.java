package flexsc;

import java.util.concurrent.Callable;

import network.Machine;

public abstract class Gadget<T> extends Machine implements Callable<Object>, Cloneable {
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

	public Gadget<T> clone() { 
		try { 
			@SuppressWarnings("unchecked")
			Gadget<T> res = (Gadget<T>) super.clone();
			res.inputs = null;
			res.env = null;
			return res;

		} catch(CloneNotSupportedException e) { 
			System.out.println("Cloning not allowed."); 
			return this; 
		}
	}

}