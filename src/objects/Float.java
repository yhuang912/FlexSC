package objects;

import circuits.FloatLib;
import flexsc.CompEnv;
import gc.*;

public class Float {
	CompEnv<Signal> env;
	private FloatLib lib;
	private Represention content;

	public static class Represention{
		public Signal s;
		public Signal[] v;
		public Signal[] p;
		public Signal z;
		
		public Represention(Signal sign, Signal[] p, Signal[] v, Signal zero) {
			this.s = sign;
			this.p = p;
			this.v = v;
			this.z = zero;
		}
		
		public boolean compatiable(Represention b) {
			return (b.p.length == p.length)
				&& (b.v.length == v.length);
		}
		
		public Represention clone() {
			return new Represention(s, p, v, z);
		}
	}
	
	
	public Float(CompEnv<Signal> env, Represention content) throws Exception {
		this.env = env;
		lib = new FloatLib(env);
		this.content = content;
	}
	
	Float add(Float b) throws Exception {
		Represention result = lib.add(this.content, b.content);
		return new Float(env, result);
	}

	Float sub(Float b) throws Exception {
		Represention result = lib.sub(this.content, b.content);
		return new Float(env, result);
	}

	Float multiply(Float b) throws Exception {
		Represention result = lib.multiply(this.content, b.content);
		return new Float(env, result);
	}
	
	Float divide(Float b) throws Exception {
		Represention result = lib.divide(this.content, b.content);
		return new Float(env, result);
	}
}
