package oramgc.pathoram;

import java.io.InputStream;
import java.io.OutputStream;
import oramgc.Block;
import oramgc.OramParty.BlockInBinary;
import test.Utils;
import flexsc.*;

public class PathOramServer<T> extends PathOramParty<T> {
	PathOramLib<T> lib;
	public PathOramServer(InputStream is, OutputStream os, int N, int dataSize,
			Party p, Mode m) throws Exception {
		super(is, os, N, dataSize, p, m);
		lib = new PathOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, eva);
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
		Block<T>[][] scPath = prepareBlocks(blocks, blocks, randomBucket);
		
		//prepare stash
		BlockInBinary[] randomBucketStash = randomBucket(stash.length);
		Block<T>[][] scStash = prepareBlocks(stash, stash, randomBucketStash);
		
		//prepare newblock
		T[] scIden = eva.inputOfGen(new boolean[lengthOfIden]);
		T[] scPos = eva.inputOfGen(new boolean[lengthOfPos]);
		
		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		outputBlock(res);
		T[] scData = res.data;
		if(data != null)
			scData = eva.inputOfGen(new boolean[lengthOfData]);
		
		Block<T> scNewBlock = new Block<T>(scIden, scPos, scData, lib.SIGNAL_ZERO);
		
		lib.add(scStash[0], scNewBlock);
		//Signal[][] debug = 
				lib.pushDown(scPath[0], scStash[0], pos);
		
		blocks = randomBucket;
		stash = randomBucketStash;
		prepareBlockInBinaries(scPath[0], scPath[1]);
		prepareBlockInBinaries(scStash[0], scStash[1]);
		putAPath(blocks, pos);
		
		//for(int i = 0; i < debug.length; ++i)
		//	eva.outputToGen(debug[i]);
		//System.out.println(eva.nonFreeGate);
	}
	BlockInBinary[] randomBucketStash;
	Block<T>[][] scStash;
	Block<T>[][] scPath;
	BlockInBinary[] randomBucket;
	T[] scIden;
	T[] scPos;
	boolean[] WorkingPos;
	public void readAndRemove(boolean[] pos) throws Exception {
		WorkingPos = pos;
		//prepare path
		BlockInBinary[] blocks = flatten(getAPath(pos));
		randomBucket = randomBucket(blocks.length);
		scPath = prepareBlocks(blocks, blocks, randomBucket);
		
		//prepare stash
		randomBucketStash = randomBucket(stash.length);
		scStash = prepareBlocks(stash, stash, randomBucketStash);
		
		//prepare newblock
		scIden = eva.inputOfGen(new boolean[lengthOfIden]);
		
		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		Block<T> res2 = lib.readAndRemove(scStash[0], scIden);
		res = lib.mux(res, res2, res.isDummy);
		BlockInBinary b = randomBlock();
		Block<T> scb = inputBlockOfClient(b);
		Block<T>finalRes = lib.mux(res, scb, res.isDummy);

		
		outputBlock(finalRes);
	}
	
	public void putBack() throws Exception {
		boolean[] pos = WorkingPos;
		scPos = eva.inputOfGen(new boolean[lengthOfPos]);
		T[] scData = eva.inputOfGen(new boolean[lengthOfData]);
		
		Block<T> scNewBlock = new Block<T>(scIden, scPos, scData, lib.SIGNAL_ZERO);
		
		lib.add(scStash[0], scNewBlock);
		//Signal[][] debug = 
				lib.pushDown(scPath[0], scStash[0], pos);
		
		BlockInBinary[] blocks = randomBucket;
		stash = randomBucketStash;
		prepareBlockInBinaries(scPath[0], scPath[1]);
		prepareBlockInBinaries(scStash[0], scStash[1]);
		putAPath(blocks, pos);
		
		//for(int i = 0; i < debug.length; ++i)
		//	eva.outputToGen(debug[i]);
		//System.out.println(eva.nonFreeGate);
	}
	
	public void access(int pos) throws Exception {
		readAndRemove(Utils.fromInt(pos, lengthOfPos));
		putBack();
	}
	
	public void access(boolean[] pos) throws Exception {
		access(Utils.toInt(pos));
	}
}
