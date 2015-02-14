// Copyright (C) 2014 by Xiao Shaun Wang <wangxiao@cs.umd.edu>
package flexsc;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import network.Network;
import rand.ISAACProvider;
import util.Utils;

public abstract class CompEnv<T> {

	public static SecureRandom rnd;
	static{
		Security.addProvider(new ISAACProvider());
		try {
			rnd = SecureRandom.getInstance("ISAACRandom");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	public  Network w;
	@SuppressWarnings("rawtypes")
	public static CompEnv getEnv(Mode m, Party p, Network w) {
		Flag.mode = m;
		return getEnv(p, w);
	}
	
	@SuppressWarnings("rawtypes")
	public static CompEnv getEnv(Party p, Network w) {
		if (Flag.mode == Mode.REAL)
			if (p == Party.Bob)
				return new gc.regular.GCEva(w);
			else
				return new gc.regular.GCGen(w);		
		else if (Flag.mode == Mode.OPT)
			if (p == Party.Bob)
				return new gc.halfANDs.GCEva(w);
			else
				return new gc.halfANDs.GCGen(w);
		else if (Flag.mode == Mode.OFFLINE)
			if (p == Party.Bob)
				return new gc.offline.GCEva(w);
			else
				return new gc.offline.GCGen(w);
//		else if (Flag.mode == Mode.VERIFY)
//			return new CVCompEnv(w, p);
//		else if (Flag.mode == Mode.COUNT)
//			return new PMCompEnv(w, p);
		else {
			try {
				throw new Exception("not a supported Mode!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	public Party p;
	public Mode m;

	public CompEnv(Network w, Party p, Mode m) {
		this.w = w;
		this.m = m;
		this.p = p;
	}

	public T[][] inputOfAlice(boolean[][] in) {
		boolean[] flattened = Utils.flatten(in);
		T[] res = inputOfAlice(flattened);
		T[][] unflattened = newTArray(in.length, in[0].length);
		Utils.unflatten(res, unflattened);
		return unflattened;
	}

	public T[][] inputOfBob(boolean[][] in) {
		boolean[] flattened = Utils.flatten(in);
		T[] res = inputOfBob(flattened);
		T[][] unflattened = newTArray(in.length, in[0].length);
		Utils.unflatten(res, unflattened);
		return unflattened;
	}

	public T[][][] inputOfAlice(boolean[][][] in) {
		boolean[] flattened = Utils.flatten(in);
		T[] res = inputOfAlice(flattened);
		T[][][] unflattened = newTArray(in.length, in[0].length, in[0][0].length);
		Utils.unflatten(res, unflattened);
		return unflattened;
	}

	public T[][][] inputOfBob(boolean[][][] in) {
		boolean[] flattened = Utils.flatten(in);
		T[] res = inputOfBob(flattened);
		T[][][] unflattened = newTArray(in.length, in[0].length, in[0][0].length);
		Utils.unflatten(res, unflattened);
		return unflattened;
	}


	public abstract T inputOfAlice(boolean in);

	public abstract T inputOfBob(boolean in);

	public abstract boolean outputToAlice(T out);

	public abstract boolean outputToBob(T out);

	public abstract T[] inputOfAlice(boolean[] in);

	public abstract T[] inputOfBob(boolean[] in);

	public abstract boolean[] outputToAlice(T[] out);

	public abstract boolean[] outputToBob(T[] out);

	public abstract T and(T a, T b);

	public abstract T xor(T a, T b);

	public abstract T not(T a);

	public abstract T ONE();

	public abstract T ZERO();

	public abstract T[] newTArray(int len);

	public abstract T[][] newTArray(int d1, int d2);

	public abstract T[][][] newTArray(int d1, int d2, int d3);

	public abstract T newT(boolean v);

	public Party getParty() {
		return p;
	}

	public void flush() {
//		try {
//			os.flush();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

//	public void sync() throws IOException {
//		if (getParty() == Party.Alice) {
//			is.read();
//			os.write(0);
//			os.flush(); // dummy I/O to prevent dropping connection earlier than
//			// protocol payloads are received.
//		} else {
//			os.write(0);
//			os.flush();
//			is.read(); // dummy write to prevent dropping connection earlier
//			// than
//			// protocol payloads are received.
//		}
//	}
}