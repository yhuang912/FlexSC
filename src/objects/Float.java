package objects;

import gc.*;

public class Float {
	CompEnv<Signal> env;
	private FloatLib lib;
	private GCFloat content;

	public static class GCFloat{
		public Signal s;
		public Signal[] v;
		public Signal[] p;
		public Signal z;
		
		public GCFloat(Signal sign, Signal[] p, Signal[] v, Signal zero) {
			this.s = sign;
			this.p = p;
			this.v = v;
			this.z = zero;
		}
		
		public boolean compatiable(GCFloat b) {
			return (b.p.length == p.length)
				&& (b.v.length == v.length);
		}
		
		public GCFloat clone() {
			return new GCFloat(s, p, v, z);
		}
	}
	
	
	public Float(CompEnv<Signal> env, GCFloat content) throws Exception {
		this.env = env;
		lib = new FloatLib(env);
		this.content = content;
	}
	
	Float add(Float b) throws Exception {
		GCFloat result = lib.add(this.content, b.content);
		return new Float(env, result);
	}

	Float sub(Float b) throws Exception {
		GCFloat result = lib.sub(this.content, b.content);
		return new Float(env, result);
	}

	Float multiply(Float b) throws Exception {
		GCFloat result = lib.multiply(this.content, b.content);
		return new Float(env, result);
	}
	
	Float divide(Float b) throws Exception {
		GCFloat result = lib.divide(this.content, b.content);
		return new Float(env, result);
	}
}
