package oram.trivialoram;

import java.io.InputStream;
import java.io.OutputStream;
import flexsc.Mode;
import flexsc.Party;
import oram.Block;
import oram.OramParty;


public class TrivialOramServer<T> extends OramParty<T> {
	public BlockInBinary[] bucket;
	int capacity;
	public TrivialOramServer(InputStream is, OutputStream os, int N,
			int dataSize, Mode m) throws Exception {
		super(is, os, N, dataSize, Party.Bob, 1, m);
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
		T[] scIden = eva.inputOfAlice(new boolean[lengthOfIden]); 
		Block<T> res = lib.readAndRemove(scBlocks, scIden);
		BlockInBinary b = randomBlock();
		Block<T> scb = inputBlockOfClient(b);
		Block<T>finalRes = lib.mux(res, scb, res.isDummy);

		bucket = randomBucket;
		prepareBlockInBinaries(scBlocks, scBlocksMask);
		outputBlock(finalRes);
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
