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

public class RecursiveOptCircuitOram<T> {
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
IntegerLib<T> lib ;
	public RecursiveOptCircuitOram(CompEnv<T> env, int N, int dataSize,
			int cutoff, int recurFactor, int capacity, int sp) {
		init(env, N, dataSize, cutoff, recurFactor, capacity, sp);
	}

	// with default params
	public RecursiveOptCircuitOram(CompEnv<T> env, int N, int dataSize) {
		init(env, N, dataSize, 1<<6, 8, 3, 80);
	}

	void init(CompEnv<T> env, int N, int dataSize, int cutoff, int recurFactor,
			int capacity, int sp) {
		this.env = env;
		lib = new IntegerLib<T>(env);
		this.is = env.is;
		this.os = env.os;
		this.p = env.p;
		this.cutoff = cutoff;
		this.recurFactor = recurFactor;
		this.capacity = capacity;
		CircuitOram<T> oram = new CircuitOram<T>(env, N, dataSize, capacity, sp);
		lengthOfIden = oram.lengthOfIden;
		clients.add(oram);
		int newDataSize = oram.lengthOfPos * recurFactor, newN = (1 << oram.lengthOfIden)
				/ recurFactor;
		while (newN > cutoff) {
			CircuitORAMInterface<T> o;
			if(newN < 1<< 20) {
				o = new CircuitOramNOOT<T>(env, newN, newDataSize, capacity, sp);
				clients.add(o);
			}
			else {
				o = new CircuitOram<T>(env, newN, newDataSize, capacity, sp);
				clients.add(o);
			}
			newDataSize = o.getLengthOfPos()* recurFactor;
			newN = (1 << o.getLengthOfIndex()) / recurFactor;
		}
		
		CircuitORAMInterface<T> last = clients.get(clients.size() - 1);
		baseOram = new TrivialPrivateOram<T>(env, (1 << last.getLengthOfIndex()),
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
			T[] baseMap = baseOram.readAndRemove(lib.padSignal(iden, baseOram.lengthOfIden));
			T[] ithPos = baseOram.lib.rightPublicShift(iden,
					baseOram.lengthOfIden);// iden>>baseOram.lengthOfIden;

			T[] pos = extract(baseMap, ithPos,
					clients.get(level - 1).getLengthOfPos());

			T[] newPos = baseOram.lib
					.randBools(clients.get(level - 1).getLengthOfPos());
			put(baseMap, ithPos, newPos);
			baseOram.putBack(lib.padSignal(iden, baseOram.lengthOfIden), baseMap);
			T[][] result = baseOram.env.newTArray(2, 0);
			result[0] = pos;
			result[1] = newPos;
			return result;
		} else {
			CircuitORAMInterface<T> currentOram = clients.get(level);

			T[][] poses = travelToDeep(subIdentifier(iden, currentOram),
					level + 1);

			boolean[] oldPos = baseOram.lib.declassifyToBoth(poses[0]);

			T[] data = currentOram.readAndRemove(
					subIdentifier(iden, currentOram), oldPos, true);
			T[] ithPos = lib.rightPublicShift(iden,
					currentOram.getLengthOfIndex());// iden>>currentOram.lengthOfIden;//iden/(1<<currentOram.lengthOfIden);

			T[] pos = extract(data, ithPos, clients.get(level - 1).getLengthOfPos());
			T[] tmpNewPos = baseOram.lib
					.randBools(clients.get(level - 1).getLengthOfPos());
			put(data, ithPos, tmpNewPos);
			currentOram.putBack(subIdentifier(iden, currentOram), poses[1],
					data);
			T[][] result = env.newTArray(2, 0);
			result[0] = pos;
			result[1] = tmpNewPos;
			return result;
		}
	}

	public T[] subIdentifier(T[] iden, CircuitORAMInterface<T> o) {
		// int a = iden & ((1<<o.lengthOfIden)-1);//(iden % (1<<o.lengthOfIden))
		return lib.padSignal(iden, o.getLengthOfIndex());
	}

	public T[] extract(T[] array, T[] ithPos, int length) {
		int numberOfEntry = array.length / length;
		T[] result = Arrays.copyOfRange(array, 0, length);
		for (int i = 1; i < numberOfEntry; ++i) {
			T hit = baseOram.lib.eq(baseOram.lib.toSignals(i, ithPos.length),
					ithPos);
			result = baseOram.lib.mux(result,
					Arrays.copyOfRange(array, i * length, (i + 1) * length),
					hit);
		}
		return result;
	}

	public void put(T[] array, T[] ithPos, T[] content) {
		int numberOfEntry = array.length / content.length;
		for (int i = 0; i < numberOfEntry; ++i) {
			T hit = baseOram.lib.eq(baseOram.lib.toSignals(i, ithPos.length),
					ithPos);
			T[] tmp = baseOram.lib.mux(
					Arrays.copyOfRange(array, i * content.length, (i + 1)
							* content.length), content, hit);
			System.arraycopy(tmp, 0, array, i * content.length, content.length);
		}
	}

}
