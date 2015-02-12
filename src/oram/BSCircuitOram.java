// Copyright (C) 2014 by Xiao Shaun Wang <wangxiao@cs.umd.edu>
package oram;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

import oram.noOTORAM.CircuitOramNOOT;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Party;

public class BSCircuitOram<T> {
	public TrivialPrivateOram<T> baseOram;
	public ArrayList<CircuitORAMInterface<T>> clients = new ArrayList<>();
	public int lengthOfIden;
	int recurFactor;
	int cutoff;
	int capacity;

	SecureRandom rng = new SecureRandom();
	protected InputStream is;
	protected OutputStream os;
	Party p;
	CompEnv<T> env;
	IntegerLib<T> lib;
	public BSCircuitOram(CompEnv<T> env, int N, int dataSize, int indexsize, 
			int cutoff, int recurFactor, int capacity, int sp) {
		init(env, N, dataSize,indexsize, cutoff, recurFactor, capacity, sp);
	}

	// with default params
	public BSCircuitOram(CompEnv<T> env, int N, int dataSize, int indexsize) {
		init(env, N, dataSize,indexsize, 1<<6, 8, 3, 80);
	}

	void init(CompEnv<T> env, int N, int dataSize,int indexsize, int cutoff, int recurFactor,
			int capacity, int sp) {
		this.env = env;
		lib = new IntegerLib<T>(env);
		this.is = env.is;
		this.os = env.os;
		this.p = env.p;
		this.cutoff = cutoff;
		this.recurFactor = recurFactor;
		this.capacity = capacity;
		CircuitOram<T> oram = new CircuitOram<T>(indexsize, env, N, dataSize, capacity, sp);
		lengthOfIden = oram.lengthOfIden;
		clients.add(oram);
		int newDataSize =(indexsize+ oram.lengthOfPos) * recurFactor, newN = (1 << oram.lengthOfIden)
				/ recurFactor;
		while (newN > cutoff) {
			CircuitORAMInterface<T> o;
			if(newN < 1<< 20) {
				o = new CircuitOramNOOT<T>(indexsize, env, newN, newDataSize, capacity, sp);
				clients.add(o);
			}
			else {
				o = new CircuitOram<T>(indexsize, env, newN, newDataSize, capacity, sp);
				clients.add(o);
			}
			newDataSize = (indexsize + o.getLengthOfPos())* recurFactor;
			newN = (1 << o.getLengthOfIndex()) / recurFactor;
		}
		
		CircuitORAMInterface<T> last = clients.get(clients.size() - 1);
		baseOram = new TrivialPrivateOram<T>(indexsize, env, (1 << last.getLengthOfIndex()),
				last.getLengthOfPos());
	}

	public T[] read(T[] iden) {
		T[][] poses = travelToDeep(iden, 1);
		CircuitORAMInterface<T> currentOram = clients.get(0);
		boolean[] oldPos = baseOram.lib.declassifyToBoth(poses[0]);

		T[] res = currentOram.read(iden, oldPos, poses[1]);
		return res;
	}

	public void write(T[] iden, T[] data) {
		T[][] poses = travelToDeep(iden, 1);
		CircuitORAMInterface<T> currentOram = clients.get(0);

		boolean[] oldPos = baseOram.lib.declassifyToBoth(poses[0]);
		currentOram.write(iden, oldPos, poses[1], data);
	}

	public T[][] travelToDeep(T[] iden, int level) {
		if (level == clients.size()) {
			T[] baseMap = baseOram.readAndRemove(iden);

			T[] pos = extract(baseMap, iden,
					clients.get(level - 1).getLengthOfPos());

			T[] newPos = baseOram.lib
					.randBools(clients.get(level - 1).getLengthOfPos());
			put(baseMap, iden, newPos);
			baseOram.putBack(iden, baseMap);
			T[][] result = baseOram.env.newTArray(2, 0);
			result[0] = pos;
			result[1] = newPos;
			return result;
		} else {
			CircuitORAMInterface<T> currentOram = clients.get(level);

			T[][] poses = travelToDeep(iden, level + 1);

			boolean[] oldPos = baseOram.lib.declassifyToBoth(poses[0]);

			T[] data = currentOram.readAndRemove(iden, oldPos, true);

			T[] pos = extract(data, iden, clients.get(level - 1).getLengthOfPos());
			T[] tmpNewPos = baseOram.lib
					.randBools(clients.get(level - 1).getLengthOfPos());
			put(data, iden, tmpNewPos);
			currentOram.putBack(iden, poses[1],
					data);
			T[][] result = env.newTArray(2, 0);
			result[0] = pos;
			result[1] = tmpNewPos;
			return result;
		}
	}

	public T[] extract(T[] array, T[] iden, int length) {
		int total_length = (length+iden.length);
		int numberOfEntry = array.length / (length+iden.length);
		T[] result = Arrays.copyOfRange(array, 0, length);
		for (int i = 0; i < numberOfEntry; ++i) {
			T hit = lib.eq(Arrays.copyOfRange(array, i*total_length, i*total_length+iden.length), iden);
			result = lib.mux(result,
					Arrays.copyOfRange(array, i * total_length+iden.length, (i + 1) * total_length),
					hit);
		}
		return result;
	}

	public void put(T[] array, T[] iden, T[] content) {
		int total_length = (content.length+iden.length);
		int numberOfEntry = array.length / (content.length+iden.length);
		for (int i = 0; i < numberOfEntry; ++i) {
			T hit = lib.eq(Arrays.copyOfRange(array, i*total_length, i*total_length+iden.length), iden);
			T[] a = lib.mux(content, Arrays.copyOfRange(array, i * total_length+iden.length, (i + 1) * total_length),
					hit);
			System.arraycopy(a, 0, array, i*total_length+iden.length, a.length);
		}
	}

}
