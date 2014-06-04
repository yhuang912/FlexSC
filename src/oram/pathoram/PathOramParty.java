package oram.pathoram;

import java.io.InputStream;
import java.io.OutputStream;

import flexsc.*;
import oram.PlainBlock;
import oram.TreeBasedOramParty;


public abstract class PathOramParty<T> extends TreeBasedOramParty<T> {
	int lengthOfStash;
	public PlainBlock[] stash = new PlainBlock[lengthOfStash];
	static byte[] seed = new byte[512];
	public PathOramParty(InputStream is, OutputStream os, int N, int dataSize, int cap,
			Party p, Mode m, int sp) throws Exception {
		super(is, os, N, dataSize, p, cap, m);
		if(cap == 4)
			lengthOfStash = (int) (1.2 * sp - 7.24);
		else if(cap == 5)
			lengthOfStash = (int) (0.875*sp-7);
		else if(cap == 6)
			lengthOfStash = (int) (0.75*sp-7);
		else ;
		stash = new PlainBlock[lengthOfStash];
		if(gen != null) {
			for(int i = 0; i < stash.length; ++i) 
				stash[i] = getDummyBlock(true);
		}
		else {
			for(int i = 0; i < stash.length; ++i) 
				stash[i] = getDummyBlock(false);
		}
	}
}
