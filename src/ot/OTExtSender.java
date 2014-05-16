// Copyright (C) 2014 by Yan Huang <yhuang@cs.umd.edu>

package ot;

import gc.GCSignal;

import java.math.*;
import java.io.*;
import java.security.SecureRandom;


public class OTExtSender extends OTSender {
	static final int OTPerBatch = 1000;	// should be bigger than 80
	
    static class SecurityParameter {
		public static final int k1 = 80;    // number of columns in T
    }

    private static SecureRandom rnd = new SecureRandom();
    private OTReceiver rcver;
    private boolean[] s;
    private GCSignal[] keys;
    
    ObjectInputStream ois;
	ObjectOutputStream oos;
	
	Cipher cipher;
	
    public OTExtSender(int msgBitLength, InputStream in, OutputStream out) throws Exception {
    	super(msgBitLength, in, out);
    	
    	ois = new ObjectInputStream(is);
    	oos = new ObjectOutputStream(os);

    	cipher = new Cipher();
    	
    	initialize();
    }

    GCSignal[][] pool = new GCSignal[OTPerBatch][2];
    int poolIndex = 0;
	@Override
	public void send(GCSignal[] m) throws Exception {
		boolean x = ois.readBoolean();
		int i = x?1:0;
		pool[poolIndex][i].xor(m[0]).send(os);
		pool[poolIndex][1-i].xor(m[1]).send(os);
		os.flush();

		poolIndex++;
		
		if (poolIndex == OTPerBatch)
			refillPool();
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
    	
    	reverseAndExtend(s, keys, msgBitLength, pairs, ois, oos, cipher);
    	
    	GCSignal[][] keyPairs = new GCSignal[SecurityParameter.k1][2];
    	for (int i = 0; i < SecurityParameter.k1; i++) {
    		keyPairs[i][0] = pairs[i][0];
    		keyPairs[i][1] = pairs[i][1];
    	}
    	for (int i = 0; i < s.length; i++)
			s[i] = rnd.nextBoolean();
    	keys = OTExtReceiver.reverseAndExtend(keyPairs, s, SecurityParameter.k1, ois, oos, cipher);
    }

	// Given s and keys, obliviously sends msgPairs which contains 'numOfPairs'
	// pair of strings, each of length 'msgBitLength' bits.  
    static void reverseAndExtend(boolean[] s, GCSignal[] keys, 
    		int msgBitLength, GCSignal[][] msgPairs,
    		ObjectInputStream ois, ObjectOutputStream oos, Cipher cipher) throws Exception {
    	BigInteger[][] cphPairs = (BigInteger[][]) ois.readObject();
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
		BigInteger[][] y = new BigInteger[numOfPairs][2];
		for (int i = 0; i < numOfPairs; i++) {
//		    y[i][0] = cipher.encrypt(i, tQ.data[i].toByteArray(),          new BigInteger(msgPairs[i][0].bytes), msgBitLength);
//		    y[i][1] = cipher.encrypt(i, tQ.data[i].xor(biS).toByteArray(), new BigInteger(msgPairs[i][1].bytes), msgBitLength);
//		    y[i][0] = cipher.encryptNoBI(i, tQ.data[i].toByteArray(),          new BigInteger(msgPairs[i][0].bytes), msgBitLength);
//		    y[i][1] = cipher.encryptNoBI(i, tQ.data[i].xor(biS).toByteArray(), new BigInteger(msgPairs[i][1].bytes), msgBitLength);
			y[i][0] = cipher.encrypt(tQ.data[i].toByteArray(),          new BigInteger(msgPairs[i][0].bytes), i);
		    y[i][1] = cipher.encrypt(tQ.data[i].xor(biS).toByteArray(), new BigInteger(msgPairs[i][1].bytes), i);
		}

		for (int i = 0; i < numOfPairs; i++) {
			oos.writeObject(y[i][0]);
			oos.writeObject(y[i][1]);
		}
		oos.flush();
    }

    private void initialize() throws Exception {
		oos.writeInt(msgBitLength);
		oos.flush();
	
		rcver = new NPOTReceiver(is, os);
	
		s = new boolean[SecurityParameter.k1];
		for (int i = 0; i < s.length; i++)
			s[i] = rnd.nextBoolean();
	
		keys = rcver.receive(s);
		
		refillPool();
    }
    
    private void refillPool() throws Exception {
    	for (int i = 0; i < pool.length; i++) {
			pool[i][0] = GCSignal.freshLabel(rnd);
			pool[i][1] = GCSignal.freshLabel(rnd);
		}
		send(pool);
		poolIndex = 0;
    }
	
    public static BigInteger fromBoolArray(boolean[] a) {
    	BigInteger res = BigInteger.ZERO;
    	for (int i = 0; i < a.length; i++)
    		if (a[i])
    			res = res.setBit(i);
    	return res;
    }
}