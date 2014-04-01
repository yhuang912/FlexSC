package objects;

import circuits.FixPointLib;
import flexsc.CompEnv;
import gc.*;

public class FixPoint {
	CompEnv<Signal> env;
	private Signal[] content;
	int offset;
	FixPointLib lib;
	
	public FixPoint(CompEnv<Signal> env, Signal[] s, int offset) throws Exception {
		this.env = env;
		lib = new FixPointLib(env);
		content = s;
		this.offset = offset;
	}
	
	FixPoint add(FixPoint b) throws Exception {
		Signal[] result = lib.add(this.content, b.content,offset);
		return new FixPoint(env, result,offset);
	}

	FixPoint sub(FixPoint b) throws Exception {
		Signal[] result = lib.sub(this.content, b.content,offset);
		return new FixPoint(env, result,offset);
	}

	FixPoint multiply(FixPoint b) throws Exception {
		Signal[] result = lib.multiply(this.content, b.content,offset);
		return new FixPoint(env, result,offset);
	}
	
	FixPoint divide(FixPoint b) throws Exception {
		Signal[] result = lib.divide(this.content, b.content,offset);
		return new FixPoint(env, result,offset);
	}
}
