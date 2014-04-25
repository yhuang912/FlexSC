package oramgc.treeoram;

import gc.Signal;

import java.io.InputStream;
import java.io.OutputStream;

import oramgc.Block;
import oramgc.OramParty.BlockInBinary;
import test.Utils;

public class TreeOramServer extends TreeOramParty {
	TreeOramLib lib;
	public TreeOramServer(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int cap) throws Exception {
		super(is, os, N, dataSize, p, cap);
		lib = new TreeOramLib(lengthOfIden, lengthOfPos, lengthOfData, logN, capacity, eva);
	}
	
	public void add() throws Exception {
		BlockInBinary[] randomBucket = randomBucket(capacity);
		Block scNewBlock = inputBlockOfClient(getDummyBlock());
		Block[][] tree1 = prepareBlocks(tree[1], tree[1], randomBucket);
		 
		lib.add(tree1[0], scNewBlock);

		tree[1] = randomBucket;
		prepareBlockInBinaries(tree1[0], tree1[1]);
	}
	
	public void readAndRemove(boolean[] pos) throws Exception {
		BlockInBinary[] blocks = flatten(getAPath(pos));
		BlockInBinary[] randomBucket = randomBucket(blocks.length);
		Block[][] scPath = prepareBlocks(blocks, blocks, randomBucket);
		
		Signal[] scIden = eva.inputOfGen(new boolean[lengthOfIden]);
				
		Block res = lib.readAndRemove(scPath[0], scIden);
		
		blocks = randomBucket;
		prepareBlockInBinaries(scPath[0], scPath[1]);
		putAPath(blocks, pos);
		outputBlock(res);
	}
	
	public void evictUnit(int index, int level) throws Exception {
		BlockInBinary[] randomBucketTop = randomBucket(capacity);
		Block[][] top = prepareBlocks(tree[index], tree[index], randomBucketTop);
		BlockInBinary[] randomBucketLeft = randomBucket(capacity);
		Block[][] left = prepareBlocks(tree[index*2], tree[index*2], randomBucketLeft);
		BlockInBinary[] randomBucketRight = randomBucket(capacity);
		Block[][] right = prepareBlocks(tree[index*2+1], tree[index*2+1], randomBucketRight);

				
		lib.evitUnit(top[0], 
					 left[0],
					 right[0],level);
		
		tree[index] = randomBucketTop;
		prepareBlockInBinaries(top[0], top[1]);
		tree[index*2] = randomBucketLeft;
		prepareBlockInBinaries(left[0], left[1]);
		tree[index*2+1] = randomBucketRight;
		prepareBlockInBinaries(right[0], right[1]);
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
