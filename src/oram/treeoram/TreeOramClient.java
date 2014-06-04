package oram.treeoram;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import oram.Block;
import oram.PlainBlock;
import test.Utils;
import flexsc.*;

public class TreeOramClient<T> extends TreeOramParty<T> {
	TreeOramLib<T> lib;
	public TreeOramClient(InputStream is, OutputStream os, int N, int dataSize,
			Party p, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, m, sp);
		lib = new TreeOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, capacity, gen);
	}
	
	public void add(PlainBlock b) throws Exception {
		Block<T> scNewBlock = inputBlockOfClient(b);
		Block<T>[][] tree1 = prepareBlocks(tree[1], tree[1], tree[1]);
		 
		lib.add(tree1[0], scNewBlock);

		tree[1] = preparePlainBlocks(tree1[0], tree1[1]);
	}
	
		
	public boolean[] readAndRemove(boolean[] iden, boolean[] pos) throws Exception {
		PlainBlock[][] blocks = getPath(pos);
		Block<T>[][][] scPath = preparePath(blocks, blocks, blocks);
		
		T[] scIden = gen.inputOfAlice(iden);
		
		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		
		blocks = preparePlainPath(scPath[0], scPath[1]);
		putPath(blocks, pos);
		return outputBlock(res).data;
	}
	
	protected Block[][][] prepareBlocksTriple(int index) throws Exception {
		Block[][][] result = new Block[2][3][];
		Block<T>[][] top = prepareBlocks(tree[index], tree[index], tree[index]);
		Block<T>[][] left = prepareBlocks(tree[index*2], tree[index*2], tree[index*2]);
		Block<T>[][] right = prepareBlocks(tree[index*2+1], tree[index*2+1], tree[index*2+1]);
		
		result[0][0] = top[0];
		result[1][0] = top[1];
		result[0][1] = left[0];
		result[1][1] = left[1];
		result[0][2] = right[0];
		result[1][2] = right[1];
		return result;
	}
	
	public void evictUnit(int index, int level) throws Exception {
		if(mode == mode.COUNT)
			index = 0;
		Block<T>[][][] triple = prepareBlocksTriple(index);				
		lib.evitUnit(triple[0][0], triple[0][1],triple[0][2],
					 level);
		
		tree[index] = preparePlainBlocks(triple[0][0], triple[1][0]);
		tree[index*2] = preparePlainBlocks(triple[0][1], triple[1][1]);
		tree[index*2+1] =preparePlainBlocks(triple[0][2], triple[1][2]);
	}

	public boolean[] read(int iden, boolean[] pos, boolean[] newPos) throws Exception {
		boolean[] r = readAndRemove(iden, pos);
		putBack(iden, newPos, r);
		return r;
	}
	
	public void write(int iden, boolean[] pos, boolean[] newPos, boolean[] data) throws Exception {
		readAndRemove(iden, pos);
		putBack(iden, newPos, data);
	}
	
	public boolean[] readAndRemove(int iden, boolean[] pos) throws Exception {
		return readAndRemove(Utils.fromInt(iden, lengthOfIden), pos);
	}
	
	public void putBack(int iden, boolean[] pos, boolean[] data) throws Exception {
		add(new PlainBlock(Utils.fromInt(iden, lengthOfIden), pos, data, false));
		evict();
		evict();
	}
	
	public boolean[] read(int iden, int pos, int newPos) throws Exception {
		boolean[] r = readAndRemove(iden, pos);
		putBack(iden, newPos, r);
		return r;
	}
	
	public void write(int iden, int pos, int newPos, boolean[] data) throws Exception {
		readAndRemove(iden, pos);
		putBack(iden, newPos, data);
	}
	
	public boolean[] readAndRemove(int iden, int pos) throws Exception {
		return readAndRemove(Utils.fromInt(iden, lengthOfIden), Utils.fromInt(pos, lengthOfPos));
	}
	
	public void putBack(int iden, int pos, boolean[] data) throws Exception {
		add(new PlainBlock(Utils.fromInt(iden, lengthOfIden), Utils.fromInt(pos, lengthOfPos), data, false));
		evict();
		evict();
	}
}