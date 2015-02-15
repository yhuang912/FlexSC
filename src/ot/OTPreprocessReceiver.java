// Copyright (C) by Xiao Shaun Wang <wangxiao@cs.umd.edu>

package ot;

import flexsc.CompEnv;
import flexsc.Flag;
import gc.GCSignal;

import java.io.IOException;
import java.util.Arrays;

import network.Network;

public class OTPreprocessReceiver  extends OTReceiver {
	GCSignal[] buffer = new GCSignal[Flag.PreProcessOTbufferSize];
	boolean[] choose = new boolean[Flag.PreProcessOTbufferSize];
	int bufferusage = 0;

	public void fillup() {
			w.flush();
		while(bufferusage < Flag.PreProcessOTbufferSize) {
			int l = Math.min(Flag.PreProcessOTRefillLength, Flag.PreProcessOTbufferSize-bufferusage);
			
			for(int i = bufferusage; i < bufferusage+l; ++i)
				choose[i] = CompEnv.rnd.nextBoolean();
			GCSignal[] kc = null;
			try {
				kc = reciever.receive(Arrays.copyOfRange(choose, bufferusage, bufferusage+l));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.arraycopy(kc, 0, buffer, bufferusage, kc.length);
			bufferusage += l;
		}
		
	}

	OTExtReceiver reciever;
	public OTPreprocessReceiver(Network w) {
		super(w);
		reciever = new OTExtReceiver(w);
		fillup();
	}


	public GCSignal receive(boolean b) throws IOException {
		bufferusage--;
		int z = (b^choose[bufferusage]) ? 1 : 0;
		Flag.sw.startOTIO();
		w.writeInt(z);
		w.flush();
		GCSignal[] y = new GCSignal[]{GCSignal.receive(w),  GCSignal.receive(w)};
		Flag.sw.stopOTIO();
		if(bufferusage == 0)
			fillup();
		return y[b?1:0].xor(buffer[bufferusage]);
	}

	public GCSignal[] receive(boolean[] b) throws IOException {
		if(bufferusage < b.length)
			fillup();
		byte[] z = new byte[b.length];
		int tmp = bufferusage;
		for(int i = 0; i < b.length; ++i) {
			--tmp;
			z[i] = (b[i]^choose[tmp]) ? (byte)1 : (byte)0;
		}
		Flag.sw.startOTIO();
		w.writeByte(z, z.length);
		w.flush();
		Flag.sw.stopOTIO();
		GCSignal[] ret = new GCSignal[b.length];
		for(int i = 0; i < b.length; ++i) {
			bufferusage--;
			Flag.sw.startOTIO();
			GCSignal[] y = new GCSignal[]{GCSignal.receive(w),  GCSignal.receive(w)};
			Flag.sw.stopOTIO();
			ret[i] = y[b[i]?1:0].xor(buffer[bufferusage]);
		}
		return ret;
	}
}
