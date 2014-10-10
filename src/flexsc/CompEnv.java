// Copyright (C) 2014 by Xiao Shaun Wang <wangxiao@cs.umd.edu>
package flexsc;

import gc.BadLabelException;
import gc.GCEva;
import gc.GCGen;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import ot.IncorrectOtUsageException;
import rand.ISAACProvider;

public abstract class CompEnv<T> {
	
	public SecureRandom rnd;
	
	@SuppressWarnings("rawtypes")
	public static CompEnv getEnv(Mode mode, Party p, InputStream is, OutputStream os) throws IOException, ClassNotFoundException {
		if(mode == Mode.REAL)
			if(p == Party.Bob)
				return new GCEva(is, os);
			else
				return new GCGen(is, os);
		else if(mode == Mode.VERIFY)
			return new CVCompEnv(is,os, p);
		else if(mode == Mode.COUNT)
			return new PMCompEnv(is,os, p);
		else return null;
	}
	
	public InputStream is;
	public OutputStream os;
	public Party party;
	public Mode mode;
	public CompEnv(InputStream is, OutputStream os, Party p, Mode m) {
		this.is = is;
		this.os = os;
		this.mode = m;
		this.party = p;
		Security.addProvider(new ISAACProvider ());
		try {
			rnd = SecureRandom.getInstance ("ISAACRandom");

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public abstract T inputOfAlice(boolean in) throws IOException;
	public abstract T inputOfBob(boolean in) throws IOException, IncorrectOtUsageException;
	public abstract boolean outputToAlice(T out) throws IOException, BadLabelException;
	public abstract boolean outputToBob(T out) throws IOException, BadLabelException;
	
	public abstract T[] inputOfAlice(boolean[] in) throws IOException;
	public abstract T[] inputOfBob(boolean[] in) throws IOException;
	public abstract boolean[] outputToAlice(T[] out) throws IOException, BadLabelException;
	public abstract boolean[] outputToBob(T[] out) throws IOException, BadLabelException;
	
	public abstract T and(T a, T b);
	public abstract T xor(T a, T b);
	public abstract T not(T a);
	
	public abstract T ONE();
	public abstract T ZERO();
	
	public abstract T[] newTArray(int len);
	public abstract T[][] newTArray(int d1, int d2);
	public abstract T[][][] newTArray(int d1, int d2, int d3);
	public abstract T newT(boolean v);
	
	abstract public CompEnv<T> getNewInstance(InputStream in, OutputStream os) throws Exception;
	
	public void flush() throws IOException {
		os.flush();
	}

	public void sync() throws IOException {
		if(getParty() == Party.Alice){
			is.read(); 
			os.write(0);
			os.flush(); // dummy I/O to prevent dropping connection earlier than
						// protocol payloads are received.
		}
		else {
			os.write(0);
			os.flush();
			is.read(); // dummy write to prevent dropping connection earlier than
			// protocol payloads are received.
		}	
	}

	public Mode getMode() {
		return mode;
	}

	public Party getParty() {
		return party;
	}
}