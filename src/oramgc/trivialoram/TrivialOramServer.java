package oramgc.trivialoram;

import java.io.InputStream;
import java.io.OutputStream;
import oramgc.Block;
import oramgc.OramParty;


public class TrivialOramServer<T> extends OramParty<T> {
	BlockInBinary[] bucket;
	int capacity;
	public TrivialOramServer(InputStream is, OutputStream os, int N,
			int dataSize, Mode m) throws Exception {
		super(is, os, N, dataSize, OramParty.Party.SERVER, 1, m);
		this.capacity = N;
		bucket = new BlockInBinary[capacity];
		
		for(int i = 0; i < bucket.length; ++i){
			bucket[i] = getDummyBlock2();
		}
		BlockInBinary[] randomBucket = randomBucket(capacity);
		Block<T>[][] result = prepareBlocks(bucket, bucket, randomBucket);
		bucket = randomBucket;
		prepareBlockInBinaries(result[0], result[1]);
	}
	
	public void add() throws Exception {
		BlockInBinary[] randomBucket = randomBucket(capacity);
		Block<T>[][] result = prepareBlocks(bucket, bucket, randomBucket);
		Block<T>[] scBlocks = result[0];
		Block<T>[] scBlocksMask = result[1];
		
		Block<T> scNewBlock = inputBlockOfClient(getDummyBlock());
		
		lib.add(scBlocks, scNewBlock);
		bucket = randomBucket;
		prepareBlockInBinaries(scBlocks, scBlocksMask);
	}
	
	public BlockInBinary pop() throws Exception{
		BlockInBinary[] randomBucket = randomBucket(bucket.length);
		Block<T>[][] result = prepareBlocks(bucket, bucket, randomBucket);
		Block<T>[] scBlocks = result[0];
		Block<T>[] scBlocksMask = result[1];
		 
		Block<T> res = lib.pop(scBlocks);
		
		bucket = randomBucket;
		prepareBlockInBinaries(scBlocks, scBlocksMask);
		outputBlock(res);
		return null;
	}
	
	public BlockInBinary readAndRemove() throws Exception {
		BlockInBinary[] randomBucket = randomBucket(bucket.length);
		Block<T>[][] result = prepareBlocks(bucket, bucket, randomBucket);
		Block<T>[] scBlocks = result[0];
		Block<T>[] scBlocksMask = result[1];
		T[] scIden = eva.inputOfGen(new boolean[lengthOfIden]); 
		Block<T> res = lib.readAndRemove(scBlocks, scIden);
		
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
