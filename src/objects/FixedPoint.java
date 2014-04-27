package objects;

import circuits.FixedPointLib;
import flexsc.CompEnv;
import flexsc.Signal;

public class FixedPoint<T extends Signal> {
	CompEnv<T> env;
	private T[] content;
	int offset;
	FixedPointLib<T> lib;
	
	public FixedPoint(CompEnv<T> env, T[] s, int offset) throws Exception {
		this.env = env;
		lib = new FixedPointLib<T>(env);
		content = s;
		this.offset = offset;
	}
	
	FixedPoint<T> add(FixedPoint<T> b) throws Exception {
		T[] result = lib.add(this.content, b.content,offset);
		return new FixedPoint<T>(env, result,offset);
	}

	FixedPoint<T> sub(FixedPoint<T> b) throws Exception {
		T[] result = lib.sub(this.content, b.content, offset);
		return new FixedPoint<T>(env, result,offset);
	}

	FixedPoint<T> multiply(FixedPoint<T> b) throws Exception {
		T[] result = lib.multiply(this.content, b.content,offset);
		return new FixedPoint<T>(env, result,offset);
	}
	
	FixedPoint<T> divide(FixedPoint<T> b) throws Exception {
		T[] result = lib.divide(this.content, b.content,offset);
		return new FixedPoint<T>(env, result,offset);
	}
}
