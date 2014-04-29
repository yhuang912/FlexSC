package cv;

import java.io.InputStream;
import java.io.OutputStream;
import flexsc.*;
import circuits.FloatFormat;
import objects.Float.Representation;
import test.Utils;

public class MeasureCompEnv implements CompEnv<Boolean> {
	InputStream is;
	OutputStream os;
	Party p;
	
	int andGate = 0;
	int xorGate = 0;
	int notGate = 0;
	int OTs = 0;
	public MeasureCompEnv(InputStream is, OutputStream os, Party p) {
		this.p = p;
		this.is = is;
		this.os = os;
	}
	
	Boolean t = true;
	Boolean f = false;
	@Override
	public Boolean inputOfAlice(boolean in) throws Exception {
		OTs = 0;
		return f;
	}

	@Override
	public Boolean inputOfBob(boolean in) throws Exception {
		return f;
	}

	@Override
	public boolean outputToAlice(Boolean out) throws Exception {
		return false;
	}

	@Override
	public Boolean and(Boolean a, Boolean b) throws Exception {
		++andGate;
		return f;
	}

	@Override
	public Boolean xor(Boolean a, Boolean b) {
		++xorGate;
		return f;
	}

	@Override
	public Boolean not(Boolean a) {
		++notGate;
		return f;
	}

	@Override
	public Boolean ONE() {
		return t;
	}

	@Override
	public Boolean ZERO() {
		return f;
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
}