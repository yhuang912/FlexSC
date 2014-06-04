package oram.trivialoram;

import java.io.InputStream;
import java.io.OutputStream;

import flexsc.Mode;
import flexsc.Party;
import oram.Block;
import oram.OramParty;
import oram.PlainBlock;


public class TrivialOramServer<T> extends OramParty<T> {
	public PlainBlock[] bucket;
	int capacity;
	Block<T>[][] result;
	public TrivialOramServer(InputStream is, OutputStream os, int N,
			int dataSize, Mode m) throws Exception {
		super(is, os, N, dataSize, Party.Bob, 1, m);
		this.capacity = N;
		bucket = new PlainBlock[capacity];
		
		for(int i = 0; i < bucket.length; ++i){
			bucket[i] = getDummyBlock(false);
		}
		PlainBlock[] randomBucket = randomBucket(capacity);
		result = prepareBlocks(bucket, bucket, randomBucket);
//		bucket = randomBucket;
//		preparePlainBlocks(result[0], result[1]);
	}
	
	public void add() throws Exception {
		//PlainBlock[] randomBucket = randomBucket(capacity);
		//Block<T>[][] result = prepareBlocks(bucket, bucket, randomBucket);
		Block<T>[] scBlocks = result[0];
//		Block<T>[] scBlocksMask = result[1];
		
		Block<T> scNewBlock = inputBlockOfClient(getDummyBlock(true));
		
		lib.add(scBlocks, scNewBlock);
//		bucket = randomBucket;
//		preparePlainBlocks(scBlocks, scBlocksMask);
	}
	
	public PlainBlock pop() throws Exception{
//		PlainBlock[] randomBucket = randomBucket(bucket.length);
//		Block<T>[][] result = prepareBlocks(bucket, bucket, randomBucket);
		Block<T>[] scBlocks = result[0];
//		Block<T>[] scBlocksMask = result[1];
		 
		Block<T> res = lib.pop(scBlocks);
		
//		bucket = randomBucket;
//		preparePlainBlocks(scBlocks, scBlocksMask);
		outputBlock(res);
		return null;
	}
	
	public PlainBlock readAndRemove() throws Exception {
//		PlainBlock[] randomBucket = randomBucket(bucket.length);
//		Block<T>[][] result = prepareBlocks(bucket, bucket, randomBucket);
		Block<T>[] scBlocks = result[0];
//		Block<T>[] scBlocksMask = result[1];
		T[] scIden = eva.inputOfAlice(new boolean[lengthOfIden]); 
		Block<T> res = lib.readAndRemove(scBlocks, scIden);
		PlainBlock b = randomBlock();
		Block<T> scb = inputBlockOfClient(b);
		Block<T>finalRes = lib.mux(res, scb, res.isDummy);

//		bucket = randomBucket;
//		preparePlainBlocks(scBlocks, scBlocksMask);
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
