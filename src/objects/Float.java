package objects;

import circuits.FloatLib;
import flexsc.CompEnv;
import flexsc.Signal;

public class Float<T extends Signal> {
	CompEnv<T> env;
	private FloatLib<T> lib;
	private Representation<T> content;

	public static class Representation<T> {
		public T s;
		public T[] v;
		public T[] p;
		public T z;
		
		public Representation(T sign, T[] p, T[] v, T zero) {
			this.s = sign;
			this.p = p;
			this.v = v;
			this.z = zero;
		}
		
		public boolean compatiable(Representation<T> b) {
			return (b.p.length == p.length)
				&& (b.v.length == v.length);
		}
		
		public Representation<T> clone() {
			return new Representation<T>(s, p, v, z);
		}
	}
	
	
	public Float(CompEnv<T> env, Representation<T> content) {
		this.env = env;
		lib = new FloatLib<T>(env);
		this.content = content;
	}
	
	Float<T> add(Float<T> b) {
		Representation<T> result = lib.add(this.content, b.content);
		return new Float<T>(env, result);
	}

	Float<T> sub(Float<T> b) {
		Representation<T> result = lib.sub(this.content, b.content);
		return new Float<T>(env, result);
	}

	Float<T> multiply(Float<T> b) {
		Representation<T> result = lib.multiply(this.content, b.content);
		return new Float<T>(env, result);
	}
	
	Float<T> divide(Float<T> b) {
		Representation<T> result = lib.divide(this.content, b.content);
		return new Float<T>(env, result);
	}
}
