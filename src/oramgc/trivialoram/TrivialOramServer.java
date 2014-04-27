package oramgc.trivialoram;

import gc.GCSignal;

import java.io.InputStream;
import java.io.OutputStream;

import oramgc.Block;
import oramgc.OramParty;
import oramgc.OramParty.BlockInBinary;
import test.Utils;

public class TrivialOramServer extends OramParty {
	BlockInBinary[] bucket;
	int capacity;
	public TrivialOramServer(InputStream is, OutputStream os, int N,
			int dataSize) throws Exception {
		super(is, os, N, dataSize, OramParty.Party.SERVER, 1);
		this.capacity = N;
		bucket = new BlockInBinary[capacity];
		
		for(int i = 0; i < bucket.length; ++i){
			bucket[i] = getDummyBlock2();
		}
		BlockInBinary[] randomBucket = randomBucket(capacity);
		Block[][] result = prepareBlocks(bucket, bucket, randomBucket);
		bucket = randomBucket;
		prepareBlockInBinaries(result[0], result[1]);
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
		GCSignal[] scIden = eva.inputOfGen(new boolean[lengthOfIden]); 
		Block res = lib.readAndRemove(scBlocks, scIden);
		
		bucket = randomBucket;
		prepareBlockInBinaries(scBlocks, scBlocksMask);
		outputBlock(res);
		return null;		
	}
	
	public void access() throws Exception {
		readAndRemove();
		add();
	}
	public void putBack() throws Exception{
		add();
	}
}
