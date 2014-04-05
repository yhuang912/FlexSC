package oramgc.treeoram;

import oramgc.BucketLib;
import test.Utils;
import flexsc.CompEnv;
import gc.Signal;

public class TreeOramLib extends BucketLib {

	int capacity, logN;
	
	public TreeOramLib(int lengthOfIden, int lengthOfPos, int lengthOfData, int logN, int capacity,
			CompEnv<Signal> e) {
		super(lengthOfIden, lengthOfPos, lengthOfData, e);
		this.logN = logN;
		this.capacity = capacity;
	}
	
	public Signal[] deepestLevel(Signal[] pos, int path) throws Exception {
		Signal[] pathSignal = toSignals(path, pos.length);
		Signal[] xored = xor(pos, pathSignal);
		return leadingZeros(xored);
	}
}