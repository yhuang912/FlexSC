package oramgc.pathoram;

import gc.Signal;

import java.io.InputStream;
import java.io.OutputStream;

import oramgc.Block;
import test.Utils;


public class PathOramServer extends PathOramParty {
	PathOramLib lib;
	public PathOramServer(InputStream is, OutputStream os, int N, int dataSize,
			Party p) throws Exception {
		super(is, os, N, dataSize, p);
		lib = new PathOramLib(lengthOfIden, lengthOfPos, lengthOfData, logN, eva);
	}
	
	public void read(int pos) throws Exception {
		read(Utils.fromInt(pos, lengthOfPos));
	}
	
	public void write(int pos) throws Exception {
		write(Utils.fromInt(pos, lengthOfPos));
	}
	
	public void read(boolean[] pos) throws Exception {
		access(pos,null);
	}
	
	public void write(boolean[] pos) throws Exception {
		access(pos, new boolean[1]);
	}
	
	public void access(boolean[] pos, boolean[] data) throws Exception {
		//prepare path
		BlockInBinary[] blocks = flatten(getAPath(pos));
		BlockInBinary[] randomBucket = randomBucket(blocks.length);
		Block[][] scPath = prepareBlocks(blocks, blocks, randomBucket);
		
		//prepare stash
		BlockInBinary[] randomBucketStash = randomBucket(stash.length);
		Block[][] scStash = prepareBlocks(stash, stash, randomBucketStash);
		
		//prepare newblock
		Signal[] scIden = eva.inputOfGen(new boolean[lengthOfIden]);
		Signal[] scPos = eva.inputOfGen(new boolean[lengthOfPos]);
		
		Block res = lib.readAndRemove(scPath[0], scIden);
		outputBlock(res);
		Signal[] scData = res.data;
		if(data != null)
			scData = eva.inputOfGen(new boolean[lengthOfData]);
		
		Block scNewBlock = new Block(scIden, scPos, scData);

		
		lib.add(scStash[0], scNewBlock);
		Signal[][] debug = lib.pushDown(scPath[0], scStash[0], pos);
		
		blocks = randomBucket;
		stash = randomBucketStash;
		prepareBlockInBinaries(scPath[0], scPath[1]);
		prepareBlockInBinaries(scStash[0], scStash[1]);
		putAPath(blocks, pos);
		
		
		//for(int i = 0; i < debug.length; ++i)
		//	eva.outputToGen(debug[i]);
		System.out.println(eva.nonFreeGate);
	}
}
