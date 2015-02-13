// Copyright (C) 2014 by Xiao Shaun Wang <wangxiao@cs.umd.edu>
package oram.noOTORAM;

import java.util.Arrays;

import flexsc.CompEnv;

public class SecureArray<T> {
	int threshold = 256;
	boolean useTrivialOram = false;
	public TrivialPrivateOram<T> trivialOram = null;
	public RecursiveCircuitOram<T> circuitOram = null;
	public int lengthOfIden;
	CompEnv<T> env;


	public SecureArray(CompEnv<T> env, int N, int dataSize, int threshold, int cutoff, int recurFactor) throws Exception {
		this.threshold = threshold;
		useTrivialOram = N <= threshold;
		if (useTrivialOram) {
			trivialOram = new TrivialPrivateOram<T>(env, N, dataSize);
			lengthOfIden = trivialOram.lengthOfIden;
		} else {
			circuitOram = new RecursiveCircuitOram<T>(env, N, dataSize, cutoff, recurFactor);
			lengthOfIden = circuitOram.lengthOfIden;
		}
		this.env = env;
	}


	public SecureArray(CompEnv<T> env, int N, int dataSize, int threshold) throws Exception {
		this.threshold = threshold;
		useTrivialOram = N <= threshold;
		if (useTrivialOram) {
			trivialOram = new TrivialPrivateOram<T>(env, N, dataSize);
			lengthOfIden = trivialOram.lengthOfIden;
		} else {
			circuitOram = new RecursiveCircuitOram<T>(env, N, dataSize);
			lengthOfIden = circuitOram.lengthOfIden;
		}
		this.env = env;
	}

	public SecureArray(CompEnv<T> env, int N, int dataSize) throws Exception {
		useTrivialOram = N <= threshold;
		if (useTrivialOram) {
			trivialOram = new TrivialPrivateOram<T>(env, N, dataSize);
			lengthOfIden = trivialOram.lengthOfIden;
		} else {
			circuitOram = new RecursiveCircuitOram<T>(env, N, dataSize);
			lengthOfIden = circuitOram.lengthOfIden;
		}
		this.env = env;
	}

	public T[] readAndRemove(T[] iden) {
		return circuitOram.clients.get(0).readAndRemove(iden, 
				Arrays.copyOfRange(circuitOram.clients.get(0).lib.declassifyToBoth(iden), 0, circuitOram.clients.get(0).lengthOfPos), false);
	}

	
	public T[] read(T[] iden) {
		iden = Arrays.copyOf(iden, lengthOfIden);

		T[] res = null;
		if (useTrivialOram)
			res= trivialOram.read(iden);
		else
			res= circuitOram.read(iden);
		env.flush();
		return res;
	}

	public void write(T[] iden, T[] data) throws Exception {
		iden = Arrays.copyOf(iden, lengthOfIden);

		if (useTrivialOram)
			trivialOram.write(iden, data);
		else
			circuitOram.write(iden, data);
		env.flush();
	}

	public void conditionalWrite(T[] iden, T[]data, T condition) {
		if(useTrivialOram) {
			T[] readData = trivialOram.readAndRemove(iden);
			T[] toAdd = trivialOram.lib.mux(readData, data, condition);
			trivialOram.putBack(iden, toAdd);
		}
		else {
			//op == 1 means write, 0 means read
			circuitOram.access(iden, data, condition);
		}
	}
}