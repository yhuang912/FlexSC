package PrivateOram;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import oram.Block;
import oram.PlainBlock;
import flexsc.Mode;
import flexsc.Party;

public class CircuitOram<T> extends TreeBasedOramParty<T> {
	CircuitOramLib<T> lib;
	Block<T>[] scQueue;
	int cnt = 0;	
	public PlainBlock[] queue;
	public int queueCapacity;

	boolean[] nextPath()
	{
		boolean [] res = new boolean[logN];
		int temp = cnt;
		for(int i = res.length-1; i >= 0; --i) {
			res[i] = (temp&1)==1;
			temp>>=1;
		}
		cnt = (cnt+1)%N;
		return res;
	}


	public CircuitOram(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int cap, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, cap, m);
		lib = new CircuitOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, capacity, env);
		queueCapacity = 30;
		queue = new PlainBlock[queueCapacity];

		for(int i = 0; i < queue.length; ++i) 
			queue[i] = getDummyBlock(p == Party.Alice);

		scQueue = prepareBlocks(queue, queue);		

	}

	protected void ControlEviction() throws Exception {
		flushOneTime(nextPath());
		flushOneTime(nextPath());
	}


	public void flushOneTime(boolean[] pos) throws Exception {
		PlainBlock[][] blocks = getPath(pos);
		Block<T>[][] scPath = preparePath(blocks, blocks);

		lib.flush(scPath, pos, scQueue);

		blocks = preparePlainPath(scPath);
		putPath(blocks, pos);
	}


	public T[] readAndRemove(T[] scIden, boolean[] pos, boolean RandomWhenNotFound) throws Exception {
		PlainBlock[][] blocks = getPath(pos);
		Block<T>[][] scPath = preparePath(blocks, blocks);


		Block<T> res = lib.readAndRemove(scPath, scIden);
		Block<T> res2 = lib.readAndRemove(scQueue, scIden);
		res = lib.mux(res, res2, res.isDummy);
		
		blocks = preparePlainPath(scPath);
		putPath(blocks, pos);

		if(RandomWhenNotFound) {
			PlainBlock b = randomBlock();	
			Block<T> scb = inputBlockOfClient(b);
			Block<T>finalRes = lib.mux(res, scb, res.isDummy);

			return finalRes.data;
		}
		else{
			return lib.mux(res.data, lib.zeros(res.data.length),res.isDummy);
		}
	}


	public void putBack(T[] scIden, T[] scNewPos, T[] scData) throws Exception {
		Block<T> b = new Block<T>(scIden, scNewPos, scData, lib.SIGNAL_ZERO);
		lib.add(scQueue, b);

		os.flush();
		ControlEviction();
	}

	public T[] read(T[] scIden, boolean[] pos, T[] scNewPos) throws Exception {
		scIden = Arrays.copyOf(scIden, lengthOfIden);
		T[] r = readAndRemove(scIden, pos, false);
		putBack(scIden, scNewPos, r);
		return r;
	}
	
	public void write(T[] scIden, boolean[] pos, T[] scNewPos, T[] scData) throws Exception {
		scIden = Arrays.copyOf(scIden, lengthOfIden);
		readAndRemove(scIden, pos, true);
		putBack(scIden, scNewPos, scData);
	}

}
