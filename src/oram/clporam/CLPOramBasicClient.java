package oram.clporam;

import java.io.InputStream;
import java.io.OutputStream;

import oram.Block;
import oram.PlainBlock;
import test.Utils;
import flexsc.*;

public class CLPOramBasicClient<T> extends CLPOramParty<T> {
	CLPOramLib<T> lib;
	public CLPOramBasicClient(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int nodeCapacity, int leafCapacity, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, nodeCapacity, leafCapacity, m, sp);
		lib = new CLPOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, nodeCapacity, leafCapacity, gen);
		scQueue = prepareBlocks(queue, queue, queue);
	}
	

	public void dequeue() throws Exception {
		//Block[][] scQueue = prepareBlocks(queue, queue, queue);
		Block<T>[][] scTree1 = prepareBlocks(tree[1], tree[1], tree[1]);
		
		Block<T> b = lib.pop(scQueue[0]);
		lib.add(scTree1[0], b);
		
		tree[1] = preparePlainBlocks(scTree1[0], scTree1[1]);
		
		flush();
	}

	@Override
	public void flushOneTime(boolean[] pos) throws Exception {
		T[] pathSignal =  gen.newTArray(pos.length);//new T[pos.length];
		for(int i = 0; i < pos.length; ++i)
			pathSignal[i] = pos[i] ? lib.SIGNAL_ONE : lib.SIGNAL_ZERO;
		
		int index = 1;
		Block<T> transit = lib.dummyBlock;
		Block<T>[] overflowedBlocks = new Block[tempStashSize];
		for(int i = 0; i < tempStashSize; ++i)
			overflowedBlocks[i] = lib.dummyBlock;
		
		for(int i = 1; i < logN; ++i) {
			PlainBlock[] top = tree[index];
			Block<T>[][] scTop = prepareBlocks(top, top, top);
			
			transit = lib.flushUnit(scTop[0], transit, i, pathSignal, overflowedBlocks);
			
			tree[index] = preparePlainBlocks(scTop[0], scTop[1]);
			index*=2;
			if(pos[lengthOfPos-i])
				++index;
		}
		PlainBlock[] top = tree[index];
		Block<T>[][] scTop = prepareBlocks(top, top, top);
		//lib.flushUnit(scTop[0], transit, logN, pathSignal, overflowedBlocks);
		lib.add(scTop[0], transit);
		tree[index] = preparePlainBlocks(scTop[0], scTop[1]);
				
		for(int i = 0; i < tempStashSize; ++i)
			lib.add(scQueue[0], overflowedBlocks[i]);
	}
	
	Block<T>[][] scQueue;
	T[] scIden;
	public boolean[] readAndRemove(boolean[] iden, boolean[] pos) throws Exception {
		PlainBlock[][] blocks = getPath(pos);
		Block<T>[][][] scPath = preparePath(blocks, blocks, blocks);

		scIden = gen.inputOfAlice(iden);
		
		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		Block<T> res2 = lib.readAndRemove(scQueue[0], scIden);
		res = lib.mux(res, res2, res.isDummy);
		PlainBlock b = randomBlock();
		Block<T> scb = inputBlockOfClient(b);
		Block<T>finalRes = lib.mux(res, scb, res.isDummy);

		blocks = preparePlainPath(scPath[0], scPath[1]);
		putPath(blocks, pos);
		
		
		PlainBlock r = outputBlock(finalRes);
		return r.data;
	}
	
	public void putBack(boolean[] iden, boolean[] newPos, boolean[] data) throws Exception {
		T[] scNewPos = gen.inputOfAlice(newPos);
		T[] scData = gen.inputOfAlice(data);
		Block<T> b = new Block<T>(scIden, scNewPos, scData, lib.SIGNAL_ZERO);

		lib.add(scQueue[0], b);
		
		dequeue();
		dequeue();
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
