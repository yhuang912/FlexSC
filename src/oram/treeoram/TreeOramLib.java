package oram.treeoram;

import java.security.SecureRandom;

import oram.Block;
import oram.BucketLib;
import flexsc.CompEnv;

public class TreeOramLib<T> extends BucketLib<T> {

	int capacity, logN;
	SecureRandom rng = new SecureRandom();
	public TreeOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, int logN, int capacity,
			CompEnv<T> e) {
		super(lengthOfIden, lengthOfPos, lengthOfData, e);
		this.logN = logN;
		this.capacity = capacity;
	}
	
	public T[] deepestLevel(T[] pos, int path) throws Exception {
		T[] pathSignal = toSignals(path, pos.length);
		T[] xored = xor(pos, pathSignal);
		return leadingZeros(xored);
	}
	
	public void evitUnit(Block<T>[] top, Block<T>[] left, Block<T>[] right, int level) throws Exception {
		Block<T> block = pop(top);
		T toLeft = eq(block.pos[lengthOfPos-level], SIGNAL_ZERO);
		conditionalAdd(left, block, toLeft);
		conditionalAdd(right, block, not(toLeft));
	}	
}