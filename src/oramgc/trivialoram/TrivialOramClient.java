package oramgc.trivialoram;

import gc.GCSignal;

import java.io.InputStream;
import java.io.OutputStream;

import oramgc.Block;
import oramgc.OramParty;
import test.Utils;

public class TrivialOramClient extends OramParty {
	BlockInBinary[] bucket;
	int capacity;
	public TrivialOramClient(InputStream is, OutputStream os, int N,
			int dataSize) throws Exception {
		super(is, os, N, dataSize, OramParty.Party.CLIENT, 1);
		this.capacity = N;
		bucket = new BlockInBinary[capacity];
		
		for(int i = 0; i < bucket.length; ++i){
			bucket[i] = getDummyBlock();
		}
		Block[][] result = prepareBlocks(bucket, bucket, bucket);
		bucket = prepareBlockInBinaries(result[0], result[1]);
	}
	
	public void add(BlockInBinary b) throws Exception {
		Block[][] result = prepareBlocks(bucket, bucket, bucket);
		Block[] scBlocks = result[0];
		Block[] scBlocksMask = result[1];		
		Block scNewBlock = inputBlockOfClient(b);
		lib.add(scBlocks, scNewBlock);
		bucket = prepareBlockInBinaries(scBlocks, scBlocksMask);
	}
	
	public BlockInBinary pop() throws Exception{
		Block[][] result = prepareBlocks(bucket, bucket, bucket);
		Block[] scBlocks = result[0];
		Block[] scBlocksMask = result[1]; 
		
		Block res = lib.pop(scBlocks);
		
		bucket = prepareBlockInBinaries(scBlocks, scBlocksMask);
		BlockInBinary r =  outputBlock(res);
		return r;
	}
	
	public BlockInBinary readAndRemove(boolean [] iden) throws Exception {
		Block[][] result = prepareBlocks(bucket, bucket, bucket);
		Block[] scBlocks = result[0];
		Block[] scBlocksMask = result[1];
		GCSignal[] scIden = gen.inputOfGen(iden);
		
		Block res = lib.readAndRemove(scBlocks, scIden);

		bucket = prepareBlockInBinaries(scBlocks, scBlocksMask);
		BlockInBinary r = outputBlock(res);
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
