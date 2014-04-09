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
		Signal[] scNewPos = eva.inputOfGen(new boolean[lengthOfPos]);
		Signal[] scData = null;
		if(data != null)
			scData = eva.inputOfGen(new boolean[lengthOfData]);
		
		Block res = lib.readAndRemove(scPath[0], scIden);
		res.iden = scIden;
		res.pos = scNewPos;
		if(data != null)
			res.data = scData;
		
		lib.add(scStash[0], res);
		Signal[][] debug = lib.pushDown(scPath[0], scStash[0], pos);
		
		blocks = randomBucket;
		stash = randomBucketStash;
		prepareBlockInBinaries(scPath[0], scPath[1]);
		prepareBlockInBinaries(scStash[0], scStash[1]);
		putAPath(blocks, pos);
		outputBlock(res);
		
		for(int i = 0; i < debug.length; ++i)
			eva.outputToGen(debug[i]);
	}
}
