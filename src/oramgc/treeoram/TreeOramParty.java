package oramgc.treeoram;

import java.io.InputStream;
import java.io.OutputStream;

import oramgc.Block;
import oramgc.OramParty;

public class TreeOramParty extends OramParty {
	BlockInBinary[][] tree;
	int capacity;
	public TreeOramParty(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int capacity) throws Exception {
		super(is, os, N, dataSize, p);
		capacity = this.capacity;
		tree  = new BlockInBinary[N+1][capacity];
		for(int i = 0; i < N+1; ++i){
			for(int j = 0; j < capacity; ++j)
				tree[i][j] = dummyBlock();
		}
	}
	
	public Block[][] inputPathOfServer(boolean[] path) throws Exception {
		Block[][] result = new Block[logN][capacity];
		int index = 1;
		for(int i = 0; i < logN; ++i) {
			result[i] = inputBucketOfServer(tree[index]);
			index*=2;
			if(path[logN-i])
				++index;
		}
		return result;
	}
	
	public Block[][] inputPathOfClient(boolean[] path) throws Exception {
		Block[][] result = new Block[logN][capacity];
		int index = 1;
		for(int i = 0; i < logN; ++i) {
			result[i] = inputBucketOfClient(tree[index]);
			index*=2;
			if(path[logN-i])
				++index;
		}
		return result;
	}
	
	//public void outputPath

}
