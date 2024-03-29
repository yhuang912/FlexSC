package oram.treeoram;


import java.io.InputStream;
import java.io.OutputStream;
import flexsc.*;
import oram.Block;
import test.Utils;

public class TreeOramServer<T> extends TreeOramParty<T> {
	TreeOramLib<T> lib;
	public TreeOramServer(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int cap, Mode m) throws Exception {
		super(is, os, N, dataSize, p, cap, m);
		lib = new TreeOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, capacity, eva);
	}
	
	public void add() throws Exception {
		BlockInBinary[] randomBucket = randomBucket(capacity);
		Block<T> scNewBlock = inputBlockOfClient(getDummyBlock());
		Block<T>[][] tree1 = prepareBlocks(tree[1], tree[1], randomBucket);
		 
		lib.add(tree1[0], scNewBlock);

		tree[1] = randomBucket;
		prepareBlockInBinaries(tree1[0], tree1[1]);
	}
	
	public void readAndRemove(boolean[] pos) throws Exception {
		BlockInBinary[] blocks = flatten(getAPath(pos));
		BlockInBinary[] randomBucket = randomBucket(blocks.length);
		Block<T>[][] scPath = prepareBlocks(blocks, blocks, randomBucket);
		
		T[] scIden = eva.inputOfAlice(new boolean[lengthOfIden]);
				
		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		
		blocks = randomBucket;
		prepareBlockInBinaries(scPath[0], scPath[1]);
		putAPath(blocks, pos);
		outputBlock(res);
	}
	
	public void evictUnit(int index, int level) throws Exception {
		BlockInBinary[] randomBucketTop = randomBucket(capacity);
		Block<T>[][] top = prepareBlocks(tree[index], tree[index], randomBucketTop);
		BlockInBinary[] randomBucketLeft = randomBucket(capacity);
		Block<T>[][] left = prepareBlocks(tree[index*2], tree[index*2], randomBucketLeft);
		BlockInBinary[] randomBucketRight = randomBucket(capacity);
		Block<T>[][] right = prepareBlocks(tree[index*2+1], tree[index*2+1], randomBucketRight);

				
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
