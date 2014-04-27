package oramgc.kaiminOram;

import gc.GCSignal;

import java.io.InputStream;
import java.io.OutputStream;

import oramgc.Block;
import test.Utils;

public class KaiminOramClient extends KaiminOramParty {
	KaiminOramLib lib;
	public KaiminOramClient(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int nodeCapacity, int leafCapacity) throws Exception {
		super(is, os, N, dataSize, p, nodeCapacity, leafCapacity);
		lib = new KaiminOramLib(lengthOfIden, lengthOfPos, lengthOfData, logN, nodeCapacity, leafCapacity, gen);
	}
	
	public BlockInBinary fetch(boolean[] iden, boolean[] pos, boolean[] newPos, boolean[] data) throws Exception{
		BlockInBinary[] blocks = flatten(getAPath(pos));
		Block[][] scPath = prepareBlocks(blocks, blocks, blocks);
		Block[][] scQueue = prepareBlocks(queue, queue, queue);
		GCSignal[] scIden = gen.inputOfGen(iden);
		GCSignal[] scNewPos = gen.inputOfGen(newPos);
		GCSignal[] scData = null;
		if(data != null)
			scData = gen.inputOfGen(data);
		
		Block res = lib.readAndRemove(scPath[0], scIden);
		Block res2 = lib.readAndRemove(scQueue[0], scIden);
		Block finalRes = lib.mux(res, res2, res.isDummy);

		if(data == null)
			scData = finalRes.data;
		
		Block b = new Block(scIden, scNewPos, scData, lib.SIGNAL_ZERO);

		lib.add(scQueue[0], b);

		blocks = prepareBlockInBinaries(scPath[0], scPath[1]);
		queue = prepareBlockInBinaries(scQueue[0], scQueue[1]);
		BlockInBinary r = outputBlock(b);
		putAPath(blocks, pos);
		
		return r;
	} 

	public void dequeue() throws Exception{
		//Block[][] scQueue = prepareBlocks(queue, queue, queue);
		Block[][] scTree1 = prepareBlocks(tree[1], tree[1], tree[1]);
		
		Block b = lib.pop(scQueue[0]);
		lib.add(scTree1[0], b);
		
		//queue = prepareBlockInBinaries(scQueue[0], scQueue[1]);
		tree[1] = prepareBlockInBinaries(scTree1[0], scTree1[1]);
		
		flush();
	}

	@Override
	public void flushOneTime(boolean[] pos) throws Exception {
		GCSignal[] pathSignal = new GCSignal[pos.length];
		for(int i = 0; i < pos.length; ++i)
			pathSignal[i] = pos[i] ? lib.SIGNAL_ONE : lib.SIGNAL_ZERO;
		
		int index = 1;
		Block transit = lib.dummyBlock;
		Block[] overflowedBlocks = new Block[tempStashSize];
		for(int i = 0; i < tempStashSize; ++i)
			overflowedBlocks[i] = lib.dummyBlock;
		
		for(int i = 1; i < logN; ++i) {
			BlockInBinary[] top = tree[index];
			Block[][] scTop = prepareBlocks(top, top, top);
			
			transit = lib.flushUnit(scTop[0], transit, i, pathSignal, overflowedBlocks);
			
			tree[index] = prepareBlockInBinaries(scTop[0], scTop[1]);
			index*=2;
			if(pos[lengthOfPos-i])
				++index;
		}
		//debug(overflowedBlocks);
		BlockInBinary[] top = tree[index];
		Block[][] scTop = prepareBlocks(top, top, top);
		lib.add(scTop[0], transit);
		tree[index] = prepareBlockInBinaries(scTop[0], scTop[1]);
		
		Block[][] scQueue = prepareBlocks(queue, queue, queue);
		
		if(DEBUG) {//veridy queue is not full
			GCSignal full = lib.SIGNAL_ONE;
			for(int i = 0; i <scQueue[0].length; ++i){
				full = lib.and(full, lib.not(lib.eq(scQueue[0][i].iden, lib.zeros(lengthOfIden)) ));
			}
			boolean fullb = gen.outputToGen(full);
			if(fullb)
				System.out.println("queue Full!!");
		}
		
		for(int i = 0; i < tempStashSize; ++i)
			lib.add(scQueue[0], overflowedBlocks[i]);
		queue = prepareBlockInBinaries(scQueue[0], scQueue[1]);
	}
	
	Block[][] scQueue;
	GCSignal[] scIden;
	public boolean[] readAndRemove(boolean[] iden, boolean[] pos) throws Exception {
		BlockInBinary[] blocks = flatten(getAPath(pos));
		Block[][] scPath = prepareBlocks(blocks, blocks, blocks);
		scQueue = prepareBlocks(queue, queue, queue);
		scIden = gen.inputOfGen(iden);
		
		Block res = lib.readAndRemove(scPath[0], scIden);
		Block res2 = lib.readAndRemove(scQueue[0], scIden);
		Block finalRes = lib.mux(res, res2, res.isDummy);

		blocks = prepareBlockInBinaries(scPath[0], scPath[1]);
		
		putAPath(blocks, pos);
		
		BlockInBinary r = outputBlock(finalRes);
		return r.data;
	}
	
	public void putBack(boolean[] iden, boolean[] newPos, boolean[] data) throws Exception {
		GCSignal[] scNewPos = gen.inputOfGen(newPos);
		GCSignal[] scData = gen.inputOfGen(data);
		Block b = new Block(scIden, scNewPos, scData, lib.SIGNAL_ZERO);

		lib.add(scQueue[0], b);
		
		dequeue();
		dequeue();
		queue = prepareBlockInBinaries(scQueue[0], scQueue[1]);
	}
	
	public boolean[] readAndRemove(int iden, boolean[] pos) throws Exception {
		return readAndRemove(Utils.fromInt(iden, lengthOfIden), pos);
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
