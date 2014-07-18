package ods.ostack;

import java.io.IOException;
import java.util.Arrays;

import ods.ObliviousDataStructure;
import oram.BucketLib;
import test.Utils;
import PrivateOramPreview.CircuitOram;
import flexsc.CompEnv;

public class OStack<T> extends ObliviousDataStructure<T> {
	CircuitOram<T> oram;
	final int capacity = 3;
	BucketLib<T> lib;
	T[] top;
	T[] counter;
	public OStack(CompEnv<T> env, int N, int dataSize,
			int sp) throws Exception {
		super(env);
		oram = new CircuitOram<T>(env, N, dataSize,capacity, sp);
		oram = new CircuitOram<T>(env, N, dataSize+oram.lengthOfPos, capacity, sp);
		lib = oram.lib;
		top = lib.randBools(rnd, oram.lengthOfPos);
		counter = env.inputOfAlice(Utils.fromInt(1, oram.lengthOfIden));
	}

	public OStack(CompEnv<T> env, int N, int dataSize) throws Exception {
		super(env);
		oram = new CircuitOram<T>(env, N, dataSize, capacity, sp);
		oram = new CircuitOram<T>(env, N, dataSize+oram.lengthOfPos, capacity, sp);
		lib = oram.lib;
		top = lib.randBools(rnd, oram.lengthOfPos);
		counter = env.inputOfAlice(Utils.fromInt(1, oram.lengthOfIden));
	}
	
	public T[] access(T op, T[] data) throws Exception {
		T[] newIter = lib.randBools(rnd, oram.lengthOfPos);
		boolean[] pos = lib.syncBooleans(lib.getBooleans(top));
		T[] block = oram.conditionalReadAndRemove(counter, pos, op);
		T[] newBlock = oram.env.newTArray(data.length+oram.lengthOfPos);
	    System.arraycopy(top, 0, newBlock, 0, top.length);
	    System.arraycopy(data, 0, newBlock, top.length, data.length);
		counter = lib.mux(
				lib.add(counter, lib.toSignals(1, oram.lengthOfIden)),
				lib.sub(counter, lib.toSignals(1, oram.lengthOfIden)), op);

	    oram.conditionalPutBack(counter, newIter, newBlock, lib.not(op));
	    
		top = lib.mux(newIter, Arrays.copyOf(block, top.length), op);

		return Arrays.copyOfRange(block, top.length, block.length);
	}
	
	//op = 0
	public void push(T[] data) throws Exception {
		T[] newIter = lib.randBools(rnd, oram.lengthOfPos);
		T[] block = oram.env.newTArray(data.length+oram.lengthOfPos);
	    System.arraycopy(top, 0, block, 0, top.length);
	    System.arraycopy(data, 0, block, top.length, data.length);
	    counter = lib.add(counter, lib.toSignals(1, oram.lengthOfIden));
		oram.putBack(counter, newIter, block);
		top = newIter;
	}
	
	//op = 1
	public T[] pop() throws IOException, Exception {
		boolean[] pos = lib.syncBooleans(lib.getBooleans(top));
		T[] block = oram.readAndRemove(counter, pos, false);
		counter = lib.sub(counter, lib.toSignals(1, oram.lengthOfIden));
		top = Arrays.copyOf(block, top.length);
		
		oram.conditionalPutBack(lib.dummyBlock.iden, lib.dummyBlock.pos, lib.dummyBlock.data, lib.SIGNAL_ZERO);
		
		return Arrays.copyOfRange(block, top.length, block.length);
	}
}
