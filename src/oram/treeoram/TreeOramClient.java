package oram.treeoram;

import java.io.InputStream;
import java.io.OutputStream;

import oram.Block;
import test.Utils;
import flexsc.*;

public class TreeOramClient<T> extends TreeOramParty<T> {
	TreeOramLib<T> lib;
	public TreeOramClient(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int capacity, Mode m) throws Exception {
		super(is, os, N, dataSize, p, capacity, m);
		lib = new TreeOramLib<T>(lengthOfIden, lengthOfPos, lengthOfData, logN, capacity, gen);
	}
	
	public void add(BlockInBinary b) throws Exception {
		Block<T> scNewBlock = inputBlockOfClient(b);
		Block<T>[][] tree1 = prepareBlocks(tree[1], tree[1], tree[1]);
		 
		lib.add(tree1[0], scNewBlock);

		tree[1] = prepareBlockInBinaries(tree1[0], tree1[1]);
	}
	
	public BlockInBinary readAndRemove(boolean[] iden, boolean[] pos) throws Exception {
		BlockInBinary[] blocks = flatten(getAPath(pos));
		Block<T>[][] scPath = prepareBlocks(blocks, blocks, blocks);
		
		T[] scIden = gen.inputOfAlice(iden);
				
		Block<T> res = lib.readAndRemove(scPath[0], scIden);
		
		blocks = prepareBlockInBinaries(scPath[0], scPath[1]);
		putAPath(blocks, pos);
		BlockInBinary r = outputBlock(res);
		return r;
	}
	
	public void evictUnit(int index, int level) throws Exception {
		Block<T>[][] top = prepareBlocks(tree[index], tree[index], tree[index]);
		Block<T>[][] left = prepareBlocks(tree[index*2], tree[index*2], tree[index*2]);
		Block<T>[][] right = prepareBlocks(tree[index*2+1], tree[index*2+1], tree[index*2+1]);
				
		lib.evitUnit(top[0], 
					 left[0],
					 right[0],level);
		
		tree[index] = prepareBlockInBinaries(top[0], top[1]);
		tree[index*2] = prepareBlockInBinaries(left[0], left[1]);
		tree[index*2+1] =prepareBlockInBinaries(right[0], right[1]);
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
		return readAndRemove(Utils.fromInt(iden, lengthOfIden), pos).data;
	}
	
	public void putBack(int iden, boolean[] pos, boolean[] data) throws Exception {
		add(new BlockInBinary(Utils.fromInt(iden, lengthOfIden), pos, data, false));
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
		return readAndRemove(Utils.fromInt(iden, lengthOfIden), Utils.fromInt(pos, lengthOfPos)).data;
	}
	
	public void putBack(int iden, int pos, boolean[] data) throws Exception {
		add(new BlockInBinary(Utils.fromInt(iden, lengthOfIden), Utils.fromInt(pos, lengthOfPos), data, false));
		evict();
		evict();
	}

}
