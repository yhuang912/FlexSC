// Copyright (C) 2014 by Yan Huang <yhuang@cs.umd.edu>

package ot;

import gc.GCSignal;

import java.math.*;
import java.io.*;
import java.security.SecureRandom;

import network.RWBigInteger;


public class OTExtSender extends OTSender {
    static class SecurityParameter {
		public static final int k1 = 80;    // number of columns in T
    }

    private static SecureRandom rnd = new SecureRandom();
    private OTReceiver rcver;
    private boolean[] s;
    private GCSignal[] keys;
    
	Cipher cipher;
	
    public OTExtSender(int msgBitLength, InputStream in, OutputStream out) throws Exception {
    	super(msgBitLength, in, out);
    	
    	cipher = new Cipher();
    	
    	initialize();
    }

    int poolIndex = 0;
	@Override
	public void send(GCSignal[] m) throws Exception {
		throw new Exception("It doesn't make sense to do single OT with OT extension!");
	}
	
	/*
	 * Everything in msgPairs are effective Sender's messages. 
	 * 
	 */
    public void send(GCSignal[][] msgPairs) throws Exception {    	
    	GCSignal[][] pairs = new GCSignal[SecurityParameter.k1 + msgPairs.length][2];
    	for (int i = 0; i < SecurityParameter.k1; i++) {
    		pairs[i][0] = GCSignal.freshLabel(rnd);
    		pairs[i][1] = GCSignal.freshLabel(rnd);
    	}
    	
    	for (int i = SecurityParameter.k1; i < pairs.length; i++) {
    		pairs[i][0] = msgPairs[i-SecurityParameter.k1][0];
    		pairs[i][1] = msgPairs[i-SecurityParameter.k1][1];
    	}
    	
    	reverseAndExtend(s, keys, msgBitLength, pairs, is, os, cipher);
    	
    	GCSignal[][] keyPairs = new GCSignal[SecurityParameter.k1][2];
    	for (int i = 0; i < SecurityParameter.k1; i++) {
    		keyPairs[i][0] = pairs[i][0];
    		keyPairs[i][1] = pairs[i][1];
    	}
    	for (int i = 0; i < s.length; i++)
			s[i] = rnd.nextBoolean();
    	keys = OTExtReceiver.reverseAndExtend(keyPairs, s, SecurityParameter.k1, is, os, cipher);
    }

	// Given s and keys, obliviously sends msgPairs which contains 'numOfPairs'
	// pairs of strings, each of length 'msgBitLength' bits.  
    static void reverseAndExtend(boolean[] s, GCSignal[] keys, 
    		int msgBitLength, GCSignal[][] msgPairs, InputStream is, OutputStream os, Cipher cipher) throws Exception {
    	
    	BigInteger[][] cphPairs = new BigInteger[SecurityParameter.k1][2];
    	for (int i = 0; i < SecurityParameter.k1; i++) {
    		cphPairs[i][0] = RWBigInteger.readBI(is);
    		cphPairs[i][1] = RWBigInteger.readBI(is);
		}
    	
    	int numOfPairs = msgPairs.length;

    	BitMatrix Q = new BitMatrix(numOfPairs, SecurityParameter.k1);

		for (int i = 0; i < SecurityParameter.k1; i++) {
		    if (s[i])
				Q.data[i] = cipher.decrypt(keys[i].bytes, cphPairs[i][1], numOfPairs);
			else
				Q.data[i] = cipher.decrypt(keys[i].bytes, cphPairs[i][0], numOfPairs);
		}

		BitMatrix tQ = Q.transpose();
	
		BigInteger biS = fromBoolArray(s);
		
		GCSignal[][] y = new GCSignal[numOfPairs][2];
		for (int i = 0; i < numOfPairs; i++) {
			y[i][0] = cipher.enc(GCSignal.newInstance(tQ.data[i].toByteArray()),  msgPairs[i][0], i);
		    y[i][1] = cipher.enc(GCSignal.newInstance(tQ.data[i].xor(biS).toByteArray()), msgPairs[i][1], i);
		}

		for (int i = 0; i < numOfPairs; i++) {
			y[i][0].send(os);
			y[i][1].send(os);
		}
		
		os.flush();
    }

    private void initialize() throws Exception {
		os.write(msgBitLength);
		os.flush();
	
		rcver = new NPOTReceiver(is, os);
	
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