package objects;

import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.Signal;

public class Integer<T extends Signal> {
	CompEnv<T> env;
	private T[] content;
	IntegerLib<T> lib;
	
	public Integer(CompEnv<T> env, T[] s) {
		this.env = env;
		lib = new IntegerLib<T>(env);
		content = s;
	}
	
	Integer<T> add(Integer<T> b) {
		T[] result = lib.add(this.content, b.content);
		return new Integer<T>(env, result);
	}

	Integer<T> sub(Integer<T> b) {
		T[] result = lib.sub(this.content, b.content);
		return new Integer<T>(env, result);
	}

	Integer<T> multiply(Integer<T> b) {
		T[] result = lib.multiply(this.content, b.content);
		return new Integer<T>(env, result);
	}
	
	Integer<T> divide(Integer<T> b) {
		T[] result = lib.divide(this.content, b.content);
		return new Integer<T>(env, result);
	}
}
