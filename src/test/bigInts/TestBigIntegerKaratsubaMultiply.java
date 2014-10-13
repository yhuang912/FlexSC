package test.bigInts;

import java.math.BigInteger;
import java.util.Random;

import flexsc.CompEnv;
import flexsc.Mode;
import gcHalfANDs.GCSignal;

import org.junit.Test;

import test.harness.TestBigInteger;
import circuits.IntegerLib;


public class TestBigIntegerKaratsubaMultiply extends TestBigInteger<GCSignal> {
		@Test
		public void testAllCases() throws Exception {
			Random rnd = new Random();
			int testCases = 10;
			for (int i = 0; i < testCases; i++) {
				BigInteger a = new BigInteger(LENGTH, rnd);
				BigInteger b = new BigInteger(LENGTH, rnd);

				runThreads(new Helper(a, b, Mode.REAL ) {
					public GCSignal[] secureCompute(GCSignal[] Signala, GCSignal[] Signalb, CompEnv<GCSignal> e) throws Exception {
						return new IntegerLib<GCSignal>(e).karatsubaMultiply(Signala ,Signalb);}

					@Override
					public BigInteger plainCompute(BigInteger x, BigInteger y) {
						return x.multiply(y);
					}
				});
			}
		}
}