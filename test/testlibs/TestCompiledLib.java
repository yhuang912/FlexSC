package testlibs;

import flexsc.CompEnv;
import harness.TestBigInteger;
import harness.TestHarness;

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Test;

import util.Utils;
import circuits.arithmetic.IntegerLib;
import compiledlib.libs.NoClass;

public class TestCompiledLib extends TestHarness {

//	@Test
	public void testCountOnes() throws Exception {

		for (int i = 0; i < testCases; i++) {
			BigInteger a = new BigInteger(TestBigInteger.LENGTH, CompEnv.rnd);
			BigInteger b = new BigInteger(TestBigInteger.LENGTH, CompEnv.rnd);
			TestBigInteger.runThreads(new TestBigInteger.Helper(a, b) {
				public <T>T[] secureCompute(T[] Signala, T[] Signalb, CompEnv<T> e) throws Exception {
//					return new NoClass<T>(e).countOnes(Signala.length, Signala);
					return new IntegerLib<>(e).numberOfOnes(Signala);
				}

				public BigInteger plainCompute(BigInteger x, BigInteger y) {
					BigInteger res = new BigInteger("0");
					for(int i = 0; i < x.bitLength(); ++i) {
						if( x.testBit(i) )
							res = res.add(new BigInteger("1"));
					}
					return res;
				}
			});
		}
	}
	
	@Test
	public void testLeadingZeros() throws Exception {

		for (int i = 0; i < testCases; i++) {
			BigInteger a = new BigInteger(TestBigInteger.LENGTH, CompEnv.rnd);
			BigInteger b = new BigInteger(TestBigInteger.LENGTH, CompEnv.rnd);
			TestBigInteger.runThreads(new TestBigInteger.Helper(a, b) {
				public <T>T[] secureCompute(T[] Signala, T[] Signalb, CompEnv<T> e) throws Exception {
					return new NoClass<T>(e).leadingZero(Signala.length, Signala);
//					return new IntegerLib<T>(e).leadingZeros(Signala);
				}

				public BigInteger plainCompute(BigInteger x, BigInteger y) {
					boolean[] bo = Utils.fromBigInteger(x, TestBigInteger.LENGTH);
					BigInteger res = new BigInteger("0");
					for(int i = bo.length-1; i >=0; --i) {
						if( ! bo[i] )
							res = res.add(new BigInteger("1"));
						else return res;
					}
					return res;
				}
			});
		}
	}
}