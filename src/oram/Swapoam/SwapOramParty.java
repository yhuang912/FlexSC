package oram.Swapoam;

import java.io.InputStream;
import java.io.OutputStream;
import flexsc.*;
import oram.PlainBlock;
import oram.TreeBasedOramParty;



public abstract class SwapOramParty<T> extends TreeBasedOramParty<T> {
	public PlainBlock[] queue;
	public int queueCapacity;
	public SwapOramParty(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int cap, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, cap, m);
		
		queueCapacity = 100;
		queue = new PlainBlock[queueCapacity];
		
		for(int i = 0; i < queue.length; ++i) 
			queue[i] = getDummyBlock(gen != null);
		
	}
	
	abstract public void flushOneTime(boolean[] pos) throws Exception;
	int cnt = 0;	
	
	boolean[] nextPath()
	{
		boolean [] res = new boolean[logN];
		for(int i = 0; i < res.length; ++i)
			res[i] = false;
		return res;
	}
}

