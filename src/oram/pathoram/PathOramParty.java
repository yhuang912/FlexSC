package oram.pathoram;

import java.io.InputStream;
import java.io.OutputStream;

import oram.Block;
import oram.TreeBasedOramParty;


public abstract class PathOramParty<T> extends TreeBasedOramParty<T> {
	final int lengthOfStash = 80;//should be 89 with lambda is 80
	public BlockInBinary[] stash = new BlockInBinary[lengthOfStash];
	final static int capacity = 4;
	static byte[] seed = new byte[512];
	public PathOramParty(InputStream is, OutputStream os, int N, int dataSize,
			Party p, Mode m) throws Exception {
		super(is, os, N, dataSize, p, capacity, m);

		stash = new BlockInBinary[lengthOfStash];
		if(gen != null) {
			for(int i = 0; i < stash.length; ++i) 
				stash[i] = getDummyBlock();
				Block<T>[][] result = prepareBlocks(stash, stash, stash);
				stash = prepareBlockInBinaries(result[0], result[1]);
		}
		else {
			for(int i = 0; i < stash.length; ++i) 
				stash[i] = getDummyBlock2();	
				BlockInBinary[] randomBucket = randomBucket(lengthOfStash);
				Block<T>[][] result = prepareBlocks(stash, stash, randomBucket);
				stash = randomBucket;
				prepareBlockInBinaries(result[0], result[1]);
		}
	}
	
	public boolean[] randompath() {
		boolean [] result = new boolean[lengthOfPos];
		for(int i = 0; i < result.length; ++i)
			result[i] = commonRandom.nextBoolean();
		return result;
	}
}
