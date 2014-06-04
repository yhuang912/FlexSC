package oram.clporam;

import java.io.InputStream;
import java.io.OutputStream;
import flexsc.*;
import oram.PlainBlock;
import oram.TreeBasedOramParty;

public abstract class CLPOramParty<T> extends TreeBasedOramParty<T> {
	public PlainBlock[] queue;
	public int queueCapacity;
	int tempStashSize;
	int leafCapacity;
	int nodeCapacity;
	public CLPOramParty(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int nodeCapacity, int leafCapacity, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, nodeCapacity, m);
		this.leafCapacity = leafCapacity;
		this.nodeCapacity = nodeCapacity;
		//taking the worst case, can be tuned.
		tempStashSize = Math.max(10, logN);
		int t = logN;
		queueCapacity = (int) ((2*t-10)/20.0*sp);
		PlainBlock b;
		if(gen != null) {
			b =  getDummyBlock(true);
		}
		else {
			b =  getDummyBlock(false);
		}

		if(m == mode.COUNT) {
			tree[0] = new PlainBlock[leafCapacity];
			for(int j = 0; j < leafCapacity; ++j)
				tree[0][j] = b;
			for(int i = this.N/2; i < this.N; ++i)
				tree[i] = tree[0];
		}
		else{
			for(int i = 0; i < this.N; ++i)
				for(int j = this.N/2; j < leafCapacity; ++j)
					tree[i][j] = b;
		}

		queue = new PlainBlock[queueCapacity];
		if(gen != null) {
			for(int i = 0; i < queue.length; ++i) 
				queue[i] = getDummyBlock(true);
		}
		else {
			for(int i = 0; i < queue.length; ++i) 
				queue[i] = getDummyBlock(false);	
		}
	}

	public void flush() throws Exception{
		for(int k = 0; k < 3; ++k) {
			flushOneTime(getRandomPath());
		}
	}
	abstract public void flushOneTime(boolean[] pos) throws Exception;
}

