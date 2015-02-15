package paillier;


import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import flexsc.CompEnv;

public class TestPaillier {

	public PublicKey pk = new PublicKey();
	public PrivateKey sk = new PrivateKey(2048);
	public TestPaillier() {
		Paillier.keyGen(sk, pk);
	}

	@Test
	public void testCases(){
		for(int i = 0; i < 100; ++i)
			testSub();
	}
	
	public void testAdd() {
		BigInteger m1 = new BigInteger(pk.k1, CompEnv.rnd);
		BigInteger m2 = new BigInteger(pk.k1, CompEnv.rnd);
		
		BigInteger em1 = Paillier.encrypt(m1, pk);
		BigInteger em2 = Paillier.encrypt(m2, pk);
		BigInteger res = Paillier.add(em1, em2, pk);
		
		Assert.assertEquals( Paillier.decrypt(res, sk), m1.add(m2).mod(pk.n));
	}
	
	public void testSub() {
//		BigInteger m1 = new BigInteger(pk.k1, CompEnv.rnd);
//		BigInteger m2 = new BigInteger(pk.k1, CompEnv.rnd);
//		BigInteger em1 = Paillier.encrypt(m1, pk);
//		BigInteger em2 = Paillier.encrypt(m1, pk);
//		BigInteger res = Paillier.add(em1, em2, pk);
//		m1 = Paillier.decrypt(em1, sk);
//		Assert.assertEquals( , m1.subtract(m2).mod(pk.n));
	}
	
	public void testMultiply() {
		BigInteger m1 = new BigInteger(pk.k1, CompEnv.rnd);
		BigInteger m2 = new BigInteger(pk.k1, CompEnv.rnd);
		
		BigInteger em1 = Paillier.encrypt(m1, pk);
//		BigInteger em2 = Paillier.encrypt(m2, pk);
		BigInteger res = Paillier.multiply(em1, m2.negate(), pk);
		
		Assert.assertEquals( Paillier.decrypt(res, sk), m1.multiply(m2.negate()).mod(pk.n));
	}	
	
}



