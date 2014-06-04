package oram.clporam;

import java.io.InputStream;
import java.io.OutputStream;

import oram.Block;
import oram.PlainBlock;
import test.Utils;
import flexsc.*;


public class CLPOramBasicServer<T> extends CLPOramParty<T> {
	CLPOramLib<T> lib;
	public CLPOramBasicServer(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int nodeCapacity, int leafCapacity, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, nodeCapacity, leafCapacity, m, sp);
		lib = new CLPOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, nodeCapacity, leafCapacity, eva);
		randomQueue = randomBucket(queueCapacity);
		scQueue = prepareBlocks(queue, queue, randomQueue);
	}

	public void dequeue() throws Exception{
		PlainBlock[] randomBucket = randomBucket(nodeCapacity);
		Block<T>[][] scTree1 = prepareBlocks(tree[1], tree[1], randomBucket);
		
		Block<T> b = lib.pop(scQueue[0]);
		lib.add(scTree1[0], b);
		
		tree[1] = randomBucket;
		preparePlainBlocks(scTree1[0], scTree1[1]);
		flush();
	}

	@Override
	public void flushOneTime(boolean[] pos) throws Exception {
		T[] pathSignal = eva.newTArray(pos.length);//new T[pos.length];
		for(int i = 0; i < pos.length; ++i)
			pathSignal[i] = pos[i] ? lib.SIGNAL_ONE : lib.SIGNAL_ZERO;
		
		int index = 1;
		Block<T> transit = lib.dummyBlock;
		Block<T>[] overflowedBlocks = new Block[tempStashSize];
		for(int i = 0; i < tempStashSize; ++i)
			overflowedBlocks[i] = lib.dummyBlock;
		
		for(int i = 1; i < logN; ++i) {
			PlainBlock[] top = tree[index];
			PlainBlock[] randomTop = randomBucket(nodeCapacity);
			Block<T>[][] scTop = prepareBlocks(top, top, randomTop);

			transit = lib.flushUnit(scTop[0], transit, i, pathSignal, overflowedBlocks);
			tree[index] = randomTop;
			preparePlainBlocks(scTop[0], scTop[1]);
			
			index*=2;
			if(pos[lengthOfPos-i])
				++index;
		}
		PlainBlock[] top = tree[index];
		PlainBlock[] randomTop = randomBucket(leafCapacity);
		
		Block<T>[][] scTop = prepareBlocks(top, top, randomTop);
		lib.add(scTop[0], transit);
		tree[index] = randomTop;
		preparePlainBlocks(scTop[0], scTop[1]);
		
		PlainBlock[] randomQueue = randomBucket(queueCapacity);
		
		for(int i = 0; i < tempStashSize; ++i)
			lib.add(scQueue[0], overflowedBlocks[i]);
	}
	
	Block<T>[][] scQueue;
	T[] scIden;
	PlainBlock[] randomQueue;
	public void readAndRemove(boolean[] pos) throws Exception {
		PlainBlock[][] blocks = getPath(pos);
		PlainBlock[][] ranPath = randomPath(blocks);
		Block<T>[][][] scPath = preparePath(blocks, blocks, ranPath);

		scIden = eva.inputOfAlice(new boolean[lengthOfIden]);
		
		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		Block<T> res2 = lib.readAndRemove(scQueue[0], scIden);
		res = lib.mux(res, res2, res.isDummy);
		PlainBlock b = randomBlock();
		Block<T> scb = inputBlockOfClient(b);
		Block<T>finalRes = lib.mux(res, scb, res.isDummy);
		
		preparePlainPath(scPath[0], scPath[1]);
		putPath(ranPath, pos);

		outputBlock(finalRes);
	}
	
	public void putBack() throws Exception {		
		T[] scNewPos = eva.inputOfAlice(new boolean[lengthOfPos]);
		T[] scData = eva.inputOfAlice(new boolean[lengthOfData]);
				
		Block<T> b = new Block<T>(scIden, scNewPos, scData, lib.SIGNAL_ZERO);

		lib.add(scQueue[0], b);
		
		dequeue();
		dequeue();
	}	
	
	public void access(int pos) throws Exception {
		readAndRemove(Utils.fromInt(pos, lengthOfPos));
		putBack();
	}
	
	public void access(boolean[] pos) throws Exception {
		access(Utils.toInt(pos));
	}

}
