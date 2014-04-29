package cv;

import circuits.FloatFormat;
import objects.Float.Representation;
import test.Utils;
import flexsc.CompEnv;

public class CVCompEnv implements CompEnv<Boolean> {

	@Override
	public Boolean inputOfAlice(boolean in) throws Exception {
		return new Boolean(in);
	}

	@Override
	public Boolean inputOfBob(boolean in) throws Exception {
		return new Boolean(in);
	}

	@Override
	public boolean outputToAlice(Boolean out) throws Exception {
		return out;
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
		Representation<Boolean> result = 
				new Representation<Boolean>(f.s, Utils.toBooleanArray(f.p), Utils.toBooleanArray(f.v), f.z);
		return result;
	}
	
	public double toDouble(Representation<Boolean> f) {
		FloatFormat d = new FloatFormat(Utils.tobooleanArray(f.v), Utils.tobooleanArray(f.p), f.s, f.z);
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

	@Override
	public boolean[] outputToAlice(Boolean[] out) throws Exception {
		return Utils.tobooleanArray(out);
	}

	@Override
	public Boolean[] inputOfAlice(boolean[] in) throws Exception {
		return Utils.toBooleanArray(in);
	}

	@Override
	public Boolean[] inputOfBob(boolean[] in) throws Exception {
		return Utils.toBooleanArray(in);
	}
}
