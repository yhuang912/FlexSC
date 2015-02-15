// Copyright (C) by Xiao Shaun Wang <wangxiao@cs.umd.edu>

package ot;

import flexsc.Flag;
import gc.GCGenComp;
import gc.GCSignal;

import java.io.IOException;
import java.util.Arrays;

import network.Network;

public class OTPreprocessSender  extends OTSender {
	OTExtSender sender;
	public OTPreprocessSender(int msgBitLength, Network w) {
		super(msgBitLength, w);
		sender = new OTExtSender(msgBitLength, w);
		fillup();
	}

	GCSignal[][] buffer = new GCSignal[Flag.PreProcessOTbufferSize][2];
	int bufferusage = 0;

	public void fillup () {

		w.flush();
		while(bufferusage < Flag.PreProcessOTbufferSize) {
			int l = Math.min(Flag.PreProcessOTRefillLength, Flag.PreProcessOTbufferSize-bufferusage);
			for(int i = bufferusage; i < bufferusage+l; ++i)
				buffer[i] = GCGenComp.genPair();
			try {
				sender.send(Arrays.copyOfRange(buffer, bufferusage, bufferusage+l));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bufferusage +=l;
			System.out.println(bufferusage/(double)Flag.PreProcessOTbufferSize);
		}
	}


	public  void send(GCSignal[] m) throws IOException {
		Flag.sw.startOTIO();
		int z = w.readInt();
		Flag.sw.stopOTIO();
		bufferusage--;
		if(z == 0) {
			pair[0] = m[0].xor(buffer[bufferusage][0]);
			pair[1] = m[1].xor(buffer[bufferusage][1]);
		}
		else {
			pair[0] = m[0].xor(buffer[bufferusage][1]);
			pair[1] = m[1].xor(buffer[bufferusage][0]);
		}
		Flag.sw.startOTIO();
		pair[0].send(w);
		pair[1].send(w);
		Flag.sw.startOTIO();

		if(bufferusage == 0)
			fillup();
	}

	GCSignal[] pair = new GCSignal[2];
	public void send(GCSignal[][] m) throws IOException {
		if(bufferusage < m.length)
			fillup();
		Flag.sw.startOTIO();
		byte[] z = w.readBytes(m.length);
		Flag.sw.stopOTIO();
		for(int i = 0; i < m.length; ++i) {
			bufferusage--;
			if(z[i] == 0) {
				pair[0] = m[i][0].xor(buffer[bufferusage][0]);
				pair[1] = m[i][1].xor(buffer[bufferusage][1]);
			}
			else {
				pair[0] = m[i][0].xor(buffer[bufferusage][1]);
				pair[1] = m[i][1].xor(buffer[bufferusage][0]);
			}
			Flag.sw.startOTIO();
			pair[0].send(w);
			pair[1].send(w);
			Flag.sw.startOTIO();
		}
	}
}
