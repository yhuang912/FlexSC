//Copyright (C) 2014 by Yan Huang <yhuang@cs.umd.edu>

package ot;

import gc.Signal;

import java.math.*;
import java.io.*;
import java.security.SecureRandom;

public class OTExtReceiver extends OTReceiver {
	private static SecureRandom rnd = new SecureRandom();

//	private int k1;
//	private int k2;
	private int msgBitLength;

	private OTSender snder;
	private BitMatrix T;
	private Signal[][] keyPairs;
 
	ObjectInputStream ois;
	ObjectOutputStream oos;
//	int numOfBaseOTs = 80;
	
	public OTExtReceiver(InputStream in, OutputStream out) throws Exception {
		super(in, out);

    	oos = new ObjectOutputStream(os);
    	ois = new ObjectInputStream(is);

		initialize();
	}

	public Signal[] receive(boolean[] choices) throws Exception {
		BigInteger[][] msgPairs = new BigInteger[OTExtSender.SecurityParameter.k1][2];
		BigInteger[][] cphPairs = new BigInteger[OTExtSender.SecurityParameter.k1][2];

		BigInteger biChoices = OTExtSender.fromBoolArray(choices);
		for (int i = 0; i < OTExtSender.SecurityParameter.k1; i++) {
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
//		data = new BigInteger[numOfChoices];
		for (int i = 0; i < choices.length; i++) {
			int sigma = choices[i] ? 1 : 0;
			res[i] = Signal.newInstance(Cipher.decrypt(i, tT.data[i],
					y[i][sigma], msgBitLength).toByteArray());
		}
		return res;
	}

	private void initialize() throws Exception {
//		k1 = ois.readInt();
//		k2 = ois.readInt();
		msgBitLength = ois.readInt();

//		snder = new NPOTSender(k1, k2, ois, oos);
		snder = new NPOTSender(OTExtSender.SecurityParameter.k2, is, os);

		T = new BitMatrix(numOfBaseOTs, OTExtSender.SecurityParameter.k1);
		T.initialize(rnd);

		keyPairs = new Signal[OTExtSender.SecurityParameter.k1][2];
		for (int i = 0; i < OTExtSender.SecurityParameter.k1; i++) {
//			keyPairs[i][0] = new BigInteger(k2, rnd);
//			keyPairs[i][1] = new BigInteger(k2, rnd);
			keyPairs[i][0] = Signal.freshLabel(rnd);
			keyPairs[i][1] = Signal.freshLabel(rnd);
		}

		snder.send(keyPairs);
	}

	@Override
	public Signal receive(boolean c) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}