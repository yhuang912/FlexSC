package oramgc.pathoram;

import java.io.InputStream;
import java.io.OutputStream;
import oramgc.Block;
import oramgc.TreeBasedOramParty;


public abstract class PathOramParty extends TreeBasedOramParty {
	final int lengthOfStash = 10;//should be 89 with lambda is 80
	public BlockInBinary[] stash = new BlockInBinary[lengthOfStash];
	final static int capacity = 4;
	static byte[] seed = new byte[512];
	public PathOramParty(InputStream is, OutputStream os, int N, int dataSize,
			Party p) throws Exception {
		super(is, os, N, dataSize, p, capacity);

		stash = new BlockInBinary[lengthOfStash];
		if(gen != null) {
			for(int i = 0; i < stash.length; ++i) 
				stash[i] = getDummyBlock();
				Block[][] result = prepareBlocks(stash, stash, stash);
				stash = prepareBlockInBinaries(result[0], result[1]);
		}
		else {
			for(int i = 0; i < stash.length; ++i) 
				stash[i] = getDummyBlock2();	
				BlockInBinary[] randomBucket = randomBucket(lengthOfStash);
				Block[][] result = prepareBlocks(stash, stash, randomBucket);
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
