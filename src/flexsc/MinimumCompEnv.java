package flexsc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import test.Utils;

public class MinimumCompEnv extends CompEnv<Boolean> {
	
	public MinimumCompEnv(InputStream is, OutputStream os, Party p) {
		super(is, os, p, Mode.VERIFY);
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

	@Override
	public boolean outputToBob(Boolean out) throws Exception {
		return out;
		
	}

	@Override
	public boolean[] outputToBob(Boolean[] out) throws Exception {
		return Utils.tobooleanArray(out);
	}

}
