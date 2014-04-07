package oramgc.trivialoram;

import gc.Signal;

import java.io.InputStream;
import java.io.OutputStream;

import oramgc.Block;
import oramgc.OramParty;

public class TrivialOramClient extends OramParty {
	BlockInBinary[] blocks;
	int capacity;
	public TrivialOramClient(InputStream is, OutputStream os, int N,
			int dataSize, int capacity) throws Exception {
		super(is, os, N, dataSize, OramParty.Party.CLIENT);
		blocks = new BlockInBinary[capacity];
		this.capacity = capacity;
		for(int i = 0; i < blocks.length; ++i)
			blocks[i] = getDummyBlock();
	}
	
	public void add(BlockInBinary b) throws Exception {
		Block[][] result = prepareBlocks(blocks, blocks, blocks);
		Block[] scBlocks = result[0];
		Block[] scBlocksMask = result[1];		
		Block scNewBlock = inputBlockOfClient(b);
		
		lib.add(scBlocks, scNewBlock);
		
		blocks = prepareBlockInBinaries(scBlocks, scBlocksMask);
	}
	
	public BlockInBinary pop() throws Exception{
		Block[][] result = prepareBlocks(blocks, blocks, blocks);
		Block[] scBlocks = result[0];
		Block[] scBlocksMask = result[1]; 
		
		Block res = lib.pop(scBlocks);
		
		blocks = prepareBlockInBinaries(scBlocks, scBlocksMask);
		BlockInBinary r =  outputBlock(res);
		return r;
	}
	
	public BlockInBinary readAndRemove(boolean [] iden) throws Exception {
		Block[][] result = prepareBlocks(blocks, blocks, blocks);
		Block[] scBlocks = result[0];
		Block[] scBlocksMask = result[1]; 
		Signal[] scIden = gen.inputOfGen(iden); 
		
		Block res = lib.readAndRemove(scBlocks, scIden);
		
		blocks = prepareBlockInBinaries(scBlocks, scBlocksMask);
		BlockInBinary r = outputBlock(res);
		return r;		
	}
	
	
	

}
