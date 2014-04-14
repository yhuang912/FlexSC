package oramgc.kaiminOram;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import oramgc.Block;
import oramgc.TreeBasedOramParty;

public abstract class KaiminOramParty extends TreeBasedOramParty {
	final boolean DEBUG = true;
	BlockInBinary[] queue;
	final int queueCapacity = 100;
	final int tempStashSize = 5;
	int leafCapacity;
	int nodeCapacity;
	public KaiminOramParty(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int nodeCapacity, int leafCapacity) throws Exception {
		super(is, os, N, dataSize, p, nodeCapacity);
		this.leafCapacity = leafCapacity;
		this.nodeCapacity = nodeCapacity;
		for(int i = this.N/2; i < this.N; ++i) {
			tree[i] = new BlockInBinary[leafCapacity];
			for(int j = 0; j < leafCapacity; ++j)
				tree[i][j] = getDummyBlock();
		}
		queue = new BlockInBinary[queueCapacity];
		for(int i = 0; i < queueCapacity; ++i)
			queue[i] = getDummyBlock();
	}
	
	public void flush() throws Exception{
		while(true){
			int a = commonRandom.nextInt(3);
			if(a == 0)
				return;
			else{
				boolean[] pos = new boolean[lengthOfPos];
				for(int i = 0; i < lengthOfPos; ++i)
					pos[i] = commonRandom.nextBoolean();
				flushOneTime(pos);
			}
		}	
	}
	abstract public void flushOneTime(boolean[] pos) throws Exception;
	
	@Override
	public BlockInBinary[][] getAPath(boolean[] path) {
		BlockInBinary[][] result = new BlockInBinary[logN][];
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
	
	@Override
	public void putAPath(BlockInBinary[] blocks, boolean[] path){
		int index = 1;
		tree[index] = Arrays.copyOfRange(blocks, 0, nodeCapacity);
		for(int i = 1; i < logN; ++i) {
			index*=2;
			if(path[lengthOfPos-i])
				++index;
			if(i != logN-1)
				tree[index] = Arrays.copyOfRange(blocks, i*nodeCapacity, (i+1)*nodeCapacity);
			else
				tree[index] = Arrays.copyOfRange(blocks, i*nodeCapacity, i*nodeCapacity + leafCapacity);
		}
	}
	
	public Block[] flatten(Block[][] path) {
		Block[] result = new Block[(path.length -1) * nodeCapacity + leafCapacity];
		int counter = 0;
		for(int i = 0; i < path.length; ++i )
			for(int j = 0; j < path[i].length; ++j)
				result[counter++] = path[i][j];
		return result;
	}
	
	public BlockInBinary[] flatten(BlockInBinary[][] path) {
		BlockInBinary[] result = new BlockInBinary[(path.length -1) * nodeCapacity + leafCapacity];
		int counter = 0;
		for(int i = 0; i < path.length; ++i )
			for(int j = 0; j < path[i].length; ++j)
				result[counter++] = path[i][j];
		return result;
	}

	
}

