package oramgc;

import java.util.Arrays;

import circuits.IntegerLib;
import flexsc.CompEnv;
import gc.Signal;


public class BucketLib extends IntegerLib {
	public Block dummyBlock;
	public BucketLib(int lengthOfIden, int lengthOfPos, int lengthOfData, CompEnv<Signal> e) {
		super(e);
		dummyBlock = new Block(zeros(lengthOfIden), zeros(lengthOfPos), zeros(lengthOfData));
	}

	public Block conditionalReadAndRemove(Block[] bucket, Signal[] iden, Signal condition) throws Exception {
		Block result = dummyBlock;
		Signal[] targetIden = mux(dummyBlock.iden, iden, condition);
		for(int i = 0; i < bucket.length; ++i) {
			Signal match = eq(targetIden, bucket[i].iden);
			result = mux(result, bucket[i], match);
			bucket[i] = mux(bucket[i], dummyBlock, match);
		}
		return result;
	}

	public Block readAndRemove(Block[] bucket, Signal[] iden) throws Exception {
		return conditionalReadAndRemove(bucket, iden, SIGNAL_ONE);
	}

	public void conditionalAdd(Block[] bucket, Block newBlock, Signal condition) throws Exception {
		Signal added = not(condition);
		for(int i = 0; i < bucket.length; ++i) {
			added = or(eq(newBlock.iden, bucket[i].iden), added);
		}
		for(int i = 0; i < bucket.length; ++i) {
			Signal match = eq(bucket[i].iden, dummyBlock.iden);
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
			Signal match = not(eq(bucket[i].iden, dummyBlock.iden));
			Signal shouldPop = and(not(poped), match);
			poped = or(poped, shouldPop);
			result = mux(result, bucket[i], shouldPop);

			bucket[i] = mux(bucket[i], dummyBlock, shouldPop);
		}
		return result;
	}

/*	public Block prepareBlock(Block block, Signal[] key, Signal[] nouce) {
		Signal[] res = AESEnc(nouce, key, dummyBlock.pos.length + dummyBlock.data.length+dummyBlock.iden.length);
		Block nouceBlock = new Block(Arrays.copyOfRange(res, 0, dummyBlock.iden.length),
				Arrays.copyOfRange(res, dummyBlock.iden.length, dummyBlock.iden.length+dummyBlock.pos.length),
				Arrays.copyOfRange(res, dummyBlock.iden.length+dummyBlock.pos.length, 
						dummyBlock.iden.length+dummyBlock.pos.length+dummyBlock.data.length));
		return xor(block, nouceBlock);
	}

	public Block[] prepareBucket(Block[] bucket, Signal[] key, Signal[][] nouce) {
		Block[] newBucket = new Block[bucket.length];
		for(int i = 0; i < bucket.length; ++i)
			newBucket[i] = prepareBlock(bucket[i], key, nouce[i]);
		return newBucket;
	}

	void writeBackBlock(Block block, Signal[] nouce, Signal[] key, Block newBlock) {
		nouce = getRandom(80);
		Signal[] res = AESEnc(nouce, key, dummyBlock.pos.length + dummyBlock.data.length+dummyBlock.iden.length);
		Block nouceBlock = new Block(Arrays.copyOfRange(res, 0, dummyBlock.iden.length),
				Arrays.copyOfRange(res, dummyBlock.iden.length, dummyBlock.iden.length+dummyBlock.pos.length),
				Arrays.copyOfRange(res, dummyBlock.iden.length+dummyBlock.pos.length, 
						dummyBlock.iden.length+dummyBlock.pos.length+dummyBlock.data.length));
		block = xor(newBlock, nouceBlock);
	}
	
	void writeBackBucket(Block[] bucket, Signal[][]nouce, Signal[] key, Block[] newBucket) {
		for(int i = 0; i < bucket.length; ++i)
			writeBackBlock(bucket[i], nouce[i], key, newBucket[i]);
	}*/

	public Block mux(Block a, Block b, Signal choose) throws Exception {
		Signal[] iden = mux(a.iden, b.iden, choose);
		Signal[] pos = mux(a.pos, b.pos, choose);
		Signal[] data = mux(a.data, b.data, choose);
		return new Block(iden, pos, data);
	}

	public Block xor(Block a, Block b) {
		Signal[] iden = xor(a.iden, b.iden);
		Signal[] pos = xor(a.pos, b.pos);
		Signal[] data = xor(a.data, b.data);
		return new Block(iden, pos, data);
	}
	
	public Block[] xor(Block[] a, Block[] b) {
		assert(a.length == b.length) : "xor blocks error";
		Block[] result = new Block[a.length];
		for(int i = 0; i < a.length; ++i)
			result[i] = xor(a[i], b[i]);
		return result;
	}

}
