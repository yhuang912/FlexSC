// Copyright (C) 2013 by Yan Huang <yhuang@cs.umd.edu>

package ot;

import gc.GCSignal;

import java.math.*;
//import java.util.*;
import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;


public class NPOTSender extends OTSender {

    private static SecureRandom rnd = new SecureRandom();
    private static final int certainty = 80;

    private final static int qLength = 160; //512;
    private final static int pLength = 1024; //15360;

    private BigInteger p, q, g, C, r;
    private BigInteger Cr, gr;
    
	ObjectInputStream ois;
	ObjectOutputStream oos;

	Cipher cipher;
	
    public NPOTSender(int msgBitLength, InputStream is,
                      OutputStream os) throws Exception {
    	super(msgBitLength, is, os);
    	ois = new ObjectInputStream(is);
    	oos = new ObjectOutputStream(os);
    	cipher = new Cipher();
    	
        initialize();
    }

    public void send(GCSignal[][] msgPairs) throws Exception {
//        super.execProtocol(msgPairs);

        step1(msgPairs);
    }

    private void initialize() throws Exception {
        File keyfile = new File("NPOTKey");
        if (keyfile.exists()) {
            FileInputStream fin = new FileInputStream(keyfile);
            ObjectInputStream fois = new ObjectInputStream(fin);

            C = (BigInteger) fois.readObject();
            p = (BigInteger) fois.readObject();
            q = (BigInteger) fois.readObject();
            g = (BigInteger) fois.readObject();
            gr = (BigInteger) fois.readObject();
            r = (BigInteger) fois.readObject();
            fois.close();

            oos.writeObject(C);
            oos.writeObject(p);
            oos.writeObject(q);
            oos.writeObject(g);
            oos.writeObject(gr);
            oos.writeInt(msgBitLength);
            oos.flush();

            Cr = C.modPow(r, p);
        } else {
        	System.out.println("to generate params");
            BigInteger pdq;
            q = new BigInteger(qLength, certainty, rnd);

            do {
                pdq = new BigInteger(pLength - qLength, rnd);
                pdq = pdq.clearBit(0); 
                p = q.multiply(pdq).add(BigInteger.ONE);
            } while (!p.isProbablePrime(certainty));

            do {
                g = new BigInteger(pLength - 1, rnd); 
            } while ((g.modPow(pdq, p)).equals(BigInteger.ONE)
                     || (g.modPow(q, p)).equals(BigInteger.ONE));

            r = (new BigInteger(qLength, rnd)).mod(q);
            gr = g.modPow(r, p);
            C = (new BigInteger(qLength, rnd)).mod(q);

            System.out.println("runs to here");
            
            oos.writeObject(C);
            oos.writeObject(p);
            oos.writeObject(q);
            oos.writeObject(g);
            oos.writeObject(gr);
            oos.writeInt(msgBitLength);
            oos.flush();

            Cr = C.modPow(r, p);

            FileOutputStream fout = new FileOutputStream(keyfile);
            ObjectOutputStream foos = new ObjectOutputStream(fout);

            foos.writeObject(C);
            foos.writeObject(p);
            foos.writeObject(q);
            foos.writeObject(g);
            foos.writeObject(gr);
            foos.writeObject(r);

            foos.flush();
            foos.close();
        }
    }

    @Override
    public void send(GCSignal[] msgPair) throws Exception {
    	GCSignal[][] m = new GCSignal[1][2];
    	m[0][0] = msgPair[0];
    	m[0][1] = msgPair[1];
    	send(m);
    }
    
    private void step1(GCSignal[][] msgPairs) throws Exception {
        BigInteger[] pk0 = (BigInteger[]) ois.readObject();
        BigInteger[] pk1 = new BigInteger[msgPairs.length];
        BigInteger[][] msg = new BigInteger[msgPairs.length][2];

        for (int i = 0; i < msgPairs.length; i++) {
            pk0[i] = pk0[i].modPow(r, p);
            pk1[i] = Cr.multiply(pk0[i].modInverse(p)).mod(p);

            msg[i][0] = cipher.encrypt(pk0[i].toByteArray(), new BigInteger(msgPairs[i][0].bytes), msgBitLength);
            msg[i][1] = cipher.encrypt(pk1[i].toByteArray(), new BigInteger(msgPairs[i][1].bytes), msgBitLength);
        }

        oos.writeObject(msg);
        oos.flush();
    }
}