// Copyright (C) 2014 by Xiao Shaun Wang <wangxiao@cs.umd.edu>
package oram;

import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Party;

public class TrivialObliviousMap<T> {
	Block<T>[] result;
	int capacity, dataSize, indexSize;
	CompEnv<T>  env;
	T[][] key; T[][] value;
	public IntegerLib<T> lib;
	
	public TrivialObliviousMap(CompEnv<T> env, int N, int indexLength, int dataLength) {
		this.env = env;
		this.capacity = N;
		this.indexSize = indexLength;
		this.dataSize = dataLength;
		lib = new IntegerLib<T>(env);
	}

	public void initialize(T[][] key, T[][] value) {
		this.key = key;
		this.value = value;
	}

	private T[] inputOfP(int t, Party p, int width) {
		if( p == Party.Alice)
			return env.inputOfAlice(Utils.fromInt(t, width));
		else 
			return env.inputOfBob(Utils.fromInt(t, width));
	}

	public void initialize(int[] _key, int[] _value, Party p) {
		this.key = env.newTArray(_key.length, 0);
		this.value = env.newTArray(_key.length, 0);
		for(int i = 0; i < _key.length; ++i) {
			key[i] = inputOfP(_key[i], p, indexSize);
			value[i] = inputOfP(_value[i], p, dataSize);
		}
	}


	public T[] read(T[] scIden) {
		T[] res = lib.toSignals(0, dataSize);
		for(int i = 0; i < key.length; ++i) {
			T match = lib.eq(scIden, key[i]);
			res = lib.mux(res, value[i], match);
		}
		return res;
	}
}

