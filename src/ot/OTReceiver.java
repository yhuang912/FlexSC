// Copyright (C) 2013 by Yan Huang <yhuang@cs.umd.edu>
// Improved by Xiao Shaun Wang <wangxiao@cs.umd.edu>

package ot;

import gc.GCSignal;

import java.io.IOException;

import network.Network;

public abstract class OTReceiver {
	Network w;
	int msgBitLength;

	public OTReceiver(Network w) {
		this.w = w;
	}

	public abstract GCSignal receive(boolean c) throws IOException;

	public abstract GCSignal[] receive(boolean[] c) throws IOException;
}
