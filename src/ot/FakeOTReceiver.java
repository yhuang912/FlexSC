package ot;

import gcHalfANDs.GCSignal;

import java.io.*;
import java.util.*;

public class FakeOTReceiver extends OTReceiver {
	public FakeOTReceiver(InputStream in, OutputStream out) {
		super(in, out);
	}
	
	@Override
	public GCSignal receive(boolean c) throws Exception {
		GCSignal[] m = new GCSignal[2];
		m[0] = GCSignal.receive(is);
		m[1] = GCSignal.receive(is);
		return m[c?1:0];
	}

	@Override
	public GCSignal[] receive(boolean[] c) throws Exception {
		GCSignal[] res = new GCSignal[c.length];
		for (int i = 0; i < c.length; i++) {
			GCSignal[] m = new GCSignal[2];
			m[0] = GCSignal.receive(is);
			m[1] = GCSignal.receive(is);
			res[i] = m[c[i]?1:0];
		}
		return res;
	}
}

