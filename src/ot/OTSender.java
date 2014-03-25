package ot;

import gc.Signal;

import java.io.*;

public abstract class OTSender {
	InputStream is;
	OutputStream os;
	int msgBitLength;

	public OTSender (int bitLen, InputStream in, OutputStream out) {
		is = in;
		os = out;
		msgBitLength = bitLen;
	}
	
	public abstract void send(Signal[] m) throws Exception;
	public abstract void send(Signal[][] m) throws Exception;
}