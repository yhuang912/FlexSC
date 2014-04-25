package oramgc.kaiminOram;

import java.security.SecureRandom;
import oramgc.Block;
import oramgc.BucketLib;
import flexsc.CompEnv;
import gc.Signal;

public class KaiminOramLib extends BucketLib {

	int logN;
	SecureRandom rng = new SecureRandom();
	int nodeCapacity, leafCapacity;
	public KaiminOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, int logN, int nodeCapacity,
			int leafCapacity,
			CompEnv<Signal> e) {
		super(lengthOfIden, lengthOfPos, lengthOfData, e);
		this.logN = logN;
		this.nodeCapacity = nodeCapacity;
		this.leafCapacity = leafCapacity;
	}
	
	public Signal[] deepestLevel(Signal[] pos, Signal[] path) throws Exception {
		Signal[] xored = xor(pos, path);
		return leadingZeros(xored);//incrementByOne(leadingZeros(xored)); 
	}
	
	public Signal[] deepestLevel(Signal[] pos, Signal[] path, Signal isDummy) throws Exception {
		Signal[] deep = deepestLevel(pos, path);
		return mux(deep, zeros(deep.length), isDummy);
	}
	
	public Block readAndRemove(Block[] path, Signal[] iden) throws Exception {
		return super.readAndRemove(path, iden);
	}
	
	public Block flushUnit(Block[] top, Block in, int level, Signal[] path, Block[] tempStash) throws Exception {
		//insert block
		add(top, in);

		//find deepest
		Signal[][] deepest = new Signal[nodeCapacity][];
		for(int i = 0; i < nodeCapacity; ++i)
			deepest[i] = deepestLevel(top[i].pos, path, top[i].isDummy);
		
		Signal[] maxIden = zeros(lengthOfIden);
		Signal[] maxdepth = zeros(deepest[0].length);
		for(int i = 1; i < nodeCapacity; ++i) {
			Signal greater = geq(deepest[i], maxdepth);
			maxIden = mux(maxIden, top[i].iden, greater);
			maxdepth = mux(maxdepth, deepest[i], greater);
		}
		
		Signal cannotPush = leq(maxdepth, toSignals(level-1, maxdepth.length));// deepestlevel is in fact the real depth-1, so we also -1 here.
		//Signal 
		//maxIden = mux(maxIden, zeros(lengthOfIden), cannotPush);
		
		Block block = conditionalReadAndRemove(top, maxIden, not(cannotPush));
		
		Signal[] leftVector = zeros(nodeCapacity);
		Signal[] rightVector = zeros(nodeCapacity);
		Signal[] left = zeros(lengthOfIden);
		Signal[] right = zeros(lengthOfIden);
		for(int i = 0; i < nodeCapacity; ++i) {
			Signal isDummy = top[i].isDummy;
			leftVector[i] = and(top[i].pos[lengthOfPos-level], not(isDummy));
			rightVector[i] = and(not(top[i].pos[lengthOfPos-level]), not(isDummy));
			left = mux(left, top[i].iden, leftVector[i]);
			right = mux(right, top[i].iden, rightVector[i]);
		}
		
		Signal[] leftCounter = numberOfOnes(leftVector);
		Signal[] rightCounter = numberOfOnes(rightVector);
		
		Signal leftOverflow = geq(leftCounter, toSignals(nodeCapacity/2, leftCounter.length));
		Signal rightOverflow = geq(rightCounter, toSignals(nodeCapacity/2, rightCounter.length));
		
		Signal[] toFetchIden = mux(zeros(lengthOfIden), left, leftOverflow);
		//toFetchIden = mux(toFetchIden, right, rightOverflow);
		
		add(tempStash, readAndRemove(top, toFetchIden));
		
		toFetchIden = mux(zeros(lengthOfIden), right, rightOverflow);
		add(tempStash, readAndRemove(top, toFetchIden));
		return block;
	}
}