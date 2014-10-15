import flexsc.CompEnv;
import harness.TestBigInteger;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import circuits.arithmetic.IntegerLib;


public class TestHammingDistance extends TestBigInteger<Boolean>{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 100;

		for (int i = 0; i < testCases; i++) {
			BigInteger a = new BigInteger(LENGTH, rnd);
			BigInteger b = new BigInteger(LENGTH, rnd);
			runThreads(new Helper(a, b) {
				public Boolean[] secureCompute(Boolean[] Signala, Boolean[] Signalb, CompEnv<Boolean> e) throws Exception {
					return new IntegerLib<Boolean>(e).hammingDistance(Signala, Signalb);}

				public BigInteger plainCompute(BigInteger x, BigInteger y) {
					BigInteger rb = x.xor(y);
					BigInteger res = new BigInteger("0");
					for(int i = 0; i < rb.bitLength(); ++i) {
							if( rb.testBit(i) )
								res = res.add(new BigInteger("1"));
					}
					return res;
					}
			});
		}
//		Flag.sw.print();
	}
}