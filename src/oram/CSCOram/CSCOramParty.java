package oram.CSCOram;

import java.io.InputStream;
import java.io.OutputStream;
import flexsc.*;
import oram.PlainBlock;
import oram.TreeBasedOramParty;



public abstract class CSCOramParty<T> extends TreeBasedOramParty<T> {
	public PlainBlock[] queue;
	public int queueCapacity;
	int[] stash15 = new int[]{4, 5, 8, 13, 18, 23, 34, 50};
	public CSCOramParty(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int cap, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, cap, m);
		
		int l = logN;
		if(logN <= 24 && logN >= 10) {
			if(logN %2 == 1)
				l++;
			queueCapacity = (int) ((sp-15)+stash15[(l-10)/2]);
		}
		else 
			queueCapacity = (int) (sp+0.32589*logN*logN -8.7411*logN+40);//(int) ((2*t-10)/20.0*sp);
		
		queue = new PlainBlock[queueCapacity];
		
		for(int i = 0; i < queue.length; ++i) 
			queue[i] = getDummyBlock(gen != null);
		
	}
	
	abstract void extractFromQueue(boolean[] pos) throws Exception;
	abstract public void flushOneTime(boolean[] pos) throws Exception;
	int cnt = 0;
	public void GEOdequeue() throws Exception {
		for(int i = 0; i < 3; ++i){
			boolean[] pos = getRandomPath();
			dequeue(pos);
		}
	}
	
	void dequeue(boolean[] pos) throws Exception {
		extractFromQueue(pos);
		flushOneTime(pos);
	}	
}

