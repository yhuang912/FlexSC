package cv;

import java.io.InputStream;
import java.io.OutputStream;

import flexsc.Party;
import circuits.FloatFormat;
import objects.Float.Representation;
import test.Utils;
import flexsc.CompEnv;

public class CVCompEnv implements CompEnv<Boolean> {
	InputStream is;
	OutputStream os;
	Party p;
	public CVCompEnv(InputStream is, OutputStream os, Party p) {
		this.p = p;
		this.is = is;
		this.os = os;
	}
	
	@Override
	public Boolean inputOfGen(boolean in) throws Exception {
		Boolean res = in;
		if(p == Party.Alice)
			os.write(in ? 1:0);
		else{
			int re = is.read();
			res = re == 1;
		}
		return new Boolean(res);
	}

	@Override
	public Boolean inputOfEva(boolean in) throws Exception {
		Boolean res = in;
		if(p == Party.Bob)
			os.write(in ? 1:0);
		else{
			int re = is.read();
			res = re == 1;
		}
		return new Boolean(res);
	}

	@Override
	public boolean outputToGen(Boolean out) throws Exception {
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

	@Override
	public boolean[] outputToGen(Boolean[] out) throws Exception {
		return Utils.tobooleanArray(out);
	}

	@Override
	public Boolean[] inputOfGen(boolean[] in) throws Exception {
		Boolean[] res = new Boolean[in.length];
		for(int i = 0; i < res.length; ++i)
			res[i] = inputOfGen(in[i]);
		return res;
	}

	@Override
	public Boolean[] inputOfEva(boolean[] in) throws Exception {
		Boolean[] res = new Boolean[in.length];
		for(int i = 0; i < res.length; ++i)
			res[i] = inputOfEva(in[i]);
		return res;
	}

	@Override
	public Representation<Boolean> inputOfEva(double d, int widthV, int widthP)
			throws Exception {
		FloatFormat f = new FloatFormat(d, 23, 9);
		Representation<Boolean> result = 
				new Representation<Boolean>(f.s, Utils.toBooleanArray(f.p), Utils.toBooleanArray(f.v), f.z);
		return result;
	}

	@Override
	public Representation<Boolean> inputOfGen(double d, int widthV, int widthP)
			throws Exception {
		FloatFormat f = new FloatFormat(d, 23, 9);
		Representation<Boolean> result = 
				new Representation<Boolean>(f.s, Utils.toBooleanArray(f.p), Utils.toBooleanArray(f.v), f.z);
		return result;
	}

	@Override
	public double outputToGen(Representation<Boolean> f) throws Exception {
		FloatFormat d = new FloatFormat(Utils.tobooleanArray(f.v), Utils.tobooleanArray(f.p), f.s, f.z);
		return d.toDouble();
	}

	@Override
	public Boolean[] inputOfEvaFixPoint(double d, int width, int offset)
			throws Exception {
		return inputOfEva(Utils.fromFixPoint(d,width,offset));
	}

	@Override
	public Boolean[] inputOfGenFixPoint(double d, int width, int offset)
			throws Exception {
		return inputOfEva(Utils.fromFixPoint(d,width,offset));
	}

	@Override
	public double outputToGen(Boolean[] f, int offset) throws Exception {
		boolean[] res = outputToGen(f);
		return  Utils.toFixPoint(res, res.length, offset);
	}
}
