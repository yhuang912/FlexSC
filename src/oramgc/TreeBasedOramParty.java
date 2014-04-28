package oramgc;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import oramgc.Block;
import oramgc.OramParty;

public abstract class TreeBasedOramParty<T> extends OramParty<T> {
	public BlockInBinary[][] tree;
	protected int capacity;
	static byte[] seed = new byte[512];

	static {
		SecureRandom r = new SecureRandom();
		r.nextBytes(seed);
	}
	protected final SecureRandom commonRandom = SecureRandom.getInstance("SHA1PRNG");

	public TreeBasedOramParty(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int capacity, Mode m) throws Exception {
		super(is, os, N, dataSize, p, m);
		this.capacity = capacity;

		tree  = new BlockInBinary[this.N][capacity];

		if(gen != null) {

			for(int i = 0; i < this.N; ++i) {
				for(int j = 0; j < capacity; ++j)
					tree[i][j] = getDummyBlock();

				Block<T>[][] result = prepareBlocks(tree[i], tree[i], tree[i]);
				tree[i] = prepareBlockInBinaries(result[0], result[1]);
			}
		}
		else {
			for(int i = 0; i < this.N; ++i) {
				for(int j = 0; j < capacity; ++j)
					tree[i][j] = getDummyBlock2();
				
				BlockInBinary[] randomBucket = randomBucket(capacity);
				Block<T>[][] result = prepareBlocks(tree[i], tree[i], randomBucket);
				tree[i] = randomBucket;
				prepareBlockInBinaries(result[0], result[1]);
			}

		}

		commonRandom.setSeed(seed);
	}

	public BlockInBinary[][] getAPath(boolean[] path) {
		BlockInBinary[][] result = new BlockInBinary[logN][capacity];
		int index = 1;
		result[0] = tree[index];
		for(int i = 1; i < logN; ++i) {
			index*=2;
			if(path[lengthOfPos-i])
				++index;
			result[i] = tree[index];
		}
		return result;
	}

	public void putAPath(BlockInBinary[] blocks, boolean[] path){
		int index = 1;
		tree[index] = Arrays.copyOfRange(blocks, 0, capacity);
		for(int i = 1; i < logN; ++i) {
			index*=2;
			if(path[lengthOfPos-i])
				++index;
			tree[index] = Arrays.copyOfRange(blocks, i*capacity, (i+1)*capacity);
		}
	}

	public Block<T>[] flatten(Block<T>[][] path) {
		Block<T>[] result = new Block[path.length * path[0].length];
		int counter = 0;
		for(int i = 0; i < path.length; ++i )
			for(int j = 0; j < path[0].length; ++j)
				result[counter++] = path[i][j];
		return result;
	}

	public BlockInBinary[] flatten(BlockInBinary[][] path) {
		BlockInBinary[] result = new BlockInBinary[path.length * path[0].length];
		int counter = 0;
		for(int i = 0; i < path.length; ++i )
			for(int j = 0; j < path[0].length; ++j)
				result[counter++] = path[i][j];
		return result;
	}

}
