package oramgc;

import circuits.BitonicSortLib;
import flexsc.CompEnv;
import gc.Signal;


public class BucketLib extends BitonicSortLib {
	public Block dummyBlock;
	protected int lengthOfIden;
	protected int lengthOfPos;
	protected int lengthOfData;
	public BucketLib(int lengthOfIden, int lengthOfPos, int lengthOfData, CompEnv<Signal> e) {
		super(e);
		this.lengthOfData = lengthOfData;
		this.lengthOfIden = lengthOfIden;
		this.lengthOfPos = lengthOfPos;
		dummyBlock = new Block(zeros(lengthOfIden), zeros(lengthOfPos), zeros(lengthOfData), SIGNAL_ONE);
	}


	public Block conditionalReadAndRemove(Block[] bucket, Signal[] iden, Signal condition) throws Exception {
		Block result = dummyBlock;
		for(int i = 0; i < bucket.length; ++i) {
			Signal match = eq(iden, bucket[i].iden);
			match = and(match, not(bucket[i].isDummy));
			match = and(match, condition);
			result = mux(result, bucket[i], match);
			bucket[i].isDummy = mux(bucket[i].isDummy, SIGNAL_ONE, match);
		}
		return result;
	}

	public Block readAndRemove(Block[] bucket, Signal[] iden) throws Exception {
		return conditionalReadAndRemove(bucket, iden, SIGNAL_ONE);
	}

	public void conditionalAdd(Block[] bucket, Block newBlock, Signal condition) throws Exception {
		Signal added = not(condition);
		for(int i = 0; i < bucket.length; ++i) {
			Signal match = and( not(bucket[i].isDummy), eq(newBlock.iden, bucket[i].iden) );
			added = or(match, added);
		}
		for(int i = 0; i < bucket.length; ++i) {
			Signal match = bucket[i].isDummy;
			Signal shouldAdd = and(not(added), match);
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

	public Block conditionalPop(Block[] bucket, Signal condition) throws Exception {
		Block result = dummyBlock;
		Signal poped = not(condition);// condition=T => shouldpop => set to poped;
		for(int i = 0; i < bucket.length; ++i) {
			Signal notDummy = not(bucket[i].isDummy);
			Signal shouldPop = and(not(poped), notDummy);
			poped = or(poped, shouldPop);
			result = mux(result, bucket[i], shouldPop);

			//bucket[i] = mux(bucket[i], dummyBlock, shouldPop);
			bucket[i].isDummy = mux(bucket[i].isDummy, SIGNAL_ONE, shouldPop);
		}
		return result;
	}

	public Block mux(Block a, Block b, Signal choose) throws Exception {
		Signal[] iden = mux(a.iden, b.iden, choose);
		Signal[] pos = mux(a.pos, b.pos, choose);
		Signal[] data = mux(a.data, b.data, choose);
		Signal isDummy = mux(a.isDummy, b.isDummy, choose);
		return new Block(iden, pos, data, isDummy);
	}

	public Block xor(Block a, Block b) {
		Signal[] iden = xor(a.iden, b.iden);
		Signal[] pos = xor(a.pos, b.pos);
		Signal[] data = xor(a.data, b.data);
		Signal isDummy = xor(a.isDummy, b.isDummy);
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
