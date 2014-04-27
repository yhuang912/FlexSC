package oramgc;

import circuits.BitonicSortLib;
import flexsc.CompEnv;
import gc.GCSignal;


public class BucketLib extends BitonicSortLib {
	public Block dummyBlock;
	protected int lengthOfIden;
	protected int lengthOfPos;
	protected int lengthOfData;
	public BucketLib(int lengthOfIden, int lengthOfPos, int lengthOfData, CompEnv<GCSignal> e) {
		super(e);
		this.lengthOfData = lengthOfData;
		this.lengthOfIden = lengthOfIden;
		this.lengthOfPos = lengthOfPos;
		dummyBlock = new Block(zeros(lengthOfIden), zeros(lengthOfPos), zeros(lengthOfData), SIGNAL_ONE);
	}


	public Block conditionalReadAndRemove(Block[] bucket, GCSignal[] iden, GCSignal condition) throws Exception {
		Block result = dummyBlock;
		for(int i = 0; i < bucket.length; ++i) {
			GCSignal match = eq(iden, bucket[i].iden);
			match = and(match, not(bucket[i].isDummy));
			match = and(match, condition);
			result = mux(result, bucket[i], match);
			bucket[i].isDummy = mux(bucket[i].isDummy, SIGNAL_ONE, match);
		}
		return result;
	}

	public Block readAndRemove(Block[] bucket, GCSignal[] iden) throws Exception {
		return conditionalReadAndRemove(bucket, iden, SIGNAL_ONE);
	}

	public void conditionalAdd(Block[] bucket, Block newBlock, GCSignal condition) throws Exception {
		GCSignal added = not(condition);
		for(int i = 0; i < bucket.length; ++i) {
			GCSignal match = and( not(bucket[i].isDummy), eq(newBlock.iden, bucket[i].iden) );
			added = or(match, added);
		}
		for(int i = 0; i < bucket.length; ++i) {
			GCSignal match = bucket[i].isDummy;
			GCSignal shouldAdd = and(not(added), match);
			added = or(added, shouldAdd);
			bucket[i] = mux(bucket[i], newBlock, shouldAdd);
		}
	}

	public void add(Block[] bucket, Block newBlock) throws Exception {
		conditionalAdd(bucket, newBlock, SIGNAL_ONE);
	}

	public Block pop(Block[] bucket) throws Exception {
		return conditionalPop(bucket, SIGNAL_ONE);
	}

	public Block conditionalPop(Block[] bucket, GCSignal condition) throws Exception {
		Block result = dummyBlock;
		GCSignal poped = not(condition);// condition=T => shouldpop => set to poped;
		for(int i = 0; i < bucket.length; ++i) {
			GCSignal notDummy = not(bucket[i].isDummy);
			GCSignal shouldPop = and(not(poped), notDummy);
			poped = or(poped, shouldPop);
			result = mux(result, bucket[i], shouldPop);

			//bucket[i] = mux(bucket[i], dummyBlock, shouldPop);
			bucket[i].isDummy = mux(bucket[i].isDummy, SIGNAL_ONE, shouldPop);
		}
		return result;
	}

	public Block mux(Block a, Block b, GCSignal choose) throws Exception {
		GCSignal[] iden = mux(a.iden, b.iden, choose);
		GCSignal[] pos = mux(a.pos, b.pos, choose);
		GCSignal[] data = mux(a.data, b.data, choose);
		GCSignal isDummy = mux(a.isDummy, b.isDummy, choose);
		return new Block(iden, pos, data, isDummy);
	}

	public Block xor(Block a, Block b) {
		GCSignal[] iden = xor(a.iden, b.iden);
		GCSignal[] pos = xor(a.pos, b.pos);
		GCSignal[] data = xor(a.data, b.data);
		GCSignal isDummy = xor(a.isDummy, b.isDummy);
		return new Block(iden, pos, data, isDummy);
	}
	
	public Block[] xor(Block[] a, Block[] b) {
		assert(a.length == b.length) : "xor blocks error";
		Block[] result = new Block[a.length];
		for(int i = 0; i < a.length; ++i)
			result[i] = xor(a[i], b[i]);
		return result;
	}
}
