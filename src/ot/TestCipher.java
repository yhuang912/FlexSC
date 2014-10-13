package ot;

import gcHalfANDs.GCSignal;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.junit.Test;

public class TestCipher {
	SecureRandom rnd = new SecureRandom();
//	BigInteger b = BigInteger.ZERO;
	Cipher c = new Cipher();
//	byte[] bs = new byte[]{0,1,2,3,4,5,6,7,8,9};
	GCSignal s = GCSignal.freshLabel(rnd);
	GCSignal b = GCSignal.freshLabel(rnd);
	
	public void test() {

		c.enc(s, b, 80);
	}

	@Test
	public void test1000() {
		for(int i = 0; i<1000000; i++)
			test();
	}
}
