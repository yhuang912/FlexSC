package oramgc.kaiminOram;

import java.security.SecureRandom;
import oramgc.Block;
import oramgc.BucketLib;
import flexsc.CompEnv;

public class KaiminOramLib<T> extends BucketLib<T> {

	int logN;
	SecureRandom rng = new SecureRandom();
	int nodeCapacity, leafCapacity;
	public KaiminOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, int logN, int nodeCapacity,
			int leafCapacity, CompEnv<T> e) {
		super(lengthOfIden, lengthOfPos, lengthOfData, e);
		this.logN = logN;
		this.nodeCapacity = nodeCapacity;
		this.leafCapacity = leafCapacity;
	}
	
	public T[] deepestLevel(T[] pos, T[] path) throws Exception {
		T[] xored = xor(pos, path);
		return leadingZeros(xored);//incrementByOne(leadingZeros(xored)); 
	}
	
	public T[] deepestLevel(T[] pos, T[] path, T isDummy) throws Exception {
		T[] deep = deepestLevel(pos, path);
		return mux(deep, zeros(deep.length), isDummy);
	}
	
	public Block<T> readAndRemove(Block<T>[] path, T[] iden) throws Exception {
		return super.readAndRemove(path, iden);
	}
	
	public Block<T> flushUnit(Block<T>[] top, Block<T> in, int level, T[] path, Block<T>[] tempStash) throws Exception {
		//insert block
		add(top, in);

		//find deepest
		T[][] deepest = env.newTArray(nodeCapacity, 0);//;new T[nodeCapacity][];
		for(int i = 0; i < nodeCapacity; ++i)
			deepest[i] = deepestLevel(top[i].pos, path, top[i].isDummy);
		
		T[] maxIden = top[0].iden;
		T[] maxdepth = deepest[0];
		for(int i = 1; i < nodeCapacity; ++i) {
			T greater = geq(deepest[i], maxdepth);
			maxIden = mux(maxIden, top[i].iden, greater);
			maxdepth = mux(maxdepth, deepest[i], greater);
		}
		
		T cannotPush = leq(maxdepth, toSignals(level-1, maxdepth.length));// deepestlevel is in fact the real depth-1, so we also -1 here.
		
		Block<T> block = conditionalReadAndRemove(top, maxIden, not(cannotPush));
		
		T[] leftVector = zeros(nodeCapacity);
		T[] rightVector = zeros(nodeCapacity);
		T[] left = zeros(lengthOfIden);
		T[] right = zeros(lengthOfIden);
		for(int i = 0; i < nodeCapacity; ++i) {
			T isDummy = top[i].isDummy;
			leftVector[i] = and(top[i].pos[lengthOfPos-level], not(isDummy));
			rightVector[i] = and(not(top[i].pos[lengthOfPos-level]), not(isDummy));
			left = mux(left, top[i].iden, leftVector[i]);
			right = mux(right, top[i].iden, rightVector[i]);
		}
		
		T[] leftCounter = numberOfOnes(leftVector);
		T[] rightCounter = numberOfOnes(rightVector);
		
		T leftOverflow = not(leq(leftCounter, toSignals(nodeCapacity/2, leftCounter.length)));
		T rightOverflow = not(leq(rightCounter, toSignals(nodeCapacity/2, rightCounter.length)));
		T overflow = or(leftOverflow, rightOverflow);
		
		T[] toFetchIden = mux(right, left, leftOverflow);

		conditionalAdd(tempStash, conditionalReadAndRemove(top, toFetchIden, overflow), overflow);
		return block;
	}
}