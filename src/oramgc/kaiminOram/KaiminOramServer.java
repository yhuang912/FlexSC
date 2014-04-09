package oramgc.kaiminOram;

import gc.Signal;

import java.io.InputStream;
import java.io.OutputStream;

import oramgc.Block;
import oramgc.OramParty.BlockInBinary;
import oramgc.OramParty.Party;
import oramgc.treeoram.TreeOramLib;
import test.Utils;


public class KaiminOramServer extends KaiminOramParty {
	TreeOramLib lib;
	public KaiminOramServer(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int capacity) throws Exception {
		super(is, os, N, dataSize, p, capacity);
		lib = new TreeOramLib(lengthOfIden, lengthOfPos, lengthOfData, logN, capacity, eva);
	}
	
	public void fetch(boolean[] pos, boolean[] data) throws Exception{
		BlockInBinary[] blocks = flatten(getAPath(pos));
		BlockInBinary[] randomPath = randomBucket(blocks.length);
		BlockInBinary[] randomQueue = randomBucket(queueCapacity);
		Block[][] scPath = prepareBlocks(blocks, blocks, randomPath);
		Block[][] scQueue = prepareBlocks(queue, queue, randomQueue);
		Signal[] scIden = eva.inputOfGen(new boolean[lengthOfIden]);
		Signal[] scNewPos = eva.inputOfGen(new boolean[lengthOfPos]);
		Signal[] scData = null;
		if(data != null)
			scData = eva.inputOfGen(new boolean[lengthOfData]);
		
		Block res = lib.readAndRemove(scPath[0], scIden);
		Block res2 = lib.readAndRemove(scQueue[0], scIden);
		Block finalRes = lib.mux(res, res2, lib.eq(res.iden, lib.zeros(lengthOfIden)));

		if(data == null)
			scData = finalRes.data;
		
		Block b = new Block(scIden, scNewPos, scData);

		lib.add(scQueue[0], b);
		blocks = randomPath;
		queue = randomQueue;
		prepareBlockInBinaries(scPath[0], scPath[1]);
		prepareBlockInBinaries(scQueue[0], scQueue[1]);
		outputBlock(b);
		putAPath(blocks, pos);
	} 


	public void dequeue() throws Exception{
		BlockInBinary[] randomQueue = randomBucket(queueCapacity);
		BlockInBinary[] randomBucket = randomBucket(capacity);
		Block[][] scQueue = prepareBlocks(queue, queue, randomQueue);
		Block[][] scTree1 = prepareBlocks(tree[1], tree[1], randomBucket);
		
		Block b = lib.pop(scQueue[0]);
		lib.add(scTree1[0], b);
		
		queue = randomQueue;
		tree[1] = randomBucket;
		prepareBlockInBinaries(scQueue[0], scQueue[1]);
		prepareBlockInBinaries(scTree1[0], scTree1[1]);
		
		flush();
	}

	@Override
	public void flushOneTime(boolean[] pos) throws Exception {
		int index = 1;
		for(int i = 1; i < logN; ++i){
			BlockInBinary[] top = tree[index];
			BlockInBinary[] left = tree[index*2];
			BlockInBinary[] right = tree[index*2+1];
			
			BlockInBinary[] randomTop = randomBucket(capacity);
			BlockInBinary[] randomLeft = randomBucket(capacity);
			BlockInBinary[] randomRight = randomBucket(capacity);
			
			Block[][] scTop = prepareBlocks(top, top, randomTop);
			Block[][] scLeft = prepareBlocks(left, left, randomLeft);
			Block[][] scRight = prepareBlocks(right, right, randomRight);
			
			lib.evitUnit(scTop[0], scLeft[0], scRight[0], i);

			tree[index] = randomTop;
			prepareBlockInBinaries(scTop[0], scTop[1]);
			tree[index*2] = randomLeft;
			prepareBlockInBinaries(scLeft[0], scLeft[1]);
			tree[index*2+1] = randomRight;
			prepareBlockInBinaries(scRight[0], scRight[1]);
			
			index*=2;
			if(pos[i-1])
				++index;
		}
	}
	
	public void read(int pos) throws Exception {
		read(Utils.fromInt(pos, lengthOfPos));
	}

	public void write(int pos) throws Exception {
		write(Utils.fromInt(pos, lengthOfPos));
	}

	public void read(boolean[] pos) throws Exception {
		fetch(pos, null);
		dequeue();
		dequeue();
	}
	
	public void write(boolean[] pos) throws Exception {
		fetch(pos, new boolean[lengthOfData]);
		dequeue();
		dequeue();
	}

}
