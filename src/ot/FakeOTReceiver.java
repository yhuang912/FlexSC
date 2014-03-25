package ot;

import gc.Signal;

import java.io.*;
import java.util.*;

public class FakeOTReceiver extends OTReceiver {
	public FakeOTReceiver(InputStream in, OutputStream out) {
		super(in, out);
	}
	
	@Override
	public Signal receive(boolean c) throws Exception {
		Signal[] m = new Signal[2];
		m[0] = Signal.receive(is);
		m[1] = Signal.receive(is);
		return m[c?1:0];
	}

	@Override
	public Signal[] receive(boolean[] c) throws Exception {
		Signal[] res = new Signal[c.length];
		for (int i = 0; i < c.length; i++) {
			Signal[] m = new Signal[2];
			m[0] = Signal.receive(is);
			m[1] = Signal.receive(is);
			res[i] = m[c[i]?1:0];
		}
		return res;
	}
}

