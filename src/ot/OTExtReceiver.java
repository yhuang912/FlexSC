//Copyright (C) 2014 by Yan Huang <yhuang@cs.umd.edu>

package ot;

import gc.Signal;

import java.math.*;
import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;

import ot.OTExtSender.SecurityParameter;

public class OTExtReceiver extends OTReceiver {
	private static SecureRandom rnd = new SecureRandom();

	private int msgBitLength;

	private OTSender snder;
	private Signal[][] keyPairs;
 
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	public OTExtReceiver(InputStream in, OutputStream out) throws Exception {
		super(in, out);

    	oos = new ObjectOutputStream(os);
    	ois = new ObjectInputStream(is);

		initialize();
	}

	public Signal[] receive(boolean[] choices) throws Exception {
		boolean[] c = new boolean[SecurityParameter.k1 + choices.length];
		for (int i = 0; i < SecurityParameter.k1; i++) 
			c[i] = rnd.nextBoolean();
		for (int i = SecurityParameter.k1; i < c.length; i++) 
			c[i] = choices[i-SecurityParameter.k1];
		
		Signal[] received = reverseAndExtend(keyPairs, c, msgBitLength, ois, oos);
		
		Signal[] keys = new Signal[SecurityParameter.k1];
		boolean[] s = new boolean[SecurityParameter.k1];
		for (int i = 0; i < OTExtSender.SecurityParameter.k1; i++) { 
			keys[i] = received[i];
			s[i] = c[i];
		}
		for (int i = 0; i < OTExtSender.SecurityParameter.k1; i++) {
			keyPairs[i][0] = Signal.freshLabel(rnd);
			keyPairs[i][1] = Signal.freshLabel(rnd);
		}
		OTExtSender.reverseAndExtend(s, keys, msgBitLength, keyPairs, ois, oos);
		
		return Arrays.copyOfRange(received, SecurityParameter.k1, received.length);
	}

	static Signal[] reverseAndExtend(Signal[][] keyPairs, 
			boolean[] choices, int msgBitLength, 
			ObjectInputStream ois, ObjectOutputStream oos) throws Exception {
		
		BigInteger[][] msgPairs = new BigInteger[SecurityParameter.k1][2];
		BigInteger[][] cphPairs = new BigInteger[SecurityParameter.k1][2];

		BitMatrix T = new BitMatrix(choices.length, SecurityParameter.k1);
		T.initialize(rnd);

		BigInteger biChoices = OTExtSender.fromBoolArray(choices);
		for (int i = 0; i < SecurityParameter.k1; i++) {
			msgPairs[i][0] = T.data[i];
			msgPairs[i][1] = T.data[i].xor(biChoices);

			cphPairs[i][0] = Cipher.encrypt(new BigInteger(keyPairs[i][0].bytes), msgPairs[i][0],
					choices.length);
			cphPairs[i][1] = Cipher.encrypt(new BigInteger(keyPairs[i][1].bytes), msgPairs[i][1],
					choices.length);
		}

		oos.writeObject(cphPairs);
		oos.flush();
//		int bytelength;

		BitMatrix tT = T.transpose();

		BigInteger[][] y = new BigInteger[choices.length][2];
//		bytelength = (msgBitLength - 1) / 8 + 1;
		for (int i = 0; i < choices.length; i++) {
//			y[i][0] = Utils.readBigInteger(bytelength, ois);
//			y[i][1] = Utils.readBigInteger(bytelength, ois);
			y[i][0] = (BigInteger) ois.readObject();
			y[i][1] = (BigInteger) ois.readObject();
		}

		Signal[] res = new Signal[choices.length];

		for (int i = 0; i < choices.length; i++) {
			int sigma = choices[i] ? 1 : 0;
			res[i] = Signal.newInstance(Cipher.decrypt(i, tT.data[i],
					y[i][sigma], msgBitLength).toByteArray());
		}
		return res;
	}

	private void initialize() throws Exception {
		msgBitLength = ois.readInt();

		snder = new NPOTSender(OTExtSender.SecurityParameter.k1, is, os);

		keyPairs = new Signal[OTExtSender.SecurityParameter.k1][2];
		for (int i = 0; i < OTExtSender.SecurityParameter.k1; i++) {
			keyPairs[i][0] = Signal.freshLabel(rnd);
			keyPairs[i][1] = Signal.freshLabel(rnd);
		}

		snder.send(keyPairs);
		
		refillPool();
	}
	
	boolean[] poolChoices = new boolean[OTExtSender.OTPerBatch];
	Signal[] pool;
	int poolIndex = 0;
	private void refillPool() throws Exception {
    	for (int i = 0; i < poolChoices.length; i++) {
			poolChoices[i] = rnd.nextBoolean();
		}
		pool = receive(poolChoices);
		poolIndex = 0;
    }

	@Override
	public Signal receive(boolean c) throws Exception {
		oos.writeBoolean(poolChoices[poolIndex] ^ c);
		oos.flush();
		Signal[] signals = new Signal[2];
		signals[0] = Signal.receive(is);
		signals[1] = Signal.receive(is);
		Signal ret = signals[c?1:0].xor(pool[poolIndex]);
		
		poolIndex++;
		if (poolIndex == OTExtSender.OTPerBatch)
			refillPool();
		
		return ret;
	}
}