package test;

import java.math.BigInteger;
import java.util.Random;

import flexsc.CompEnv;
import gc.GCSignal;

import org.junit.Test;

import test.harness.TestBigInteger;
import circuits.IntegerLib;


public class TestBigIntegerMultiply extends TestBigInteger{
		@Test
		public void testAllCases() throws Exception {
			Random rnd = new Random();
			int testCases = 10;
			for (int i = 0; i < testCases; i++) {
				BigInteger a = new BigInteger(LENGTH, rnd);
				BigInteger b = new BigInteger(LENGTH, rnd);
				
				runThreads(new Helper(a, b ) {
					public GCSignal[] secureCompute(GCSignal[] Signala, GCSignal[] Signalb, CompEnv<GCSignal> e) throws Exception {
						return new IntegerLib(e).unSignedMultiply(Signala ,Signalb);}

					@Override
					public BigInteger plainCompute(BigInteger x, BigInteger y) {
						return x.multiply(y);
					}
				});
			}
		}
}