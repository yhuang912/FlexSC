package objects;

import circuits.FixedPointLib;
import flexsc.CompEnv;
import gc.*;

public class FixedPoint {
	CompEnv<Signal> env;
	private Signal[] content;
	int offset;
	FixedPointLib lib;
	
	public FixedPoint(CompEnv<Signal> env, Signal[] s, int offset) throws Exception {
		this.env = env;
		lib = new FixedPointLib(env);
		content = s;
		this.offset = offset;
	}
	
	FixedPoint add(FixedPoint b) throws Exception {
		Signal[] result = lib.add(this.content, b.content,offset);
		return new FixedPoint(env, result,offset);
	}

	FixedPoint sub(FixedPoint b) throws Exception {
		Signal[] result = lib.sub(this.content, b.content,offset);
		return new FixedPoint(env, result,offset);
	}

	FixedPoint multiply(FixedPoint b) throws Exception {
		Signal[] result = lib.multiply(this.content, b.content,offset);
		return new FixedPoint(env, result,offset);
	}
	
	FixedPoint divide(FixedPoint b) throws Exception {
		Signal[] result = lib.divide(this.content, b.content,offset);
		return new FixedPoint(env, result,offset);
	}
}
