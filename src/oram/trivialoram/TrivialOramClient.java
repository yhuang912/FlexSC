package oram.trivialoram;

import java.io.InputStream;
import java.io.OutputStream;
import flexsc.*;
import oram.Block;
import oram.OramParty;
import test.Utils;

public class TrivialOramClient<T> extends OramParty<T> {
	BlockInBinary[] bucket;
	int capacity;
	public TrivialOramClient(InputStream is, OutputStream os, int N,
			int dataSize, Mode m) throws Exception {
		super(is, os, N, dataSize, Party.Alice, 1, m);
		this.capacity = N;
		bucket = new BlockInBinary[capacity];
		
		for(int i = 0; i < bucket.length; ++i){
			bucket[i] = getDummyBlock();
		}
		Block<T>[][] result = prepareBlocks(bucket, bucket, bucket);
		bucket = prepareBlockInBinaries(result[0], result[1]);
	}
	
	public void add(BlockInBinary b) throws Exception {
		Block<T>[][] result = prepareBlocks(bucket, bucket, bucket);
		Block<T>[] scBlocks = result[0];
		Block<T>[] scBlocksMask = result[1];		
		Block<T> scNewBlock = inputBlockOfClient(b);
		lib.add(scBlocks, scNewBlock);
		bucket = prepareBlockInBinaries(scBlocks, scBlocksMask);
	}
	
	public BlockInBinary pop() throws Exception{
		Block<T>[][] result = prepareBlocks(bucket, bucket, bucket);
		Block<T>[] scBlocks = result[0];
		Block<T>[] scBlocksMask = result[1]; 
		
		Block<T> res = lib.pop(scBlocks);
		
		bucket = prepareBlockInBinaries(scBlocks, scBlocksMask);
		BlockInBinary r =  outputBlock(res);
		return r;
	}
	
	public BlockInBinary readAndRemove(boolean [] iden) throws Exception {
		Block<T>[][] result = prepareBlocks(bucket, bucket, bucket);
		Block<T>[] scBlocks = result[0];
		Block<T>[] scBlocksMask = result[1];
		T[] scIden = gen.inputOfGen(iden);
		
		Block<T> res = lib.readAndRemove(scBlocks, scIden);
		BlockInBinary b = randomBlock();
		Block<T> scb = inputBlockOfClient(b);
		Block<T>finalRes = lib.mux(res, scb, res.isDummy);

		bucket = prepareBlockInBinaries(scBlocks, scBlocksMask);
		BlockInBinary r = outputBlock(finalRes);
		//System.out.print(Utils.toInt(r.iden)+" "+r.isDummy);
		return r;
	}
	
	public boolean[] read(int iden) throws Exception {
		boolean[] r = readAndRemove(iden);
		putBack(iden, r);
		return r;
	}
	
	public void write(int iden, boolean[] b) throws Exception {
		readAndRemove(iden);
		putBack(iden, b);
	}
	
	public boolean[] readAndRemove(int iden) throws Exception {
		return readAndRemove(Utils.fromInt(iden, lengthOfIden)).data;
	}
	
	public void putBack(int iden, boolean[] data) throws Exception{
		add(new BlockInBinary(Utils.fromInt(iden, lengthOfIden), new boolean[]{true}, data, false));
	}
	
	

}
