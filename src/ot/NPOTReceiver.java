// Copyright (C) 2013 by Yan Huang <yhuang@cs.umd.edu>

package ot;

import flexsc.Flag;
import gc.GCSignal;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import network.RWBigInteger;
import rand.ISAACProvider;


public class NPOTReceiver extends OTReceiver {
//    private static SecureRandom rnd = new SecureRandom();
	static SecureRandom rnd;
	static{
	Security.addProvider(new ISAACProvider ());
	try {
		rnd = SecureRandom.getInstance ("ISAACRandom");
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

    private BigInteger p, q, g, C;
    private BigInteger gr;

    private BigInteger[][] pk;

    private BigInteger[] keys;

	Cipher cipher;
	
    public NPOTReceiver(InputStream in, OutputStream out) throws Exception {
    	super(in, out);
    	
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
    	Flag.sw.startOTIO();
    	C = RWBigInteger.readBI(is);
        p  = RWBigInteger.readBI(is);
        q  = RWBigInteger.readBI(is);
        g  = RWBigInteger.readBI(is);
        gr = RWBigInteger.readBI(is);
        msgBitLength = is.read();
    	Flag.sw.stopOTIO();
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

    	Flag.sw.startOTIO();
        for (int i = 0; i < choices.length; i++)
        	RWBigInteger.writeBI(os, pk0[i]);
        os.flush();
    	Flag.sw.stopOTIO();

    }

    private GCSignal[] step2(boolean[] choices) throws Exception {
    	BigInteger[][] msg = new BigInteger[choices.length][2];
    	Flag.sw.startOTIO();
    	for (int i = 0; i < choices.length; i++) {
    		msg[i][0] = RWBigInteger.readBI(is);
    		msg[i][1] = RWBigInteger.readBI(is);
    	}
    	Flag.sw.stopOTIO();

        GCSignal[] data = new GCSignal[choices.length];
        for (int i = 0; i < choices.length; i++) {
            int sigma = choices[i] ? 1 : 0;
			data[i] = GCSignal.newInstance(cipher.decrypt(keys[i].toByteArray(), msg[i][sigma],
					msgBitLength).toByteArray());
        }
        return data;
    }
}