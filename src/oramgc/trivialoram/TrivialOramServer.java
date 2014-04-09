package oramgc.trivialoram;

import gc.Signal;

import java.io.InputStream;
import java.io.OutputStream;

import oramgc.Block;
import oramgc.OramParty;

public class TrivialOramServer extends OramParty {
	BlockInBinary[] bucket;
	int capacity;
	public TrivialOramServer(InputStream is, OutputStream os, int N,
			int dataSize, int capacity) throws Exception {
		super(is, os, N, dataSize, OramParty.Party.SERVER);
		this.capacity = capacity;
		bucket = new BlockInBinary[capacity];
		for(int i = 0; i < bucket.length; ++i)
			bucket[i] = getDummyBlock();
	}
	
	public void add() throws Exception {
		BlockInBinary[] randomBucket = randomBucket(capacity);
		Block[][] result = prepareBlocks(bucket, bucket, randomBucket);
		Block[] scBlocks = result[0];
		Block[] scBlocksMask = result[1];
		
		Block scNewBlock = inputBlockOfClient(getDummyBlock());
		
		lib.add(scBlocks, scNewBlock);
		bucket = randomBucket;
		prepareBlockInBinaries(scBlocks, scBlocksMask);
	}
	
	public BlockInBinary pop() throws Exception{
		BlockInBinary[] randomBucket = randomBucket(bucket.length);
		Block[][] result = prepareBlocks(bucket, bucket, randomBucket);
		Block[] scBlocks = result[0];
		Block[] scBlocksMask = result[1];
		 
		Block res = lib.pop(scBlocks);
		
		bucket = randomBucket;
		prepareBlockInBinaries(scBlocks, scBlocksMask);
		outputBlock(res);
		return null;
	}
	
	public BlockInBinary readAndRemove() throws Exception {
		BlockInBinary[] randomBucket = randomBucket(bucket.length);
		Block[][] result = prepareBlocks(bucket, bucket, randomBucket);
		Block[] scBlocks = result[0];
		Block[] scBlocksMask = result[1];
		Signal[] scIden = eva.inputOfGen(new boolean[lengthOfIden]); 
		
		Block res = lib.readAndRemove(scBlocks, scIden);
		
		bucket = randomBucket;
		prepareBlockInBinaries(scBlocks, scBlocksMask);
		outputBlock(res);
		return null;		
	}
	
	
	

}
