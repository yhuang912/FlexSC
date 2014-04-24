// Copyright (C) 2014 by Yan Huang <yhuang@cs.umd.edu>

package ot;

import gc.Signal;

import java.math.*;
import java.io.*;
import java.security.SecureRandom;


public class OTExtSender extends OTSender {
	static final int OTPerBatch = 1000;	// should be bigger than 80
	
    static class SecurityParameter {
		public static final int k1 = 80;    // number of columns in T
//		public static final int k2 = k1 + OTPerBatch;	// number of rows in T (i.e., the string length in the base OT)
    }

    private static SecureRandom rnd = new SecureRandom();
    private OTReceiver rcver;
    private boolean[] s;
    private Signal[] keys;
    
    ObjectInputStream ois;
	ObjectOutputStream oos;
//	int numOfPairs;
	
    public OTExtSender(int msgBitLength, InputStream in, OutputStream out) throws Exception {
    	super(msgBitLength, in, out);
    	
    	ois = new ObjectInputStream(is);
    	oos = new ObjectOutputStream(os);

    	initialize();
    }

    Signal[][] pool = new Signal[OTPerBatch][2];
    int poolIndex = 0;
	@Override
	public void send(Signal[] m) throws Exception {
		boolean x = ois.readBoolean();
		int i = x?1:0;
		pool[poolIndex][i].xor(m[0]).send(os);
		pool[poolIndex][1-i].xor(m[1]).send(os);
		os.flush();

		poolIndex++;
		
		if (poolIndex == OTPerBatch)
			refillPool();
	}
	
    public void send(Signal[][] msgPairs) throws Exception {    	
    	Signal[][] pairs = new Signal[SecurityParameter.k1 + msgPairs.length][2];
    	for (int i = 0; i < SecurityParameter.k1; i++) {
    		pairs[i][0] = Signal.freshLabel(rnd);
    		pairs[i][1] = Signal.freshLabel(rnd);
    	}
    	
    	for (int i = SecurityParameter.k1; i < pairs.length; i++) {
    		pairs[i][0] = msgPairs[i-SecurityParameter.k1][0];
    		pairs[i][1] = msgPairs[i-SecurityParameter.k1][1];
    	}
    	
    	reverseAndExtend(s, keys, msgBitLength, pairs, ois, oos);
    	
    	Signal[][] keyPairs = new Signal[SecurityParameter.k1][2];
    	for (int i = 0; i < SecurityParameter.k1; i++) {
    		keyPairs[i][0] = pairs[i][0];
    		keyPairs[i][1] = pairs[i][1];
    	}
    	for (int i = 0; i < s.length; i++)
			s[i] = rnd.nextBoolean();
    	keys = OTExtReceiver.reverseAndExtend(keyPairs, s, SecurityParameter.k1, ois, oos);
    }

	// Given s and keys, obliviously sends msgPairs which contains 'numOfPairs'
	// pair of strings, each of length 'msgBitLength' bits.  
    static void reverseAndExtend(boolean[] s, Signal[] keys, 
    		int msgBitLength, Signal[][] msgPairs,
    		ObjectInputStream ois, ObjectOutputStream oos) throws Exception {
    	BigInteger[][] cphPairs = (BigInteger[][]) ois.readObject();
    	int numOfPairs = msgPairs.length;
//    	int bytelength;

    	BitMatrix Q = new BitMatrix(numOfPairs, SecurityParameter.k1);

		for (int i = 0; i < SecurityParameter.k1; i++) {
		    if (s[i])
				Q.data[i] = Cipher.decrypt(new BigInteger(keys[i].bytes), cphPairs[i][1], numOfPairs);
			else
				Q.data[i] = Cipher.decrypt(new BigInteger(keys[i].bytes), cphPairs[i][0], numOfPairs);
		}

		BitMatrix tQ = Q.transpose();
	
		BigInteger biS = fromBoolArray(s);
		BigInteger[][] y = new BigInteger[numOfPairs][2];
		for (int i = 0; i < numOfPairs; i++) {
		    y[i][0] = Cipher.encrypt(i, tQ.data[i],          new BigInteger(msgPairs[i][0].bytes), msgBitLength);
		    y[i][1] = Cipher.encrypt(i, tQ.data[i].xor(biS), new BigInteger(msgPairs[i][1].bytes), msgBitLength);
		}
	
//		bytelength = (msgBitLength-1)/8 + 1;
		for (int i = 0; i < numOfPairs; i++) {
//		    Utils.writeBigInteger(y[i][0], bytelength, oos);
//		    Utils.writeBigInteger(y[i][1], bytelength, oos);
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
			pool[i][0] = Signal.freshLabel(rnd);
			pool[i][1] = Signal.freshLabel(rnd);
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