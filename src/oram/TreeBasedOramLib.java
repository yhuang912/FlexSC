package oram;

import flexsc.CompEnv;


public class TreeBasedOramLib<T> extends BucketLib<T> {
	public TreeBasedOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, CompEnv<T> e) {
		super(lengthOfIden,lengthOfPos,lengthOfData,e);
	}


	public T[] deepestLevel(T[] pos, T[] path, T isDummy) throws Exception {
		T[] xored = xor(pos, path);
		T[] deep = leadingZeros(xored);
		return mux(deep, zeros(deep.length), isDummy);
	}

	public T[][] DeepestBlock(Block<T>[] bucket, T[] pos) throws Exception {
		T[][] deepest = env.newTArray(bucket.length, 0);//;new T[nodeCapacity][];
		for(int j = 0; j < bucket.length; ++j){
			deepest[j] = deepestLevelArray(bucket[j].pos, pos);
		}

		T[] maxIden = bucket[0].iden;
		T[] maxdepth = deepest[0];
		T isDummy = bucket[0].isDummy;
		for(int j = 1; j < bucket.length; ++j) {
			T greater = geq(deepest[j], maxdepth);
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
		T[][] deepest = env.newTArray(bucket.length, 0);//;new T[nodeCapacity][];
		for(int j = 0; j < bucket.length; ++j){
			deepest[j] = deepestLevelArray(bucket[j].pos, pos);
		}

		T[] minIden = bucket[0].iden;
		T[] minDepth = deepest[0];
		T isDummy = bucket[0].isDummy;

		for(int j = 1; j < bucket.length; ++j) {
			T less = leq(deepest[j], minDepth);
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
}