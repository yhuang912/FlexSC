package oramgc.treeoram;

import java.security.SecureRandom;
import oramgc.Block;
import oramgc.BucketLib;
import flexsc.CompEnv;
import gc.Signal;

public class TreeOramLib extends BucketLib {

	int capacity, logN;
	SecureRandom rng = new SecureRandom();
	public TreeOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, int logN, int capacity,
			CompEnv<Signal> e) {
		super(lengthOfIden, lengthOfPos, lengthOfData, e);
		this.logN = logN;
		this.capacity = capacity;
	}
	
	public Signal[] deepestLevel(Signal[] pos, int path) throws Exception {
		Signal[] pathSignal = toSignals(path, pos.length);
		Signal[] xored = xor(pos, pathSignal);
		return leadingZeros(xored);
	}
	
	public Block readAndRemove(Block[] path, Signal[] iden) throws Exception {
		return super.readAndRemove(path, iden);
	}
	
	public void evitUnit(Block[] top, Block[] left, Block[] right, int level) throws Exception {
		Block block = pop(top);
		Signal toLeft = eq(block.pos[lengthOfPos-level], SIGNAL_ZERO);
		conditionalAdd(left, block, toLeft);
		conditionalAdd(right, block, not(toLeft));
	}	
}