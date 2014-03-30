package test.hamming;

import java.math.BigInteger;
import java.util.Random;
import flexsc.CompEnv;
import gc.Signal;
import org.junit.Test;
import test.harness.TestBigInteger;
import circuits.IntegerLib;


public class TestHammingDistance extends TestBigInteger{

	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 1;

		for (int i = 0; i < testCases; i++) {
			BigInteger a = new BigInteger(LENGTH, rnd);
			BigInteger b = new BigInteger(LENGTH, rnd);
			runThreads(new Helper(a, b) {
				public Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception {
					return new IntegerLib(e).hammingDistance(Signala, Signalb);}

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
	}
}