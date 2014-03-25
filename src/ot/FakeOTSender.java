package ot;

import gc.Signal;

import java.io.*;

public class FakeOTSender extends OTSender {
	public FakeOTSender(int bitLen, InputStream in, OutputStream out) {
		super(bitLen, in, out);
	}
	
	@Override
	public void send(Signal[] m) throws Exception {
		m[0].send(os);
		m[1].send(os);
	}

	@Override
	public void send(Signal[][] m) throws Exception {
		for (int i = 0; i < m.length; i++) {
			m[i][0].send(os);
			m[i][1].send(os);
		}
	}
}