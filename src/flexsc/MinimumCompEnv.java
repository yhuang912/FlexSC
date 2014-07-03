package flexsc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import objects.Float.Representation;
import test.Utils;
import circuits.FloatFormat;
import flexsc.CompEnv;
import flexsc.Party;

public class MinimumCompEnv implements CompEnv<Boolean> {
	
	public MinimumCompEnv(InputStream is, OutputStream os, Party p) {
	}
	
	@Override
	public Boolean inputOfAlice(boolean in) throws Exception {
		return in;
	}

	@Override
	public Boolean inputOfBob(boolean in) throws Exception {
		return in;
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

	@Override
	public Boolean[][] newTArray(int d1, int d2) {
		return new Boolean[d1][d2];
	}
	
	@Override
	public Boolean[][][] newTArray(int d1, int d2, int d3) {
		return new Boolean[d1][d2][d3];
	}

	public Boolean[] inputOfAlice(boolean[] in) throws Exception {
		Boolean[] res = new Boolean[in.length];
		for(int i = 0; i < res.length; ++i)
			res[i] = inputOfAlice(in[i]);
		return res;
	}

	@Override
	public Boolean[] inputOfBob(boolean[] in) throws Exception {
		Boolean[] res = new Boolean[in.length];
		for(int i = 0; i < res.length; ++i)
			res[i] = inputOfBob(in[i]);
		return res;
	}

	@Override
	public Representation<Boolean> inputOfBobFloatPoint(double d, int widthV, int widthP)
			throws Exception {
		FloatFormat f = new FloatFormat(d, 23, 9);
		Representation<Boolean> result = 
				new Representation<Boolean>(f.s, Utils.toBooleanArray(f.p), Utils.toBooleanArray(f.v), f.z);
		return result;
	}

	@Override
	public Representation<Boolean> inputOfAliceFloatPoint(double d, int widthV, int widthP)
			throws Exception {
		FloatFormat f = new FloatFormat(d, 23, 9);
		Representation<Boolean> result = 
				new Representation<Boolean>(f.s, Utils.toBooleanArray(f.p), Utils.toBooleanArray(f.v), f.z);
		return result;
	}

	@Override
	public double outputToAliceFloatPoint(Representation<Boolean> f) throws Exception {
		FloatFormat d = new FloatFormat(Utils.tobooleanArray(f.v), Utils.tobooleanArray(f.p), f.s, f.z);
		return d.toDouble();
	}

	@Override
	public Boolean[] inputOfBobFixedPoint(double d, int width, int offset)
			throws Exception {
		return inputOfBob(Utils.fromFixPoint(d,width,offset));
	}

	@Override
	public Boolean[] inputOfAliceFixedPoint(double d, int width, int offset)
			throws Exception {
		return inputOfBob(Utils.fromFixPoint(d,width,offset));
	}

	@Override
	public double outputToAliceFixedPoint(Boolean[] f, int offset) throws Exception {
		boolean[] res = outputToAlice(f);
		return  Utils.toFixPoint(res, res.length, offset);
	}

	@Override
	public boolean[] outputToAlice(Boolean[] out) throws Exception {
		return Utils.tobooleanArray(out);
	}

	@Override
	public CompEnv<Boolean> getNewInstance(InputStream in, OutputStream os)
			throws Exception {
		return new MinimumCompEnv(in, os, getParty());
	}

	@Override
	public Party getParty() {
		return null;
	}
	@Override
	public void flush() throws IOException {		
	}

}
