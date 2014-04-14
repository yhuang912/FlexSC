package oramgc.pathoram;

import gc.Signal;

import java.io.InputStream;
import java.io.OutputStream;
import oramgc.Block;
import test.Utils;


public class PathOramClient extends PathOramParty {
	PathOramLib lib;
	public PathOramClient(InputStream is, OutputStream os, int N, int dataSize,
			Party p) throws Exception {
		super(is, os, N, dataSize, p);
		lib = new PathOramLib(lengthOfIden, lengthOfPos, lengthOfData, logN, gen);
	}
	
	public BlockInBinary read(int iden, int pos, int newPos) throws Exception {
		return read(Utils.fromInt(iden, lengthOfIden), 
				Utils.fromInt(pos, lengthOfPos),
				Utils.fromInt(newPos, lengthOfPos));
	}
	
	public void write(int iden, int pos, int newPos, boolean[] data) throws Exception {
		write(Utils.fromInt(iden, lengthOfIden), 
				Utils.fromInt(pos, lengthOfPos),
				Utils.fromInt(newPos, lengthOfPos), data);
	}
	
	public BlockInBinary read(boolean[] iden, boolean[] pos, boolean[] newPos) throws Exception {
		return access(iden, pos, newPos, null);
	}
	
	public void write(boolean[] iden, boolean[] pos, boolean[] newPos, boolean[] data) throws Exception {
		access(iden, pos, newPos, data);
	}

	
	public BlockInBinary access(boolean[] iden, boolean[] pos, boolean[] newPos, boolean[] data) throws Exception {
		BlockInBinary[] blocks = flatten(getAPath(pos));
		Block[][] scPath = prepareBlocks(blocks, blocks, blocks);
		Block[][] scStash = prepareBlocks(stash, stash, stash);
		Signal[] scIden = gen.inputOfGen(iden);
		Signal[] scPos = gen.inputOfGen(newPos);
		
		
		Block res = lib.readAndRemove(scPath[0], scIden);

		BlockInBinary r =  outputBlock(res);
		
		Signal[] scData = res.data;
		if(data != null)
			scData = gen.inputOfGen(data);
		
		Block scNewBlock = new Block(scIden, scPos, scData);
		
		lib.add(scStash[0], scNewBlock);
		
		Signal[][] debug = lib.pushDown(scPath[0], scStash[0], pos);
		
		blocks = prepareBlockInBinaries(scPath[0], scPath[1]);
		stash = prepareBlockInBinaries(scStash[0], scStash[1]);
		putAPath(blocks, pos);
		
		/*int [] re = new int[debug.length];
		for(int i = 0; i < debug.length; ++i)
			re[i] = Utils.toInt( gen.outputToGen(debug[i]));
		
		System.out.println(Arrays.toString(re));
		*/
		return r;
	}
}
