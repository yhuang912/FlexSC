package ot;

import gc.Signal;

import java.io.*;
import java.util.*;

public abstract class OTReceiver {
	InputStream is;
	OutputStream os;
	int msgBitLength;

	public OTReceiver (InputStream in, OutputStream out) {
		is = in;
		os = out;
	}
	
	public abstract Signal receive(boolean c) throws Exception;
	public abstract Signal[] receive(boolean[] c) throws Exception;
}
