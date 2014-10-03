// Copyright (C) 2014 by Xiao Shaun Wang <wangxiao@cs.umd.edu>
package flexsc;

import gc.BadLabelException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import test.Utils;

public class CVCompEnv extends BooleanCompEnv {	
	public CVCompEnv(InputStream is, OutputStream os, Party p) {
		super(is, os, p, Mode.VERIFY);
		this.party = p;
	}
	
	@Override
	public Boolean inputOfAlice(boolean in) throws IOException{
		os.flush();
		Boolean res = in;
		if(party == Party.Alice)
			os.write(in ? 1:0);
		else{
			int re = is.read();
			res = re == 1;
		}
		os.flush();
		return new Boolean(res);
	}

	@Override
	public Boolean inputOfBob(boolean in) throws IOException {
		os.flush();
		Boolean res = in;
		if(party == Party.Bob)
			os.write(in ? 1:0);
		else{
			int re = is.read();
			res = re == 1;
		}
		os.flush();
		return new Boolean(res);
	}

	@Override
	public boolean outputToAlice(Boolean out) throws IOException, BadLabelException {
		return out;
	}

	public boolean outputToBob(Boolean out) throws IOException, BadLabelException {
		return out;
	}

	
	@Override
	public Boolean and(Boolean a, Boolean b) {
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

	public Boolean[] inputOfAlice(boolean[] in) throws IOException {
		Boolean[] res = new Boolean[in.length];
		for(int i = 0; i < res.length; ++i)
			res[i] = inputOfAlice(in[i]);
		return res;
	}

	@Override
	public Boolean[] inputOfBob(boolean[] in) throws IOException {
		Boolean[] res = new Boolean[in.length];
		for(int i = 0; i < res.length; ++i)
			res[i] = inputOfBob(in[i]);
		return res;
	}

	@Override
	public boolean[] outputToAlice(Boolean[] out) throws IOException, BadLabelException {
		return Utils.tobooleanArray(out);
	}

	@Override
	public boolean[] outputToBob(Boolean[] out) throws IOException, BadLabelException {
		return Utils.tobooleanArray(out);
	}
	
	@Override
	public CompEnv<Boolean> getNewInstance(InputStream in, OutputStream os)
			throws Exception {
		return new CVCompEnv(in, os, getParty());
	}
}