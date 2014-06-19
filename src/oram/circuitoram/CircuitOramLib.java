package oram.circuitoram;


import oram.Block;
import oram.TreeBasedOramLib;
import flexsc.CompEnv;

public class CircuitOramLib<T> extends TreeBasedOramLib<T> {
	int logN;
	int capacity;
	
	public CircuitOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, int logN, int capacity, CompEnv<T> e) {
		super(lengthOfIden, lengthOfPos, lengthOfData, e);
		this.logN = logN;
		this.capacity = capacity;
	}
	
	public T[][] deepestBlockShort(Block<T>[] blocks, T[]pathSignal) {
		T[][] res = env.newTArray(blocks.length, 3);
		for(int i = 0; i <res.length; ++i) {
			res[i] = zeros(10);
		}
		return res;
	}

	public void flush(Block<T>[][] scPath, boolean[] path, Block<T>[] scQueue) throws Exception {
		//make path to signal
		T[] pathSignal =  env.newTArray(path.length);
		for(int i = 0; i < path.length; ++i)
			pathSignal[i] = path[i] ? SIGNAL_ONE : SIGNAL_ZERO;

		//PrepareDeepest(path)
		T[][] stashDeepest = deepestBlockShort(scQueue, pathSignal);
		T[][] deepestD = env.newTArray(scPath.length+1, 0);
		T[] deepestBot = ones(scPath.length+1);
		
		T[] cur = toSignals(0, stashDeepest[0].length);
		T[] curv = stashDeepest[0];
		for(int i = 0; i < logN; ++i) {
			T[] iSignal= toSignals(i, curv.length);
			T curvGEQI = geq(curv, iSignal);
			deepestD[i] = mux(deepestD[i], cur, curvGEQI);
			T[][] pathiDeepest = deepestBlockShort(scPath[i], pathSignal);
			
			T lGcurv = not(leq(pathiDeepest[0], curv));
			curv = mux(curv, pathiDeepest[0], lGcurv);
			cur = mux(cur, iSignal, lGcurv);
		}
		
		//prepareTarget(path)
		T[] c = env.newTArray(10);
		T cBot = SIGNAL_ONE;
		T[] l = env.newTArray(10);
		
		T[][] target = env.newTArray(scPath.length+1, 10);
		T[] targetBot = ones(scPath.length+1);
		
		for(int i = logN-1; i >=0; --i)
		{
			//prepare conditions
			T[] iSignal= toSignals(i, curv.length);
			T iEQl = eq(iSignal, l);
			T isFull = isFull(scPath[i]);
			T hasSlot = or(and(cBot, not(isFull)), not(targetBot[i]));
			T canPush = not(deepestBot[i]);
			
			//begin assignment
			target[i] = mux(targetBot, c, iEQl);
			T[] tmpC = mux(targetBot, iSignal, iEQl);
			c = mux(targetBot, iSignal, canPush);
		}
		
		//evictionFast(path)
		Block<T> hold = dummyBlock;
		for(int i = 0; i < logN; ++i)
		{
			T[] iSignal= toSignals(i, curv.length);
			T iEQl = eq(iSignal, l);
			T toExtract = and(not(hold.isDummy), iEQl);
			Block<T> toWrite = mux(dummyBlock, hold, toExtract);//can make it more efficent!!!!
			hold = mux(hold, readAndRemove(scPath[i], iSignal), not(targetBot[i]));
			l = mux(l, target[i], not(targetBot[i]));
			conditionalAdd(scPath[i], toWrite, toExtract);
		}
	}
}