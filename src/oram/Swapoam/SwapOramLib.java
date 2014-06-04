package oram.Swapoam;

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
		for(int j = 1; j < capacity; ++j) {
			T greater = leq(deepest[j], maxdepth);
			maxIden = mux(maxIden, bucket[j].iden, greater);
			maxdepth = mux(maxdepth, deepest[j], greater);
		}
		T[][] result = env.newTArray(2, 0);
		result[0] = maxIden;
		result[1] = maxdepth;
		return result;
	}
	
	public T[][] SwallowestBlock(Block<T>[] bucket, T[] pos) throws Exception {
		T[][] deepest = env.newTArray(capacity, 0);//;new T[nodeCapacity][];
		for(int j = 0; j < capacity; ++j){
			deepest[j] = deepestLevelArray(bucket[j].pos, pos);
		}

		T[] minIden = bucket[0].iden;
		T[] minDepth = deepest[0];
		for(int j = 1; j < capacity; ++j) {
			T less = leq(deepest[j], minIden);
			minIden = mux(minIden, bucket[j].iden, less);
			minDepth = mux(minDepth, deepest[j], less);
		}
		T[][] result = env.newTArray(2, 0);
		result[0] = minIden;
		result[1] = minDepth;
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
	
	public void flush(Block<T>[][] scPath, boolean[] path) throws Exception {
		T[] pathSignal =  env.newTArray(path.length);
		for(int i = 0; i < path.length; ++i)
			pathSignal[i] = path[i] ? SIGNAL_ONE : SIGNAL_ZERO;

		for(int i = 0; i < logN-1; ++i) {
			T[][] max = DeepestBlock(scPath[i], pathSignal);
			T[] maxIden = max[0];
			T[] maxdepth= max[1];
			
			T[][] min = SwallowestBlock(scPath[i+1], pathSignal);
			T[] minIden = min[0];
			T[] mindepth= min[1];

			T cannotPush = geq(maxdepth, toSignals((1<<i)-1, maxdepth.length));

			T full = SIGNAL_ONE;
			for(int j = 0; j < scPath[i+1].length; ++j)
				full = and(full, not(scPath[i+1][j].isDummy));

			Block<T> maxBlock = readAndRemove(scPath[i], maxIden);
			Block<T> minBlock = conditionalReadAndRemove(scPath[i+1], minIden, full);
			T shouldPush = SIGNAL_ONE;//!!
			
			conditionalAdd(scPath[i+1], maxBlock, shouldPush);
			conditionalAdd(scPath[i], minBlock, full);			
		}
	}
}