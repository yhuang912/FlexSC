package oram.swapoam;

import oram.Block;
import oram.TreeBasedOramLib;
import flexsc.CompEnv;

public class SwapOramLib<T> extends TreeBasedOramLib<T> {
	int logN;
	int capacity;
	
	public SwapOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, int logN, int capacity, CompEnv<T> e) {
		super(lengthOfIden, lengthOfPos, lengthOfData, e);
		this.logN = logN;
		this.capacity = capacity;
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

			T shouldPush = or(and(not(max[2][0]), geq(maxdepth, mindepth)), min[2][0]);

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
			T cannotPush = or(leq(maxdepth, toSignals((1<<(i+1))-1, maxdepth.length)), max[2][0]);
			
			T shouldPush = or(and(not(max[2][0]), geq(maxdepth, mindepth)), min[2][0]);
			shouldPush = and(shouldPush, not(cannotPush));
			
			Block<T> minBlock = conditionalReadAndRemove(scPath[i+1], minIden, full);
			Block<T> maxBlock = conditionalReadAndRemove(scPath[i], maxIden, shouldPush);
			
			conditionalAdd(scPath[i+1], maxBlock, shouldPush);
			conditionalAdd(scPath[i], minBlock, full);
		}
	}
}