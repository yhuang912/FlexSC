package oram.treeoram;


import java.io.InputStream;
import java.io.OutputStream;

import flexsc.*;
import oram.Block;
import oram.PlainBlock;
import test.Utils;

public class TreeOramServer<T> extends TreeOramParty<T> {
	TreeOramLib<T> lib;
	public TreeOramServer(InputStream is, OutputStream os, int N, int dataSize,
			Party p, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, m, sp);
		lib = new TreeOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, capacity, eva);
	}
	
	public void add() throws Exception {
		PlainBlock[] randomBucket = randomBucket(capacity);
		Block<T> scNewBlock = inputBlockOfClient(getDummyBlock(true));
		Block<T>[][] tree1 = prepareBlocks(tree[1], tree[1], randomBucket);
		 
		lib.add(tree1[0], scNewBlock);

		tree[1] = randomBucket;
		preparePlainBlocks(tree1[0], tree1[1]);
	}
	
	public void readAndRemove(boolean[] pos) throws Exception {
		PlainBlock[][] blocks = getPath(pos);
		PlainBlock[][] ranPath = randomPath(blocks);
		Block<T>[][][] scPath = preparePath(blocks, blocks, ranPath);
		
		T[] scIden = eva.inputOfAlice(new boolean[lengthOfIden]);
		
		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		
		preparePlainPath(scPath[0], scPath[1]);
		putPath(ranPath, pos);
		outputBlock(res);
	}
	
	PlainBlock[][] ranTriple = new PlainBlock[3][];
	protected Block[][][] prepareBlocksTriple(int index) throws Exception {
		Block[][][] result = new Block[2][3][];
		ranTriple[0] = randomBucket(tree[index].length);
		ranTriple[1] = randomBucket(tree[index*2].length);
		ranTriple[2] = randomBucket(tree[index*2+1].length);
		Block<T>[][] top = prepareBlocks(tree[index], tree[index], ranTriple[0]);
		Block<T>[][] left = prepareBlocks(tree[index*2], tree[index*2], ranTriple[1]);
		Block<T>[][] right = prepareBlocks(tree[index*2+1], tree[index*2+1], ranTriple[2]);
		
		result[0][0] = top[0];
		result[1][0] = top[1];
		result[0][1] = left[0];
		result[1][1] = left[1];
		result[0][2] = right[0];
		result[1][2] = right[1];
		return result;
	}
	
	public void evictUnit(int index, int level) throws Exception {
		if(mode == mode.COUNT)
			index = 0;
		Block<T>[][][] triple = prepareBlocksTriple(index);				
		lib.evitUnit(triple[0][0], triple[0][1],triple[0][2],
					 level);
		
		tree[index] = ranTriple[0];
		preparePlainBlocks(triple[0][0], triple[1][0]);
		tree[index*2] = ranTriple[1];
		preparePlainBlocks(triple[0][1], triple[1][1]);
		tree[index*2+1] =ranTriple[2];
		preparePlainBlocks(triple[0][2], triple[1][2]);
	}
	
	public void access(int pos) throws Exception {
		access(Utils.fromInt(pos, lengthOfPos));
	}

	
	public void access(boolean[] pos) throws Exception {
		readAndRemove(pos);
		putBack();
	}
	
	
	public void putBack() throws Exception {
		add();
		evict();
		evict();
	}

}
