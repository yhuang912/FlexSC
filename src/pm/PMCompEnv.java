package pm;

import java.io.InputStream;
import java.io.OutputStream;
import flexsc.*;
import circuits.FloatFormat;
import objects.Float.Representation;
import test.Utils;

/*
 * The computational environment for performance measurement. 
 */
public class PMCompEnv implements CompEnv<Boolean> {
	public static class Statistics {
		public int andGate = 0;
		public int xorGate = 0;
		public int notGate = 0;
		public int OTs = 0;
		public void flush(){
			andGate = 0;
			xorGate = 0;
			notGate = 0;
			OTs = 0;			
		}
		public void add(Statistics s2) {
			andGate += s2.andGate;
			xorGate += s2.xorGate;
			notGate += s2.notGate;
			OTs += s2.OTs;
		}
	}
	InputStream is;
	OutputStream os;
	Party p;
	public Statistics statistic;
	Boolean t = true;
	Boolean f = false;

	public PMCompEnv(InputStream is, OutputStream os, Party p) {
		this.p = p;
		t = true;
		f = false;
		statistic = new Statistics();
		this.is = is;
		this.os = os;
	}
	
	@Override
	public Boolean inputOfAlice(boolean in) throws Exception {
		++statistic.OTs;
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
		++statistic.andGate;
		return f;
	}

	@Override
	public Boolean xor(Boolean a, Boolean b) {
		++statistic.xorGate;
		return f;
	}

	@Override
	public Boolean not(Boolean a) {
		++statistic.notGate;
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