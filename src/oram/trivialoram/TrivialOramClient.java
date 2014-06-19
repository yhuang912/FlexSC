package oram.trivialoram;

import java.io.InputStream;
import java.io.OutputStream;

import oram.Block;
import oram.OramParty;
import oram.PlainBlock;
import test.Utils;
import flexsc.Mode;
import flexsc.Party;

public class TrivialOramClient<T> extends OramParty<T> {
	public PlainBlock[] bucket;
	Block<T>[][] result;
	int capacity;
	public TrivialOramClient(InputStream is, OutputStream os, int N,
			int dataSize, Mode m) throws Exception {
		super(is, os, N, dataSize, Party.Alice, 1, m);
		this.capacity = N;
		bucket = new PlainBlock[capacity];
		
		for(int i = 0; i < bucket.length; ++i){
			bucket[i] = getDummyBlock(true);
		}
		result = prepareBlocks(bucket, bucket, bucket);
		//bucket = preparePlainBlocks(result[0], result[1]);
	}
	
	public void add(PlainBlock b) throws Exception {
		//Block<T>[][] result = prepareBlocks(bucket, bucket, bucket);
		Block<T>[] scBlocks = result[0];
		Block<T>[] scBlocksMask = result[1];		
		Block<T> scNewBlock = inputBlockOfClient(b);
		lib.add(scBlocks, scNewBlock);
		//bucket = preparePlainBlocks(scBlocks, scBlocksMask);
	}
	
	public PlainBlock pop() throws Exception{
		//Block<T>[][] result = prepareBlocks(bucket, bucket, bucket);
		Block<T>[] scBlocks = result[0];
		Block<T>[] scBlocksMask = result[1]; 
		
		Block<T> res = lib.pop(scBlocks);
		
		//bucket = preparePlainBlocks(scBlocks, scBlocksMask);
		PlainBlock r =  outputBlock(res);
		return r;
	}
	
	public PlainBlock readAndRemove(boolean [] iden) throws Exception {
		//Block<T>[][] result = prepareBlocks(bucket, bucket, bucket);
		Block<T>[] scBlocks = result[0];
		Block<T>[] scBlocksMask = result[1];
		T[] scIden = gen.inputOfAlice(iden);
		
		Block<T> res = lib.readAndRemove(scBlocks, scIden);
		PlainBlock b = randomBlock();
		Block<T> scb = inputBlockOfClient(b);
		Block<T>finalRes = lib.mux(res, scb, res.isDummy);

		//bucket = preparePlainBlocks(scBlocks, scBlocksMask);
		PlainBlock r = outputBlock(finalRes);
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
		add(new PlainBlock(Utils.fromInt(iden, lengthOfIden), new boolean[]{true}, data, false));
	}
	
	

}
