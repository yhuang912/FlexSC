// Copyright (C) 2013 by Yan Huang <yhuang@cs.umd.edu>

package ot;

import gc.GCSignal;

import java.math.*;
import java.io.*;
import java.security.SecureRandom;


public class NPOTReceiver extends OTReceiver {
    private static SecureRandom rnd = new SecureRandom();

    private BigInteger p, q, g, C;
    private BigInteger gr;

    private BigInteger[][] pk;

    private BigInteger[] keys;
	ObjectInputStream ois;
	ObjectOutputStream oos;

	Cipher cipher;
	
    public NPOTReceiver(InputStream in, OutputStream out) throws Exception {
    	super(in, out);
    	oos = new ObjectOutputStream(out);
    	ois = new ObjectInputStream(in);
    	
    	cipher = new Cipher();

        initialize();
    }

    @Override
    public GCSignal receive(boolean c) throws Exception {
    	return receive(new boolean[]{c})[0];
    }

    public GCSignal[] receive(boolean[] choices) throws Exception {
        step1(choices);
        return step2(choices);
    }
    
    private void initialize() throws Exception {
        C  = (BigInteger) ois.readObject();
        p  = (BigInteger) ois.readObject();
        q  = (BigInteger) ois.readObject();
        g  = (BigInteger) ois.readObject();
        gr = (BigInteger) ois.readObject();
        msgBitLength = ois.readInt();
    }

    private void step1(boolean[] choices) throws Exception {
    	keys = new BigInteger[choices.length];
        pk = new BigInteger[choices.length][2];
        BigInteger[] pk0 = new BigInteger[choices.length];
        for (int i = 0; i < choices.length; i++) {
        	BigInteger k = (new BigInteger(q.bitLength(), rnd)).mod(q);
        	BigInteger gk = g.modPow(k, p);
        	BigInteger C_over_gk = C.multiply(gk.modInverse(p)).mod(p);
        	keys[i] = gr.modPow(k, p);
        	
            int sigma = choices[i] ? 1 : 0;
            pk[i][sigma] = gk;
            pk[i][1-sigma] = C_over_gk;

            pk0[i] = pk[i][0];
        }

        oos.writeObject(pk0);
        oos.flush();
    }

    private GCSignal[] step2(boolean[] choices) throws Exception {
        BigInteger[][] msg = (BigInteger[][]) ois.readObject();

        GCSignal[] data = new GCSignal[choices.length];
        for (int i = 0; i < choices.length; i++) {
            int sigma = choices[i] ? 1 : 0;
			data[i] = GCSignal.newInstance(cipher.decrypt(keys[i].toByteArray(), msg[i][sigma],
					msgBitLength).toByteArray());
        }
        return data;
    }
}