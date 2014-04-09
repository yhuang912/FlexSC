package oramgc.kaiminOram;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import oramgc.TreeBasedOramParty;
import oramgc.OramParty.BlockInBinary;
import test.Utils;

public abstract class KaiminOramParty extends TreeBasedOramParty {
	BlockInBinary[] queue;
	final int queueCapacity = 10;
	public KaiminOramParty(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int capacity) throws Exception {
		super(is, os, N, dataSize, p, capacity);
		queue = new BlockInBinary[queueCapacity];
		for(int i = 0; i < queueCapacity; ++i)
			queue[i] = getDummyBlock();
	}
	
	public void flush() throws Exception{
		while(true){
			int a = commonRandom.nextInt(3);
			if(a == 0)
				return;
			else{
				boolean[] pos = new boolean[lengthOfPos];
				for(int i = 0; i < lengthOfPos; ++i)
					pos[i] = commonRandom.nextBoolean();
				flushOneTime(pos);
			}
		}	
	}
	abstract public void flushOneTime(boolean[] pos) throws Exception;
}
