package oramgc.pathoram;

import gc.GCSignal;

import java.io.InputStream;
import java.io.OutputStream;

import oramgc.Block;
import oramgc.OramParty.BlockInBinary;
import test.Utils;


public class PathOramClient extends PathOramParty {
	PathOramLib lib;
	public PathOramClient(InputStream is, OutputStream os, int N, int dataSize,
			Party p) throws Exception {
		super(is, os, N, dataSize, p);
		lib = new PathOramLib(lengthOfIden, lengthOfPos, lengthOfData, logN, gen);
	}
	
	public BlockInBinary access(boolean[] iden, boolean[] pos, boolean[] newPos, boolean[] data) throws Exception {
		BlockInBinary[] blocks = flatten(getAPath(pos));
		Block[][] scPath = prepareBlocks(blocks, blocks, blocks);
		Block[][] scStash = prepareBlocks(stash, stash, stash);
		GCSignal[] scIden = gen.inputOfGen(iden);
		GCSignal[] scPos = gen.inputOfGen(newPos);
		
		
		Block res = lib.readAndRemove(scPath[0], scIden);

		BlockInBinary r =  outputBlock(res);
		
		GCSignal[] scData = res.data;
		if(data != null)
			scData = gen.inputOfGen(data);
		
		Block scNewBlock = new Block(scIden, scPos, scData, lib.SIGNAL_ZERO);
		
		lib.add(scStash[0], scNewBlock);
		
		//Signal[][] debug = 
		lib.pushDown(scPath[0], scStash[0], pos);
		
		blocks = prepareBlockInBinaries(scPath[0], scPath[1]);
		stash = prepareBlockInBinaries(scStash[0], scStash[1]);
		putAPath(blocks, pos);
		
		return r;
	}
	GCSignal[] scIden;
	Block[][] scStash;
	Block[][] scPath;
	boolean[] workingPos;
	public BlockInBinary readAndRemove(boolean[] iden, boolean[] pos) throws Exception {
		workingPos = pos;
		BlockInBinary[] blocks = flatten(getAPath(pos));
		scPath = prepareBlocks(blocks, blocks, blocks);
		scStash = prepareBlocks(stash, stash, stash);
		scIden = gen.inputOfGen(iden);
		
		Block res = lib.readAndRemove(scPath[0], scIden);

		BlockInBinary r =  outputBlock(res);
		return r;
	}
	
	public void putBack(boolean[] iden, boolean[] newPos, boolean[] data) throws Exception {
		GCSignal[] scPos = gen.inputOfGen(newPos);
		GCSignal[] scData = gen.inputOfGen(data);
		boolean[] pos = workingPos;
		Block scNewBlock = new Block(scIden, scPos, scData, lib.SIGNAL_ZERO);
		
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
