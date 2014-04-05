package oramgc.trivialoram;

import gc.Signal;

import java.io.InputStream;
import java.io.OutputStream;

import oramgc.Block;
import oramgc.BucketLib;
import oramgc.OramParty;
import oramgc.OramParty.BlockInBinary;

public class TrivialOramClient extends OramParty {
	BlockInBinary[] blocks;
	int capacity;
	BucketLib lib;
	public TrivialOramClient(InputStream is, OutputStream os, int N,
			int dataSize, int capacity) throws Exception {
		super(is, os, N, dataSize, OramParty.Party.CLIENT);
		blocks = new BlockInBinary[capacity];
		this.capacity = capacity;
		for(int i = 0; i < blocks.length; ++i)
			blocks[i] = getDummyBlock();
		lib = new BucketLib(lengthOfIden, lengthOfPos, lengthOfData, gen);
	}
	
	public void add(BlockInBinary b) throws Exception{
		Block scNewBlock = inputBlockOfClient(b);
		Block[] scBucketServer = inputBucketOfServer(blocks);
		Block[] scBucketClient = inputBucketOfClient(blocks);
		//BlockInBinary[] randomBucket = randomBucket(blocks.length);
		Block[] scRandomBucket = inputBucketOfServer(blocks);
		
		Block[] bucket = lib.xor(scBucketServer,scBucketClient); 
		lib.add(bucket, scNewBlock);
		Block[] bucketForClient = lib.xor(scRandomBucket, bucket);
		
		//blocks = randomBucket; 
		os.flush();
		blocks = outputBucket(bucketForClient);
		os.flush();
	}
	
	public BlockInBinary pop() throws Exception{
		Block[] scBucketServer = inputBucketOfServer(blocks);
		Block[] scBucketClient = inputBucketOfClient(blocks);
		//BlockInBinary[] randomBucket = randomBucket(blocks.length);
		Block[] scRandomBucket = inputBucketOfServer(blocks);
		Block[] bucket = lib.xor(scBucketServer,scBucketClient); 
		
		Block res = lib.pop(bucket);
		
		Block[] bucketForClient = lib.xor(scRandomBucket, bucket);
		//blocks = randomBucket;
		os.flush();
		blocks = outputBucket(bucketForClient);
		BlockInBinary r =  outputBlock(res);
		os.flush();
		return r;
	}
	
	public BlockInBinary readAndRemove(boolean [] iden) throws Exception {
		Block[] scBucketServer = inputBucketOfServer(blocks);
		Block[] scBucketClient = inputBucketOfClient(blocks);
		//BlockInBinary[] randomBucket = randomBucket(blocks.length);
		Block[] scRandomBucket = inputBucketOfServer(blocks);
		Signal[] scIden = gen.inputOfGen(iden);
		
		Block[] bucket = lib.xor(scBucketServer,scBucketClient); 
		
		Block res = lib.readAndRemove(bucket, scIden);
		
		Block[] bucketForClient = lib.xor(scRandomBucket, bucket);
		//blocks = randomBucket;
		os.flush();
		blocks = outputBucket(bucketForClient);
		BlockInBinary r = outputBlock(res);
		os.flush();
		return r;		
	}
	
	
	

}
