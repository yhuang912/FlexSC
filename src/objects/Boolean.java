package objects;

import circuits.CircuitLib;
import flexsc.CompEnv;
import flexsc.Signal;

public class Boolean<T extends Signal> {
	CompEnv<T> env;
	private T content;
	private CircuitLib<T> lib;
	
	public Boolean(CompEnv<T> env, T s) {
		this.env = env;
		lib = new CircuitLib<T>(env);
		content = s;
	}
	
	Boolean<T> and(Boolean<T> b) {
		T result = lib.and(this.content, b.content);
		return new Boolean<T>(env, result);
	}
}
