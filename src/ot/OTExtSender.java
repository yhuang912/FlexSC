// Copyright (C) 2014 by Yan Huang <yhuang@cs.umd.edu>

package ot;

import gc.Signal;

import java.math.*;
import java.io.*;
import java.security.SecureRandom;


public class OTExtSender extends OTSender {
    static class SecurityParameter {
    	protected static final int numOfBaseOTs = 100;	// should be bigger than 80
		public static final int k1 = 80;    // number of columns in T
		public static final int k2 = k1 + numOfBaseOTs;	// number of rows in T (i.e., the string length in the base OT)
    }

    private static SecureRandom rnd = new SecureRandom();
    private OTReceiver rcver;
    private boolean[] s;
    private Signal[] keys;
    
    ObjectInputStream ois;
	ObjectOutputStream oos;
	int numOfPairs;
	
    public OTExtSender(int msgBitLength, InputStream in, OutputStream out) throws Exception {
    	super(msgBitLength, in, out);
    	
    	ois = new ObjectInputStream(is);
    	oos = new ObjectOutputStream(os);

    	initialize();
    }

	@Override
	public void send(Signal[] m) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
    public void send(Signal[][] msgPairs) throws Exception {
    	BigInteger[][] cphPairs = (BigInteger[][]) ois.readObject();
    	numOfPairs = msgPairs.length;
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
//		oos.writeInt(SecurityParameter.k1);
//		oos.writeInt(SecurityParameter.k2);
		oos.writeInt(msgBitLength);
		oos.flush();
	
		rcver = new NPOTReceiver(is, os);
	
		s = new boolean[SecurityParameter.k1];
		for (int i = 0; i < s.length; i++)
			s[i] = rnd.nextBoolean();
	
		keys = rcver.receive(s);
//		keys = rcver.getData();
    }
	
    public static BigInteger fromBoolArray(boolean[] a) {
    	BigInteger res = BigInteger.ZERO;
    	for (int i = 0; i < a.length; i++)
    		if (a[i])
    			res = res.setBit(i);
    	return res;
    }
}