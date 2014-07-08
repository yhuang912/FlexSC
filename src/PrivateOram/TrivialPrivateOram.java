package PrivateOram;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import oram.Block;
import flexsc.Mode;
import flexsc.Party;

public class TrivialPrivateOram<T> extends OramParty<T> {
	public PlainBlock[] bucket;
	Block<T>[] result;
	int capacity;
	public TrivialPrivateOram(InputStream is, OutputStream os, int N,
			int dataSize, Mode m, Party p) throws Exception {
		super(is, os, N, dataSize, p, 1, m);
		this.capacity = N;
		bucket = new PlainBlock[capacity];
		
		for(int i = 0; i < bucket.length; ++i){
			bucket[i] = getDummyBlock(true);
		}
		result = prepareBlocks(bucket, bucket);
	}
	
	public void add(T[] iden, T[] data) throws Exception {	
		T[] pos = env.newTArray(1);
		pos[0] = lib.SIGNAL_ONE;
		Block<T> scNewBlock = new Block<T>(iden, pos, data, lib.SIGNAL_ZERO);
		lib.add(result, scNewBlock);
	}
	
	public T[] readAndRemove(T[] scIden) throws Exception {		
		Block<T> res = lib.readAndRemove(result, scIden);
		PlainBlock b = randomBlock();
		Block<T> scb = inputBlockOfClient(b);
		Block<T>finalRes = lib.mux(res, scb, res.isDummy);

		return finalRes.data;
	}
	
	public T[] read(T[] scIden) throws Exception {
		scIden = Arrays.copyOf(scIden, lengthOfIden);
		T[] r = readAndRemove(scIden);
		putBack(scIden, r);
		return r;
	}
	
	public void write(T[] scIden, T[] b) throws Exception {
		scIden = Arrays.copyOf(scIden, lengthOfIden);
		readAndRemove(scIden);
		putBack(scIden, b);
	}
	
	public void putBack(T[] scIden, T[] scData) throws Exception{
		scIden = Arrays.copyOf(scIden, lengthOfIden);
		add(scIden, scData);
	}

}
