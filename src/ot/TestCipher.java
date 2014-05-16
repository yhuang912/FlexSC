package ot;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.junit.Test;

public class TestCipher {
	SecureRandom rnd = new SecureRandom();
	BigInteger b = BigInteger.ZERO;
	Cipher c = new Cipher();
	byte[] bs = new byte[]{0,1,2,3,4,5,6,7,8,9};
	
	public void test() {

		b = c.encrypt(0, bs, b, 80);
//		Assert.assertTrue(m.equals(gb.dec(a, b, 0L, gb.enc(a, b, 0L, m))));
	}

	@Test
	public void test1000() {
		for(int i = 0; i<100000; i++)
			test();
	}
}
