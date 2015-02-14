// Copyright (C) 2014 by Xiao Shaun Wang <wangxiao@cs.umd.edu>
package flexsc;

import network.Network;
import util.Utils;

public class CVCompEnv extends BooleanCompEnv {
	public CVCompEnv(Network w, Party p) {
		super(w, p, Mode.VERIFY);
		this.p = p;
	}

	public int numOfAnds = 0;
	@Override
	public Boolean inputOfAlice(boolean in) {
		Boolean res = null;
		
			res = in;
			if (p == Party.Alice)
				w.writeInt(in ? 1 : 0);
			else {
				int re = w.readInt();
				res = re == 1;
			}
			flush();
		
		return res;
	}

	@Override
	public Boolean inputOfBob(boolean in) {
		Boolean res = null;
//		try {
//			os.flush();
			res = in;
			if (p == Party.Bob)
				w.writeInt(in ? 1 : 0);
			else {
				int re = w.readInt();
				res = re == 1;
			}
//			os.flush();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		return res;
	}
	
	

	@Override
	public boolean outputToAlice(Boolean out) {
		return out;
	}

	public boolean outputToBob(Boolean out) {
		return out;
	}

	@Override
	public Boolean and(Boolean a, Boolean b) {
		numOfAnds ++;
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

	public Boolean[] inputOfAlice(boolean[] in) {
		Boolean[] res = new Boolean[in.length];
		for (int i = 0; i < res.length; ++i)
			res[i] = inputOfAlice(in[i]);
		return res;
	}

	@Override
	public Boolean[] inputOfBob(boolean[] in) {
		Boolean[] res = new Boolean[in.length];
		for (int i = 0; i < res.length; ++i)
			res[i] = inputOfBob(in[i]);
		return res;
	}

	@Override
	public boolean[] outputToAlice(Boolean[] out) {
		return Utils.tobooleanArray(out);
	}

	@Override
	public boolean[] outputToBob(Boolean[] out) {
		return Utils.tobooleanArray(out);
	}
}
