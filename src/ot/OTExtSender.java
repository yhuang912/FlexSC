// Copyright (C) 2013 by Yan Huang <yhuang@cs.umd.edu>
// Improved by Xiao Shaun Wang <wangxiao@cs.umd.edu>

package ot;

import flexsc.Flag;
import flexsc.OTMODE;
import gc.GCSignal;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import network.Network;
import rand.ISAACProvider;

public class OTExtSender extends OTSender {
	static class SecurityParameter {
		public static final int k1 = 80; // number of columns in T
	}

	private static SecureRandom rnd;
	static {
		Security.addProvider(new ISAACProvider());
		try {
			rnd = SecureRandom.getInstance("ISAACRandom");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private OTReceiver rcver;
	private boolean[] s;
	private GCSignal[] keys;

	Cipher cipher;

	public OTExtSender(int msgBitLength, Network w) {
		super(msgBitLength, w);

		cipher = new Cipher();

		try {
			initialize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	int poolIndex = 0;

	@Override
	public void send(GCSignal[] m) {
		try {
			throw new Exception(
					"It doesn't make sense to do single OT with OT extension!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Everything in msgPairs are effective Sender's messages.
	 */
	GCSignal[][] keyPairs = new GCSignal[SecurityParameter.k1][2];

	public void send(GCSignal[][] msgPairs) throws IOException {
		GCSignal[][] pairs = new GCSignal[SecurityParameter.k1
		                                  + msgPairs.length][2];
		for (int i = 0; i < SecurityParameter.k1; i++) {
			pairs[i][0] = GCSignal.freshLabel(rnd);
			pairs[i][1] = GCSignal.freshLabel(rnd);
		}

		for (int i = SecurityParameter.k1; i < pairs.length; i++) {
			pairs[i][0] = msgPairs[i - SecurityParameter.k1][0];
			pairs[i][1] = msgPairs[i - SecurityParameter.k1][1];
		}

		reverseAndExtend(s, keys, msgBitLength, pairs, w, cipher);

		for (int i = 0; i < SecurityParameter.k1; i++) {
			keyPairs[i][0] = pairs[i][0];
			keyPairs[i][1] = pairs[i][1];
		}
		for (int i = 0; i < s.length; i++)
			s[i] = rnd.nextBoolean();
		keys = OTExtReceiver.reverseAndExtend(keyPairs, s,
				SecurityParameter.k1, w, cipher);
	}

	// Given s and keys, obliviously sends msgPairs which contains 'numOfPairs'
	// pairs of strings, each of length 'msgBitLength' bits.

	static void reverseAndExtend(boolean[] s, GCSignal[] keys,
			int msgBitLength, GCSignal[][] msgPairs, Network w, Cipher cipher) throws IOException {
		BigInteger[][] cphPairs = new BigInteger[SecurityParameter.k1][2];
		int numOfPairs = msgPairs.length;

		BitMatrix Q = new BitMatrix(numOfPairs, SecurityParameter.k1);
		//		BitMatrix tQ = new BitMatrix(SecurityParameter.k1, numOfPairs);


		for (int i = 0; i < SecurityParameter.k1; i++) {
			if(Flag.otMode == OTMODE.EXTENSIONOT)
				Flag.sw.startOTIO();
			cphPairs[i][0] = w.readBI();
			cphPairs[i][1] = w.readBI();
			if(Flag.otMode == OTMODE.EXTENSIONOT)
				Flag.sw.stopOTIO();
			if (s[i]) {
				Q.data[i] = cipher.decrypt(keys[i].bytes, cphPairs[i][1],
						numOfPairs);
			}
			else {
				Q.data[i] = cipher.decrypt(keys[i].bytes, cphPairs[i][0],
						numOfPairs);
			}
		}

		BitMatrix tQ = Q.transpose();

		BigInteger biS = fromBoolArray(s);

		GCSignal[][] y = new GCSignal[numOfPairs][2];
		for (int i = 0; i < numOfPairs; i++) {
			y[i][0] = cipher.enc(
					GCSignal.newInstance(tQ.data[i].toByteArray()),
					msgPairs[i][0], i);
			y[i][1] = cipher.enc(
					GCSignal.newInstance(tQ.data[i].xor(biS).toByteArray()),
					msgPairs[i][1], i);
			if(Flag.otMode == OTMODE.EXTENSIONOT)
				Flag.sw.startOTIO();
			y[i][0].send(w);
			y[i][1].send(w);
			if(Flag.otMode == OTMODE.EXTENSIONOT)
				Flag.sw.stopOTIO();
		}
		if(Flag.otMode == OTMODE.EXTENSIONOT)
			Flag.sw.startOTIO();
		w.flush();
		if(Flag.otMode == OTMODE.EXTENSIONOT)
			Flag.sw.stopOTIO();

	}

	private void initialize() throws Exception {
		w.writeInt(msgBitLength);
		w.flush();


		rcver = new NPOTReceiver(w);

		s = new boolean[SecurityParameter.k1];
		for (int i = 0; i < s.length; i++)
			s[i] = rnd.nextBoolean();

		keys = rcver.receive(s);
	}

	public static BigInteger fromBoolArray(boolean[] a) {
		BigInteger res = BigInteger.ZERO;
		for (int i = 0; i < a.length; i++)
			if (a[i])
				res = res.setBit(i);
		return res;
	}
}