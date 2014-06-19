package oram.pathoramNaive;

import java.io.InputStream;
import java.io.OutputStream;

import oram.Block;
import oram.PlainBlock;
import test.Utils;
import flexsc.Mode;
import flexsc.Party;


public class PathOramServer<T> extends PathOramParty<T> {
	PathOramLib<T> lib;
	public PathOramServer(InputStream is, OutputStream os, int N, int dataSize, int cap,
			Party p, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, cap, p, m, sp);
		lib = new PathOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, eva);
		randomBucketStash = randomBucket(stash.length);
		scStash = prepareBlocks(stash, stash, randomBucketStash);

	}
	
	PlainBlock[] randomBucketStash;
	Block<T>[][] scStash;
	Block<T>[][][] scPath;
	PlainBlock[][] randomPath;
	T[] scIden;
	T[] scPos;
	boolean[] WorkingPos;
	public void readAndRemove(boolean[] pos) throws Exception {
		WorkingPos = pos;
		//prepare path
		PlainBlock[][] blocks = getPath(pos);
		randomPath = randomPath(blocks);
		scPath = preparePath(blocks, blocks, randomPath);
		
		//prepare newblock
		scIden = eva.inputOfAlice(new boolean[lengthOfIden]);
		
		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		Block<T> res2 = lib.readAndRemove(scStash[0], scIden);
		res = lib.mux(res, res2, res.isDummy);
		PlainBlock b = randomBlock();
		Block<T> scb = inputBlockOfClient(b);
		Block<T>finalRes = lib.mux(res, scb, res.isDummy);

		
		outputBlock(finalRes);
	}
	
	public void putBack() throws Exception {
		boolean[] pos = WorkingPos;
		scPos = eva.inputOfAlice(new boolean[lengthOfPos]);
		T[] scData = eva.inputOfAlice(new boolean[lengthOfData]);
		
		Block<T> scNewBlock = new Block<T>(scIden, scPos, scData, lib.SIGNAL_ZERO);
		
		lib.add(scStash[0], scNewBlock);
		lib.pushDown(scPath[0], scStash[0], pos);
		
		
		PlainBlock[][] blocks = randomPath;
		preparePlainPath(scPath[0], scPath[1]);
		putPath(blocks, pos);
	}
	
	public void access(int pos) throws Exception {
		readAndRemove(Utils.fromInt(pos, lengthOfPos));
		putBack();
	}
	
	public void access(boolean[] pos) throws Exception {
		access(Utils.toInt(pos));
	}
}
