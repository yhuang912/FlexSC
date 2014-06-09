package oram.CSCOram;

import oram.Block;
import oram.TreeBasedOramLib;
import flexsc.CompEnv;

public class CSCOramLib<T> extends TreeBasedOramLib<T> {

	int logN;
	int capacity;
	public CSCOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, int logN, int capacity, CompEnv<T> e) {
		super(lengthOfIden, lengthOfPos, lengthOfData, e);
		this.logN = logN;
		this.capacity = capacity;
	}
	
	public void flush(Block<T>[][] scPath, boolean[] path) throws Exception {
		T[] pathSignal =  env.newTArray(path.length);
		for(int i = 0; i < path.length; ++i)
			pathSignal[i] = path[i] ? SIGNAL_ONE : SIGNAL_ZERO;

		for(int i = 0; i < logN-1; ++i) {
			T[][] max = DeepestBlock(scPath[i], pathSignal);
			T[] maxIden = max[0];
			T[] maxdepth= max[1];

//			T cannotPush = leq(maxdepth, toSignals(i, maxdepth.length));
			T cannotPush = geq(maxdepth, toSignals((1<<i)-1, maxdepth.length));

			T full = SIGNAL_ONE;
			for(int j = 0; j < scPath[i+1].length; ++j)
				full = and(full, not(scPath[i+1][j].isDummy));

			Block<T> block = conditionalReadAndRemove(scPath[i], maxIden, and(not(cannotPush), not(full)));
			add(scPath[i+1], block);
		}
	}

	public void putFromQueueToPath(Block<T>[][] path, Block<T>[] stash, boolean[] pos) throws Exception{
		T[] posInSignal = env.newTArray(lengthOfPos);//new Signal[lengthOfPos];
		for(int i = 0; i < pos.length; ++i)
			posInSignal[i] = pos[i] ? SIGNAL_ONE : SIGNAL_ZERO;


		T[][] max = DeepestBlock(stash, posInSignal);
		T[] maxIden = max[0];
		
		Block<T> b = readAndRemove(stash, maxIden);
		T pushed = b.isDummy;
		
		T[] canPushm = deepestLevelArray(b.pos, posInSignal);//,blockInSignal[k][blockInSignal[k].length-1]);

		for(int i = logN-1; i >=0; --i) {
			for(int j = 0; j < path[i].length; ++j) {
				T canPush = and(path[i][j].isDummy, canPushm[i]);
				T toPush = and(not(pushed), canPush);
				path[i][j] = mux(path[i][j], b, toPush);
				pushed = or(pushed, toPush);
			}
		}
		conditionalAdd(stash, b, not(pushed));
	}
}