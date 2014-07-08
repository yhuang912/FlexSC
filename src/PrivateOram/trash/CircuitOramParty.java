package PrivateOram.trash;

import java.io.InputStream;
import java.io.OutputStream;

import oram.PlainBlock;
import flexsc.Mode;
import flexsc.Party;


public abstract class CircuitOramParty<T>  {
	
	public CircuitOramParty(InputStream is, OutputStream os, int N, int dataSize,
			Party p, int cap, Mode m, int sp) throws Exception {
		
		//to be tuned
	}
	
	abstract public void flushOneTime(boolean[] pos) throws Exception;

	

}

