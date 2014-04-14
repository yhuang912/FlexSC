package oramgc.pathoram;

import java.io.InputStream;
import java.io.OutputStream;
import oramgc.TreeBasedOramParty;

public abstract class PathOramParty extends TreeBasedOramParty {
	final int lengthOfStash = 89;//should be 89 with lambda is 80
	public BlockInBinary[] stash = new BlockInBinary[lengthOfStash];
	final static int capacity = 4;
	static byte[] seed = new byte[512];
	public PathOramParty(InputStream is, OutputStream os, int N, int dataSize,
			Party p) throws Exception {
		super(is, os, N, dataSize, p, capacity);

		for(int i = 0; i < stash.length; ++i) {
				stash[i] = getDummyBlock();
		}
	}
	
	public boolean[] randompath() {
		boolean [] result = new boolean[lengthOfPos];
		for(int i = 0; i < result.length; ++i)
			result[i] = commonRandom.nextBoolean();
		return result;
	}
}
