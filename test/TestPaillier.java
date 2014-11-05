

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import flexsc.CompEnv;

public class TestPaillier {

	int bitlength = 1024;
	util.paillier.Paillier p = new util.paillier.Paillier();
	public TestPaillier() {
	}

	@Test
	public void testCases(){
		for(int i = 0; i < 100; ++i)
			testMultiply();
	}
	
	public void testAdd() {
		BigInteger m1 = new BigInteger(p.bitLength, CompEnv.rnd);
		BigInteger m2 = new BigInteger(p.bitLength, CompEnv.rnd);
		
		BigInteger em1 = p.Encryption(m1);
		BigInteger em2 = p.Encryption(m2);
		BigInteger res = p.add(em1, em2);
		
		Assert.assertEquals(p.Decryption(res), m1.add(m2).mod(p.n));
	}
	
	public void testMultiply() {
		BigInteger m1 = new BigInteger(p.bitLength, CompEnv.rnd);
		BigInteger m2 = new BigInteger(p.bitLength, CompEnv.rnd);
		
		BigInteger em1 = p.Encryption(m1);
		BigInteger res = p.multiply(em1, m2);
		
		Assert.assertEquals(p.Decryption(res), m1.multiply(m2).mod(p.n));
	}
	
	
}



