package oramgc.kaiminOram;

import java.security.SecureRandom;

import oramgc.Block;
import oramgc.BucketLib;
import flexsc.CompEnv;
import gc.Signal;

public class kaiminOramLib extends BucketLib {

	int capacity, logN;
	SecureRandom rng = new SecureRandom();
	public kaiminOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, int logN, int capacity,
			CompEnv<Signal> e) {
		super(lengthOfIden, lengthOfPos, lengthOfData, e);
		this.logN = logN;
		this.capacity = capacity;
	}
	
	public Signal[] deepestLevel(Signal[] pos, Signal[] path) throws Exception {
		Signal[] xored = xor(pos, path);
		return incrementByOne(leadingZeros(xored)); 
	}
	
	public Signal[] deepestLevel(Signal[] pos, Signal[] path, Signal[] iden) throws Exception {
		Signal[] deep = deepestLevel(pos, path);
		Signal e = eq(iden, zeros(lengthOfIden));
		return mux(deep, zeros(deep.length), e);
	}
	
	public Block readAndRemove(Block[] path, Signal[] iden) throws Exception {
		return super.readAndRemove(path, iden);
	}
	
	public void evitUnit(Block[] top, Block[] left, Block[] right, int level, boolean[] path) throws Exception {
		Signal[] pathSignal = new Signal[path.length];
		for(int i = 0; i < path.length; ++i)
			pathSignal[i] = path[i] ? SIGNAL_ONE : SIGNAL_ZERO;
		
		Signal[][] deepest = new Signal[capacity][];
		for(int i = 0; i < capacity; ++i)
			deepest[i] = deepestLevel(top[i].pos, pathSignal, top[i].iden);
		
		Signal[] maxIden = top[0].iden;
		Signal[] maxdepth = deepest[0];
		for(int i = 1; i < capacity; ++i) {
			Signal greater = geq(deepest[i], maxdepth);
			maxIden = mux(maxIden, top[i].iden, greater);
			maxdepth = mux(maxdepth, deepest[i], greater);
		}
		
		Block block = readAndRemove(top, maxIden);
		Signal toLeft = eq(block.pos[lengthOfPos-level], SIGNAL_ZERO);
		conditionalAdd(left, block, toLeft);
		conditionalAdd(right, block, not(toLeft));
	}
}