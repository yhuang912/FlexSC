package cv;

import circuits.FloatFormat;
import objects.Float.Representation;
import flexsc.CompEnv;

public class CVCompEnv implements CompEnv<Boolean> {

	@Override
	public Boolean inputOfGen(boolean in) throws Exception {
		throw new Exception("No need to prepare inputs.");
	}

	@Override
	public Boolean inputOfEva(boolean in) throws Exception {
		throw new Exception("No need to prepare inputs.");
	}

	@Override
	public boolean outputToGen(Boolean out) throws Exception {
		throw new Exception("No need to prepare outputs.");
	}

	@Override
	public Boolean and(Boolean a, Boolean b) throws Exception {
		return a && b;
	}

	@Override
	public Boolean xor(Boolean a, Boolean b) {
		return a ^ b;
	}

	@Override
	public Boolean not(Boolean a) {
		return !a;
	}

	@Override
	public Boolean ONE() {
		return true;
	}

	@Override
	public Boolean ZERO() {
		return false;
	}

	@Override
	public Boolean[] newTArray(int len) {
		Boolean[] res = new Boolean[len];
		return res;
	}

	@Override
	public Boolean newT(boolean v) {
		return v;
	}
	
	public Representation<Boolean> fromDouble(double d, int widthV, int widthP) {
		FloatFormat f = new FloatFormat(d, 23, 9);
		Boolean[] v = new Boolean[f.v.length];
		Boolean[] p = new Boolean[f.p.length];
		for(int i = 0; i < v.length; ++i)
			v[i] = f.v[i];
		for(int i = 0; i < p.length; ++i)
			p[i] = f.p[i];
		Representation<Boolean> result = new Representation<Boolean>(f.s, p, v, f.z);
		return result;
	}
	
	public double toDouble(Representation<Boolean> f) {
		boolean[] v = new boolean[f.v.length];
		boolean[] p = new boolean[f.p.length];
		for(int i = 0; i < v.length; ++i)
			v[i] = f.v[i];
		for(int i = 0; i < p.length; ++i)
			p[i] = f.p[i];
		FloatFormat d = new FloatFormat(v, p, f.s, f.z);
		return d.toDouble();
	}

	@Override
	public Boolean[][] newTArray(int d1, int d2) {
		return new Boolean[d1][d2];
	}
	
	@Override
	public Boolean[][][] newTArray(int d1, int d2, int d3) {
		return new Boolean[d1][d2][d3];
	}
}
