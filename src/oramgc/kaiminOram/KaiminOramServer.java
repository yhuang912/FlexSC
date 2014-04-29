package oramgc.kaiminOram;

import java.io.InputStream;
import java.io.OutputStream;
import oramgc.Block;
import oramgc.OramParty.BlockInBinary;
import test.Utils;
import flexsc.*;


public class KaiminOramServer<T> extends KaiminOramParty<T> {
	KaiminOramLib<T> lib;
	public KaiminOramServer(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int nodeCapacity, int leafCapacity, Mode m) throws Exception {
		super(is, os, N, dataSize, p, nodeCapacity, leafCapacity, m);
		lib = new KaiminOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, nodeCapacity, leafCapacity, eva);
	}
	
	public void fetch(boolean[] pos, boolean[] data) throws Exception{
		BlockInBinary[] blocks = flatten(getAPath(pos));
		BlockInBinary[] randomPath = randomBucket(blocks.length);
		BlockInBinary[] randomQueue = randomBucket(queueCapacity);
		Block<T>[][] scPath = prepareBlocks(blocks, blocks, randomPath);
		Block<T>[][] scQueue = prepareBlocks(queue, queue, randomQueue);
		T[] scIden = eva.inputOfGen(new boolean[lengthOfIden]);
		T[] scNewPos = eva.inputOfGen(new boolean[lengthOfPos]);
		T[] scData = null;
		if(data != null)
			scData = eva.inputOfGen(new boolean[lengthOfData]);
		
		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		Block<T> res2 = lib.readAndRemove(scQueue[0], scIden);
		Block<T> finalRes = lib.mux(res, res2, res.isDummy);
		
		if(data == null)
			scData = finalRes.data;
		
		Block<T> b = new Block<T>(scIden, scNewPos, scData, lib.SIGNAL_ZERO);

		lib.add(scQueue[0], b);
		blocks = randomPath;
		queue = randomQueue;
		prepareBlockInBinaries(scPath[0], scPath[1]);
		prepareBlockInBinaries(scQueue[0], scQueue[1]);
		outputBlock(b);
		putAPath(blocks, pos);
	} 


	public void dequeue() throws Exception{
		//BlockInBinary[] randomQueue = randomBucket(queueCapacity);
		BlockInBinary[] randomBucket = randomBucket(nodeCapacity);
		//Block[][] scQueue = prepareBlocks(queue, queue, randomQueue);
		Block<T>[][] scTree1 = prepareBlocks(tree[1], tree[1], randomBucket);
		
		Block<T> b = lib.pop(scQueue[0]);
		lib.add(scTree1[0], b);
		
		//queue = randomQueue;
		tree[1] = randomBucket;
		//prepareBlockInBinaries(scQueue[0], scQueue[1]);
		prepareBlockInBinaries(scTree1[0], scTree1[1]);
		
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
		for(int i = 1; i < logN; ++i){
			BlockInBinary[] top = tree[index];
			BlockInBinary[] randomTop = randomBucket(nodeCapacity);
			//System.out.println(top.length);
			Block<T>[][] scTop = prepareBlocks(top, top, randomTop);

			transit = lib.flushUnit(scTop[0], transit, i, pathSignal, overflowedBlocks);
			tree[index] = randomTop;
			prepareBlockInBinaries(scTop[0], scTop[1]);
			
			index*=2;
			if(pos[lengthOfPos-i])
				++index;
		}
		//debug(overflowedBlocks);
		BlockInBinary[] top = tree[index];
		BlockInBinary[] randomTop = randomBucket(leafCapacity);
		
		Block<T>[][] scTop = prepareBlocks(top, top, randomTop);
		lib.add(scTop[0], transit);
		tree[index] = randomTop;
		prepareBlockInBinaries(scTop[0], scTop[1]);
		
		BlockInBinary[] randomQueue = randomBucket(queueCapacity);
		Block<T>[][] scQueue = prepareBlocks(queue, queue, randomQueue);
		
		if(DEBUG) {//veridy queue is not full
			T full = lib.SIGNAL_ONE;
			for(int i = 0; i <scQueue[0].length; ++i){
				full = lib.and(full, lib.not(lib.eq(scQueue[0][i].iden, lib.zeros(lengthOfIden)) ));
			}
			eva.outputToGen(full);
		}
		for(int i = 0; i < tempStashSize; ++i)
			lib.add(scQueue[0], overflowedBlocks[i]);
		queue = randomQueue;
		prepareBlockInBinaries(scQueue[0], scQueue[1]);
	}
	
	Block<T>[][] scQueue;
	T[] scIden;
	BlockInBinary[] randomQueue;
	public void readAndRemove(boolean[] pos) throws Exception {
		BlockInBinary[] blocks = flatten(getAPath(pos));
		BlockInBinary[] randomPath = randomBucket(blocks.length);
		randomQueue = randomBucket(queueCapacity);
		Block<T>[][] scPath = prepareBlocks(blocks, blocks, randomPath);
		scQueue = prepareBlocks(queue, queue, randomQueue);
		scIden = eva.inputOfGen(new boolean[lengthOfIden]);
		
		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		Block<T> res2 = lib.readAndRemove(scQueue[0], scIden);
		Block<T> finalRes = lib.mux(res, res2, res.isDummy);
		
		blocks = randomPath;
		prepareBlockInBinaries(scPath[0], scPath[1]);
		putAPath(blocks, pos);
		outputBlock(finalRes);
	}
	
	public void putBack() throws Exception {		
		T[] scNewPos = eva.inputOfGen(new boolean[lengthOfPos]);
		T[] scData = eva.inputOfGen(new boolean[lengthOfData]);
				
		Block<T> b = new Block<T>(scIden, scNewPos, scData, lib.SIGNAL_ZERO);

		lib.add(scQueue[0], b);
		
		dequeue();
		dequeue();
		queue = randomQueue;
		prepareBlockInBinaries(scQueue[0], scQueue[1]);
		//queue = prepareBlockInBinaries(scQueue[0], scQueue[1]);
	}	
	
	public void access(int pos) throws Exception {
		readAndRemove(Utils.fromInt(pos, lengthOfPos));
		putBack();
	}
	
	public void access(boolean[] pos) throws Exception {
		access(Utils.toInt(pos));
	}


}
