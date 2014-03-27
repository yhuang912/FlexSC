// Copyright (C) 2013 by Yan Huang <yhuang@cs.umd.edu>

package ot;

import gc.Signal;

import java.math.*;
import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;


public class NPOTReceiver extends OTReceiver {
    private static SecureRandom rnd = new SecureRandom();

    private BigInteger p, q, g, C;
    private BigInteger gr;

    private BigInteger[][] pk;

    private BigInteger[] keys;
	ObjectInputStream ois;
	ObjectOutputStream oos;

    public NPOTReceiver(InputStream in, OutputStream out) throws Exception {
    	super(in, out);
    	oos = new ObjectOutputStream(out);
    	ois = new ObjectInputStream(in);

        initialize();
    }

    @Override
    public Signal receive(boolean c) throws Exception {
    	return receive(new boolean[]{c})[0];
    }

    public Signal[] receive(boolean[] choices) throws Exception {
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

    private Signal[] step2(boolean[] choices) throws Exception {
        BigInteger[][] msg = (BigInteger[][]) ois.readObject();

        Signal[] data = new Signal[choices.length];
        for (int i = 0; i < choices.length; i++) {
            int sigma = choices[i] ? 1 : 0;
			data[i] = Signal.newInstance(Cipher.decrypt(keys[i], msg[i][sigma],
					msgBitLength).toByteArray());
        }
        return data;
    }
}