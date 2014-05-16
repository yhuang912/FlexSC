//Copyright (C) 2014 by Yan Huang <yhuang@cs.umd.edu>

package ot;

import gc.GCSignal;

import java.math.*;
import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;

import ot.OTExtSender.SecurityParameter;

public class OTExtReceiver extends OTReceiver {
	private static SecureRandom rnd = new SecureRandom();

	private int msgBitLength;

	private OTSender snder;
	private GCSignal[][] keyPairs;
 
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	Cipher cipher;
	
	public OTExtReceiver(InputStream in, OutputStream out) throws Exception {
		super(in, out);

    	oos = new ObjectOutputStream(os);
    	ois = new ObjectInputStream(is);

    	cipher = new Cipher();
    	
		initialize();
	}

	public GCSignal[] receive(boolean[] choices) throws Exception {
		boolean[] c = new boolean[SecurityParameter.k1 + choices.length];
		for (int i = 0; i < SecurityParameter.k1; i++) 
			c[i] = rnd.nextBoolean();
		for (int i = SecurityParameter.k1; i < c.length; i++) 
			c[i] = choices[i-SecurityParameter.k1];
		
		GCSignal[] received = reverseAndExtend(keyPairs, c, msgBitLength, ois, oos, cipher);
		
		GCSignal[] keys = new GCSignal[SecurityParameter.k1];
		boolean[] s = new boolean[SecurityParameter.k1];
		for (int i = 0; i < OTExtSender.SecurityParameter.k1; i++) { 
			keys[i] = received[i];
			s[i] = c[i];
		}
		for (int i = 0; i < OTExtSender.SecurityParameter.k1; i++) {
			keyPairs[i][0] = GCSignal.freshLabel(rnd);
			keyPairs[i][1] = GCSignal.freshLabel(rnd);
		}
		OTExtSender.reverseAndExtend(s, keys, msgBitLength, keyPairs, ois, oos, cipher);
		
		return Arrays.copyOfRange(received, SecurityParameter.k1, received.length);
	}

	static GCSignal[] reverseAndExtend(GCSignal[][] keyPairs, 
			boolean[] choices, int msgBitLength, 
			ObjectInputStream ois, ObjectOutputStream oos, Cipher cipher) throws Exception {
		
		BigInteger[][] msgPairs = new BigInteger[SecurityParameter.k1][2];
		BigInteger[][] cphPairs = new BigInteger[SecurityParameter.k1][2];

		BitMatrix T = new BitMatrix(choices.length, SecurityParameter.k1);
		T.initialize(rnd);

		BigInteger biChoices = OTExtSender.fromBoolArray(choices);
		for (int i = 0; i < SecurityParameter.k1; i++) {
			msgPairs[i][0] = T.data[i];
			msgPairs[i][1] = T.data[i].xor(biChoices);

			cphPairs[i][0] = cipher.encrypt(keyPairs[i][0].bytes, msgPairs[i][0],
					choices.length);
			cphPairs[i][1] = cipher.encrypt(keyPairs[i][1].bytes, msgPairs[i][1],
					choices.length);
		}

		oos.writeObject(cphPairs);
		oos.flush();

		BitMatrix tT = T.transpose();

		BigInteger[][] y = new BigInteger[choices.length][2];
		for (int i = 0; i < choices.length; i++) {
			y[i][0] = (BigInteger) ois.readObject();
			y[i][1] = (BigInteger) ois.readObject();
		}

		GCSignal[] res = new GCSignal[choices.length];

		for (int i = 0; i < choices.length; i++) {
			int sigma = choices[i] ? 1 : 0;
//			res[i] = GCSignal.newInstance(cipher.decryptNoBI(i, tT.data[i].toByteArray(),
//					y[i][sigma], msgBitLength).toByteArray());
			res[i] = GCSignal.newInstance(cipher.decrypt(tT.data[i].toByteArray(),
					y[i][sigma], i).toByteArray());

		}
		return res;
	}

	private void initialize() throws Exception {
		msgBitLength = ois.readInt();

		snder = new NPOTSender(OTExtSender.SecurityParameter.k1, is, os);

		keyPairs = new GCSignal[OTExtSender.SecurityParameter.k1][2];
		for (int i = 0; i < OTExtSender.SecurityParameter.k1; i++) {
			keyPairs[i][0] = GCSignal.freshLabel(rnd);
			keyPairs[i][1] = GCSignal.freshLabel(rnd);
		}

		snder.send(keyPairs);
		
		refillPool();
	}
	
	boolean[] poolChoices = new boolean[OTExtSender.OTPerBatch];
	GCSignal[] pool;
	int poolIndex = 0;
	private void refillPool() throws Exception {
    	for (int i = 0; i < poolChoices.length; i++) {
			poolChoices[i] = rnd.nextBoolean();
		}
		pool = receive(poolChoices);
		poolIndex = 0;
    }

	@Override
	public GCSignal receive(boolean c) throws Exception {
		oos.writeBoolean(poolChoices[poolIndex] ^ c);
		oos.flush();
		GCSignal[] signals = new GCSignal[2];
		signals[0] = GCSignal.receive(is);
		signals[1] = GCSignal.receive(is);
		GCSignal ret = signals[c?1:0].xor(pool[poolIndex]);
		
		poolIndex++;
		if (poolIndex == OTExtSender.OTPerBatch)
			refillPool();
		
		return ret;
	}
}