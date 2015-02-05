// Copyright (C) 2014 by Xiao Shaun Wang <wangxiao@cs.umd.edu>
package oram;

import java.util.Map.Entry;
import java.util.TreeMap;

import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Party;

public class TrivialObliviousMap<T> {
	//Block<T>[] result;
	int capacity, dataSize, indexSize;
	CompEnv<T>  env;
	T[][] key; T[][] value;
	public IntegerLib<T> lib;
	
	public TrivialObliviousMap(CompEnv<T> env){
		this.env = env;
		lib = new IntegerLib<T>(env);
	}
	
	public void init(TreeMap<Long,boolean[]> m, int indexLength, int dataLength) {
		this.capacity = m.size();
		this.indexSize = indexLength;
		this.dataSize = dataLength;
		
		key = env.newTArray(capacity, 0);
		value = env.newTArray(capacity, 0);
		
		int i = 0;
		for(Entry<Long, boolean[]>e: m.entrySet()) {
			key[i] = inputOfP(e.getKey().intValue(), indexLength, env.getParty());
			value[i] = inputOfP(e.getValue(), env.getParty());
			i++;
		}
	}
	
	public void init(int cap, int indexLength, int dataLength) {
		this.capacity = cap;
		this.indexSize = indexLength;
		this.dataSize = dataLength;
		key = env.newTArray(capacity, 0);
		value = env.newTArray(capacity, 0);
		
		Party p = env.getParty() == Party.Alice ? Party.Bob: Party.Alice;
		for(int i = 0; i < cap; ++i) {
			key[i] = inputOfP(0, indexLength, p);
			value[i] = inputOfP(new boolean[dataLength], p);
		}
	}

	private T[] inputOfP(boolean[] t, Party p) {
		if( p == Party.Alice)
			return env.inputOfAlice(t);
		else 
			return env.inputOfBob(t);
	}
	
	private T[] inputOfP(int t, int width, Party p ) {
		if( p == Party.Alice)
			return env.inputOfAlice(Utils.fromInt(t, width));
		else 
			return env.inputOfBob(Utils.fromInt(t, width));
	}


	public T[] read(T[] scIden) {
		scIden = lib.padSignedSignal(scIden, indexSize);
		T[] res = lib.zeros(dataSize);
		for(int i = 0; i < capacity; ++i) {
			T match = lib.eq(scIden, key[i]);
			res = lib.mux(res, value[i], match);
		}
		return res;
	}
}

