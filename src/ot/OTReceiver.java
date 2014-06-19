package ot;

import gc.GCSignal;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class OTReceiver {
	InputStream is;
	OutputStream os;
	int msgBitLength;

	public OTReceiver (InputStream in, OutputStream out) {
		is = in;
		os = out;
	}
	
	public abstract GCSignal receive(boolean c) throws Exception;
	public abstract GCSignal[] receive(boolean[] c) throws Exception;
}
