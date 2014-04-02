package oramgc.trivialoram;

import gc.Signal;

import java.io.InputStream;
import java.io.OutputStream;
import oramgc.Block;
import oramgc.BucketLib;
import oramgc.OramParty;

public class TrivialOramServer extends OramParty {
	BlockInBinary[] blocks;
	int capacity;
	BucketLib lib;
	public TrivialOramServer(InputStream is, OutputStream os, int N,
			int dataSize, int capacity) throws Exception {
		super(is, os, N, dataSize, OramParty.Party.SERVER);
		this.capacity = capacity;
		blocks = new BlockInBinary[capacity];
		for(int i = 0; i < blocks.length; ++i)
			blocks[i] = dummyBlock();
		lib = new BucketLib(lengthOfIden, lengthOfPos, lengthOfData, eva);
	}
	
	public void add() throws Exception{
		BlockInBinary[] randomBucket = randomBucket(blocks.length);
		Block scNewBlock = inputBlockOfClient(null);
		Block[] scBucketServer = inputBucketOfServer(blocks);
		Block[] scBucketClient = inputBucketOfClient(blocks);
		Block[] scRandomBucket = inputBucketOfServer(randomBucket);
		
		Block[] bucket = lib.xor(scBucketServer,scBucketClient); 
		
		lib.add(bucket, scNewBlock);
		
		Block[] bucketForClient = lib.xor(scRandomBucket, bucket);
		
		blocks = randomBucket;
		os.flush();
		outputBucket(bucketForClient);
		os.flush();
	}
	
	public BlockInBinary pop() throws Exception{
		BlockInBinary[] randomBucket = randomBucket(blocks.length);
		Block[] scBucketServer = inputBucketOfServer(blocks);
		Block[] scBucketClient = inputBucketOfClient(blocks);
		Block[] scRandomBucket = inputBucketOfServer(randomBucket);
		Block[] bucket = lib.xor(scBucketServer,scBucketClient); 
		
		Block res = lib.pop(bucket);
		
		Block[] bucketForClient = lib.xor(scRandomBucket, bucket);
		blocks = randomBucket;
		os.flush();
		outputBucket(bucketForClient);
		outputBlock(res);
		os.flush();
		return null;
	}
	
	public BlockInBinary readAndRemove() throws Exception {
		Block[] scBucketServer = inputBucketOfServer(blocks);
		Block[] scBucketClient = inputBucketOfClient(blocks);
		BlockInBinary[] randomBucket = randomBucket(blocks.length);
		Block[] scRandomBucket = inputBucketOfServer(randomBucket);
		Signal[] scIden = eva.inputOfGen(new boolean[lengthOfIden]);
		
		Block[] bucket = lib.xor(scBucketServer,scBucketClient); 
		
		Block res = lib.readAndRemove(bucket, scIden);
		
		Block[] bucketForClient = lib.xor(scRandomBucket, bucket);
		blocks = randomBucket;
		os.flush();
		outputBucket(bucketForClient);
		outputBlock(res);
		os.flush();
		return null;		
	}
	
	
	

}
