package oram.Swapoam;

import java.util.Arrays;

import oram.Block;
import oram.BucketLib;
import flexsc.CompEnv;
import flexsc.Signal;

public class SwapOramLib<T> extends BucketLib<T> {

	int logN;
	int capacity;
	public SwapOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, int logN, int capacity, CompEnv<T> e) {
		super(lengthOfIden, lengthOfPos, lengthOfData, e);
		this.logN = logN;
		this.capacity = capacity;
	}

	public T[] deepestLevel(T[] pos, T[] path, T isDummy) throws Exception {
		T[] xored = xor(pos, path);
		T[] deep = leadingZeros(xored);
		return mux(deep, zeros(deep.length), isDummy);
	}
	
	public T[][] DeepestBlock2(Block<T>[] bucket, T[] pos) throws Exception {
		T[][] deepest = env.newTArray(capacity, 0);//;new T[nodeCapacity][];
		for(int j = 0; j < capacity; ++j)
			deepest[j] = deepestLevel(bucket[j].pos, pos, bucket[j].isDummy);

		T[] maxIden = bucket[0].iden;
		T[] maxdepth = deepest[0];
		for(int j = 1; j < capacity; ++j) {
			T greater = geq(deepest[j], maxdepth);
			maxIden = mux(maxIden, bucket[j].iden, greater);
			maxdepth = mux(maxdepth, deepest[j], greater);
		}
		T[][] result = env.newTArray(2, 0);
		result[0] = maxIden;
		result[1] = maxdepth;
		return result;
	}

	public T[][] DeepestBlock(Block<T>[] bucket, T[] pos) throws Exception {
		T[][] deepest = env.newTArray(capacity, 0);//;new T[nodeCapacity][];
		for(int j = 0; j < capacity; ++j){
			deepest[j] = deepestLevelArray(bucket[j].pos, pos);
		}

		T[] maxIden = bucket[0].iden;
		T[] maxdepth = deepest[0];
		T isDummy = bucket[0].isDummy;
		for(int j = 1; j < capacity; ++j) {
			T greater = leq(deepest[j], maxdepth);
			greater = and(greater, not(bucket[j].isDummy));
			maxIden = mux(maxIden, bucket[j].iden, greater);
			maxdepth = mux(maxdepth, deepest[j], greater);
			isDummy = mux(isDummy, bucket[j].isDummy, greater);
		}
		T[][] result = env.newTArray(3, 0);
		result[0] = maxIden;
		result[1] = maxdepth;
		result[2] = env.newTArray(1);
		result[2][0] = isDummy;
		return result;
	}
	
	public T[][] SwallowestBlock(Block<T>[] bucket, T[] pos) throws Exception {
		T[][] deepest = env.newTArray(capacity, 0);//;new T[nodeCapacity][];
		for(int j = 0; j < capacity; ++j){
			deepest[j] = deepestLevelArray(bucket[j].pos, pos);
		}

		T[] minIden = bucket[0].iden;
		T[] minDepth = deepest[0];
		T isDummy = bucket[0].isDummy;

		for(int j = 1; j < capacity; ++j) {
			T less = geq(deepest[j], minDepth);
			less = and(less, not(bucket[j].isDummy));
			minIden = mux(minIden, bucket[j].iden, less);
			minDepth = mux(minDepth, deepest[j], less);
			isDummy = mux(isDummy, bucket[j].isDummy, less);
		}
		T[][] result = env.newTArray(3, 0);
		result[0] = minIden;
		result[1] = minDepth;
		result[2] = env.newTArray(1);
		result[2][0] = isDummy;
		return result;
	}
	
	public T[] deepestLevelArray(T[] pos, T[] path) throws Exception {
		T[] result = xor(pos, path); 
		for(int i = result.length-2; i >=0; --i) {
			result[i] = or(result[i], result[i+1]);
		}
		T[] result2 = env.newTArray(result.length+1);
		
		for(int i = 0; i < result.length; ++i)
			result2[i+1] = not(result[result.length-i-1]);
		result2[0] = SIGNAL_ONE;
		return result2;
	}
	
	public void flush(Block<T>[][] scPath, boolean[] path, Block<T>[] scQueue) throws Exception {
		T[] pathSignal =  env.newTArray(path.length);
		for(int i = 0; i < path.length; ++i)
			pathSignal[i] = path[i] ? SIGNAL_ONE : SIGNAL_ZERO;
		
		{
			T[][] max = DeepestBlock(scQueue, pathSignal);
			T[] maxIden = max[0];
			T[] maxdepth= max[1];
			
			T[][] min = SwallowestBlock(scPath[0], pathSignal);
			T[] minIden = min[0];
			T[] mindepth= min[1];

			T full = SIGNAL_ONE;
			for(int j = 0; j < scPath[0].length; ++j)
				full = and(full, not(scPath[0][j].isDummy));

			T shouldPush = or(and(not(max[2][0]), leq(maxdepth, mindepth)), min[2][0]);
			
			Block<T> minBlock = conditionalReadAndRemove(scPath[0], minIden, full);
			Block<T> maxBlock = conditionalReadAndRemove(scQueue, maxIden,shouldPush);
			
			conditionalAdd(scPath[0], maxBlock, shouldPush);
			conditionalAdd(scQueue, minBlock, full);
		}

		
		for(int i = 0; i < logN -1; ++i) {
			T[][] max = DeepestBlock(scPath[i], pathSignal);
			T[] maxIden = max[0];
			T[] maxdepth= max[1];
			
			T[][] min = SwallowestBlock(scPath[i+1], pathSignal);
			T[] minIden = min[0];
			T[] mindepth= min[1];

			T full = SIGNAL_ONE;
			for(int j = 0; j < scPath[i+1].length; ++j)
				full = and(full, not(scPath[i+1][j].isDummy));
			T cannotPush = or(geq(maxdepth, toSignals((1<<(maxdepth.length-i+1))-1, maxdepth.length)), max[2][0]);
			
			T shouldPush = or(and(not(max[2][0]), leq(maxdepth, mindepth)), min[2][0]);
			shouldPush = and(shouldPush, not(cannotPush));
			
			Block<T> minBlock = conditionalReadAndRemove(scPath[i+1], minIden, full);
			Block<T> maxBlock = conditionalReadAndRemove(scPath[i], maxIden, shouldPush);
			
			conditionalAdd(scPath[i+1], maxBlock, shouldPush);
			conditionalAdd(scPath[i], minBlock, full);
		}
	}
}