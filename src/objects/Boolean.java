package objects;

import circuits.CircuitLib;
import flexsc.CompEnv;
import gc.*;

public class Boolean {
	CompEnv<Signal> env;
	private Signal content;
	private CircuitLib lib;
	
	public Boolean(CompEnv<Signal> env, Signal s) throws Exception {
		this.env = env;
		lib = new CircuitLib(env);
		content = s;
	}
	
	Boolean and(Boolean b) throws Exception {
		Signal result = lib.and(this.content, b.content);
		return new Boolean(env, result);
	}
}
