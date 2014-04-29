package oramgc.treeoram;

import java.io.InputStream;
import java.io.OutputStream;
import oramgc.TreeBasedOramParty;
import flexsc.*;

public abstract class TreeOramParty<T> extends TreeBasedOramParty<T> {
	public TreeOramParty(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int capacity, Mode m) throws Exception {
		super(is, os, N, dataSize, p, capacity, m);
	}
	
	public int randomIndexOnLevel(int level) {
		if(level == 1)
			return 1;
		
		int left = 1<<(level-1);
		return commonRandom.nextInt(left) + left;
	}
	
	public void evict() throws Exception {
		for(int i = 1; i < logN; ++i) {
			int index = randomIndexOnLevel(i);
			evictUnit(index, i);
		}
	}
	
	abstract public void evictUnit(int index, int level) throws Exception;
}
