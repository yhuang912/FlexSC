package oram.pathoramNaive;

import java.io.InputStream;
import java.io.OutputStream;
import flexsc.*;
import oram.Block;
import test.Utils;


public class PathOramClient<T> extends PathOramParty<T> {
	PathOramLib<T> lib;
	public PathOramClient(InputStream is, OutputStream os, int N, int dataSize,
			Party p, Mode m) throws Exception {
		super(is, os, N, dataSize, p, m);
		lib = new PathOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, gen);
	}
	
	public BlockInBinary access(boolean[] iden, boolean[] pos, boolean[] newPos, boolean[] data) throws Exception {
		BlockInBinary[] blocks = flatten(getAPath(pos));
		Block<T>[][] scPath = prepareBlocks(blocks, blocks, blocks);
		Block<T>[][] scStash = prepareBlocks(stash, stash, stash);
		T[] scIden = gen.inputOfAlice(iden);
		T[] scPos = gen.inputOfAlice(newPos);
		
		
		Block<T> res = lib.readAndRemove(scPath[0], scIden);

		BlockInBinary r =  outputBlock(res);
		
		T[] scData = res.data;
		if(data != null)
			scData = gen.inputOfAlice(data);
		
		Block<T> scNewBlock = new Block<T>(scIden, scPos, scData, lib.SIGNAL_ZERO);
		
		lib.add(scStash[0], scNewBlock);
		
		//Signal[][] debug = 
		lib.pushDown(scPath[0], scStash[0], pos);
		
		blocks = prepareBlockInBinaries(scPath[0], scPath[1]);
		stash = prepareBlockInBinaries(scStash[0], scStash[1]);
		putAPath(blocks, pos);
		
		return r;
	}
	T[] scIden;
	Block<T>[][] scStash;
	Block<T>[][] scPath;
	boolean[] workingPos;
	public BlockInBinary readAndRemove(boolean[] iden, boolean[] pos) throws Exception {
		workingPos = pos;
		BlockInBinary[] blocks = flatten(getAPath(pos));
		scPath = prepareBlocks(blocks, blocks, blocks);
		scStash = prepareBlocks(stash, stash, stash);
		scIden = gen.inputOfAlice(iden);
		
		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		Block<T> res2 = lib.readAndRemove(scStash[0], scIden);
		res = lib.mux(res, res2, res.isDummy);
		BlockInBinary b = randomBlock();
		Block<T> scb = inputBlockOfClient(b);
		Block<T>finalRes = lib.mux(res, scb, res.isDummy);

		BlockInBinary r =  outputBlock(finalRes);
		return r;
	}
	
	public void putBack(boolean[] iden, boolean[] newPos, boolean[] data) throws Exception {
		T[] scPos = gen.inputOfAlice(newPos);
		T[] scData = gen.inputOfAlice(data);
		boolean[] pos = workingPos;
		Block<T> scNewBlock = new Block<T>(scIden, scPos, scData, lib.SIGNAL_ZERO);
		
		lib.add(scStash[0], scNewBlock);
		
		//Signal[][] debug = 
		lib.pushDown(scPath[0], scStash[0], pos);
		
		BlockInBinary[] blocks = prepareBlockInBinaries(scPath[0], scPath[1]);
		stash = prepareBlockInBinaries(scStash[0], scStash[1]);
		putAPath(blocks, pos);
	}
	
	public boolean[] readAndRemove(int iden, boolean[] pos) throws Exception {
		return readAndRemove(Utils.fromInt(iden, lengthOfIden), pos).data;
	}
	
	public void putBack(int iden, boolean[] pos, boolean[] data) throws Exception {
		putBack(Utils.fromInt(iden, lengthOfIden), pos, data);
	}
	
	public boolean[] read(int iden, int pos, int newPos) throws Exception {
		return read(iden, Utils.fromInt(pos, lengthOfPos), Utils.fromInt(newPos, lengthOfPos));
	}
	
	public void write(int iden, int pos, int newPos, boolean[] data) throws Exception {
		write(iden, Utils.fromInt(pos, lengthOfPos), Utils.fromInt(newPos, lengthOfPos), data);
	}
	
	public boolean[] read(int iden, boolean[] pos, boolean[] newPos) throws Exception {
		boolean[] r = readAndRemove(iden, pos);
		putBack(iden, newPos, r);
		return r;
	}
	
	public void write(int iden, boolean[] pos, boolean[] newPos, boolean[] data) throws Exception {
		readAndRemove(iden, pos);
		putBack(iden, newPos, data);
	}

}
