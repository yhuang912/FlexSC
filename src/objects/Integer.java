package objects;

import circuits.IntegerLib;
import flexsc.CompEnv;
import gc.*;

public class Integer {
	CompEnv<Signal> env;
	private Signal[] content;
	IntegerLib lib;
	
	public Integer(CompEnv<Signal> env, Signal[] s) throws Exception {
		this.env = env;
		lib = new IntegerLib(env);
		content = s;
	}
	
	Integer add(Integer b) throws Exception {
		Signal[] result = lib.add(this.content, b.content);
		return new Integer(env, result);
	}

	Integer sub(Integer b) throws Exception {
		Signal[] result = lib.sub(this.content, b.content);
		return new Integer(env, result);
	}

	Integer multiply(Integer b) throws Exception {
		Signal[] result = lib.multiply(this.content, b.content);
		return new Integer(env, result);
	}
	
	Integer divide(Integer b) throws Exception {
		Signal[] result = lib.divide(this.content, b.content);
		return new Integer(env, result);
	}
}
