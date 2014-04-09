package oramgc.kaiminOram;

import gc.Signal;

import java.io.InputStream;
import java.io.OutputStream;

import oramgc.Block;
import oramgc.treeoram.TreeOramLib;
import test.Utils;

public class KaiminOramClient extends KaiminOramParty {
	TreeOramLib lib;
	public KaiminOramClient(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int capacity) throws Exception {
		super(is, os, N, dataSize, p, capacity);
		lib = new TreeOramLib(lengthOfIden, lengthOfPos, lengthOfData, logN, capacity, gen);
	}
	
	public BlockInBinary fetch(boolean[] iden, boolean[] pos, boolean[] newPos, boolean[] data) throws Exception{
		BlockInBinary[] blocks = flatten(getAPath(pos));
		Block[][] scPath = prepareBlocks(blocks, blocks, blocks);
		Block[][] scQueue = prepareBlocks(queue, queue, queue);
		Signal[] scIden = gen.inputOfGen(iden);
		Signal[] scNewPos = gen.inputOfGen(newPos);
		Signal[] scData = null;
		if(data != null)
			scData = gen.inputOfGen(data);
		
		Block res = lib.readAndRemove(scPath[0], scIden);
		Block res2 = lib.readAndRemove(scQueue[0], scIden);
		Block finalRes = lib.mux(res, res2, lib.eq(res.iden, lib.zeros(lengthOfIden)));

		if(data == null)
			scData = finalRes.data;
		
		Block b = new Block(scIden, scNewPos, scData);

		lib.add(scQueue[0], b);

		blocks = prepareBlockInBinaries(scPath[0], scPath[1]);
		queue = prepareBlockInBinaries(scQueue[0], scQueue[1]);
		BlockInBinary r = outputBlock(b);
		putAPath(blocks, pos);
		
		return r;
	} 

	public void dequeue() throws Exception{
		Block[][] scQueue = prepareBlocks(queue, queue, queue);
		Block[][] scTree1 = prepareBlocks(tree[1], tree[1], tree[1]);
		
		Block b = lib.pop(scQueue[0]);
		lib.add(scTree1[0], b);
		
		queue = prepareBlockInBinaries(scQueue[0], scQueue[1]);
		tree[1] = prepareBlockInBinaries(scTree1[0], scTree1[1]);
		
		flush();
	}

	@Override
	public void flushOneTime(boolean[] pos) throws Exception {
		int index = 1;
		for(int i = 1; i < logN; ++i) {
			BlockInBinary[] top = tree[index];
			BlockInBinary[] left = tree[index*2];
			BlockInBinary[] right = tree[index*2+1];
			
			Block[][] scTop = prepareBlocks(top, top, top);
			Block[][] scLeft = prepareBlocks(left, left, left);
			Block[][] scRight = prepareBlocks(right, right, right);
			
			lib.evitUnit(scTop[0], scLeft[0], scRight[0], i);

			tree[index] = prepareBlockInBinaries(scTop[0], scTop[1]);
			tree[index*2] = prepareBlockInBinaries(scLeft[0], scLeft[1]);
			tree[index*2+1] = prepareBlockInBinaries(scRight[0], scRight[1]);
			index*=2;
			if(pos[i-1])
				++index;
		}
	}
	
	public BlockInBinary read(int iden, int pos, int newPos) throws Exception {
		return read(Utils.fromInt(iden, lengthOfIden), Utils.fromInt(pos, lengthOfPos), Utils.fromInt(newPos, lengthOfPos));
	}

	public void write(int iden, int pos, int newPos, boolean[] data) throws Exception {
		write(Utils.fromInt(iden, lengthOfIden), Utils.fromInt(pos, lengthOfPos), Utils.fromInt(newPos, lengthOfPos),data);
	}

	public BlockInBinary read(boolean[] iden, boolean[] pos, boolean[] newPos) throws Exception {
		BlockInBinary result = fetch(iden, pos, newPos, null);
		dequeue();
		dequeue();
		return result;
	}
	
	public void write(boolean[] iden, boolean[] pos, boolean[] newPos, boolean[] data) throws Exception {
		fetch(iden, pos, newPos, data);
		dequeue();
		dequeue();
	}

}
