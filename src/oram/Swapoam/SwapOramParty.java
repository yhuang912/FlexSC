package oram.Swapoam;

import java.io.InputStream;
import java.io.OutputStream;

import flexsc.*;
import oram.PlainBlock;
import oram.TreeBasedOramParty;
import test.Utils;



public abstract class SwapOramParty<T> extends TreeBasedOramParty<T> {
	public PlainBlock[] queue;
	public int queueCapacity;
	public SwapOramParty(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int cap, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, cap, m);
		
		queueCapacity = 55;
		queue = new PlainBlock[queueCapacity];
		
		for(int i = 0; i < queue.length; ++i) 
			queue[i] = getDummyBlock(gen != null);
	}
	
	abstract public void flushOneTime(boolean[] pos) throws Exception;
	int cnt = 0;	
	 
	//to be implemtned
	boolean[] nextPath()
	{
		boolean [] res = new boolean[logN];
		int temp = cnt;
		for(int i = res.length-1; i >= 0; --i) {
			res[i] = (temp&1)==1;
			temp>>=1;
		}
		cnt = (cnt+1)%N;
		return res;
	}
}

