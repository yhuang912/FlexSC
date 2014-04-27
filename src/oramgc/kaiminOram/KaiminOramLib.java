package oramgc.kaiminOram;

import java.security.SecureRandom;
import oramgc.Block;
import oramgc.BucketLib;
import flexsc.CompEnv;
import gc.GCSignal;

public class KaiminOramLib extends BucketLib {

	int logN;
	SecureRandom rng = new SecureRandom();
	int nodeCapacity, leafCapacity;
	public KaiminOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, int logN, int nodeCapacity,
			int leafCapacity,
			CompEnv<GCSignal> e) {
		super(lengthOfIden, lengthOfPos, lengthOfData, e);
		this.logN = logN;
		this.nodeCapacity = nodeCapacity;
		this.leafCapacity = leafCapacity;
	}
	
	public GCSignal[] deepestLevel(GCSignal[] pos, GCSignal[] path) throws Exception {
		GCSignal[] xored = xor(pos, path);
		return leadingZeros(xored);//incrementByOne(leadingZeros(xored)); 
	}
	
	public GCSignal[] deepestLevel(GCSignal[] pos, GCSignal[] path, GCSignal isDummy) throws Exception {
		GCSignal[] deep = deepestLevel(pos, path);
		return mux(deep, zeros(deep.length), isDummy);
	}
	
	public Block readAndRemove(Block[] path, GCSignal[] iden) throws Exception {
		return super.readAndRemove(path, iden);
	}
	
	public Block flushUnit(Block[] top, Block in, int level, GCSignal[] path, Block[] tempStash) throws Exception {
		//insert block
		add(top, in);

		//find deepest
		GCSignal[][] deepest = new GCSignal[nodeCapacity][];
		for(int i = 0; i < nodeCapacity; ++i)
			deepest[i] = deepestLevel(top[i].pos, path, top[i].isDummy);
		
		GCSignal[] maxIden = top[0].iden;
		GCSignal[] maxdepth = deepest[0];
		for(int i = 1; i < nodeCapacity; ++i) {
			GCSignal greater = geq(deepest[i], maxdepth);
			maxIden = mux(maxIden, top[i].iden, greater);
			maxdepth = mux(maxdepth, deepest[i], greater);
		}
		
		GCSignal cannotPush = leq(maxdepth, toSignals(level-1, maxdepth.length));// deepestlevel is in fact the real depth-1, so we also -1 here.
		
		Block block = conditionalReadAndRemove(top, maxIden, not(cannotPush));
		
		GCSignal[] leftVector = zeros(nodeCapacity);
		GCSignal[] rightVector = zeros(nodeCapacity);
		GCSignal[] left = zeros(lengthOfIden);
		GCSignal[] right = zeros(lengthOfIden);
		for(int i = 0; i < nodeCapacity; ++i) {
			GCSignal isDummy = top[i].isDummy;
			leftVector[i] = and(top[i].pos[lengthOfPos-level], not(isDummy));
			rightVector[i] = and(not(top[i].pos[lengthOfPos-level]), not(isDummy));
			left = mux(left, top[i].iden, leftVector[i]);
			right = mux(right, top[i].iden, rightVector[i]);
		}
		
		GCSignal[] leftCounter = numberOfOnes(leftVector);
		GCSignal[] rightCounter = numberOfOnes(rightVector);
		
		GCSignal leftOverflow = not(leq(leftCounter, toSignals(nodeCapacity/2, leftCounter.length)));
		GCSignal rightOverflow = not(leq(rightCounter, toSignals(nodeCapacity/2, rightCounter.length)));
		GCSignal overflow = or(leftOverflow, rightOverflow);
		
		GCSignal[] toFetchIden = mux(right, left, leftOverflow);

		conditionalAdd(tempStash, conditionalReadAndRemove(top, toFetchIden, overflow), overflow);
		return block;
	}
}