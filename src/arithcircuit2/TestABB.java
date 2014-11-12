package arithcircuit2;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;
import gc.GCSignal;
import gc.regular.GCGen;



public class TestABB  {

	static int bitlength = 512;
	static BigInteger v1 = null;
	static BigInteger v2 = null;

	@Test
	public void testCases () throws InterruptedException {
		for(int i = 0; i < 100; ++i)
			testACase();
	}
	
	public void testACase() throws InterruptedException {
		v1 = new BigInteger(bitlength, CompEnv.rnd);
		v2 = new BigInteger(bitlength, CompEnv.rnd);
		GenRunnable gen = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();

		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(5);
		tEva.start();
		tGen.join();
	}
	public static class GenRunnable extends network.Server implements Runnable {
		public void run() {
			try {
				listen(54321);
				ABBAlice alice = new ABBAlice(is, os, bitlength);
				BigInteger	a = alice.inputOfAlice(v1);
				BigInteger b = alice.inputOfBob(BigInteger.ZERO);
				double d1 = System.nanoTime();
				BigInteger c = alice.multiply(a, b);
				System.out.println((System.nanoTime()-d1)/1000000000);
				BigInteger rr = alice.outputToAlice(c);
//				System.out.println(rr);
				Assert.assertTrue((rr.equals(v1.multiply(v2).mod(alice.pk.n))));
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static class EvaRunnable extends network.Client implements Runnable {
		public void run() {
			try {
				connect("localhost", 54321);				
				ABBBob bob = new ABBBob(is, os);

				BigInteger a = bob.inputOfAlice(BigInteger.ZERO);
				BigInteger b = bob.inputOfBob(v2);
				BigInteger c = bob.multiply(a, b);
				bob.outputToAlice(c);
				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	//	@Test
	public void runThreads() throws Exception {
		GenRunnable gen = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();

		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(5);
		tEva.start();
		tGen.join();
	}
}