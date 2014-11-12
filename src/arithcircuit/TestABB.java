package arithcircuit;

import java.security.SecureRandom;
import java.util.Arrays;

import org.junit.Test;

import arithcircuit.TestBigInteger.EvaRunnable;
import arithcircuit.TestBigInteger.GenRunnable;
import compiledlib.tmp.FUNC_0_Native__Native__Native__Native_Impl;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;



public class TestABB  {

	public static Mode m = Mode.COUNT;
final public static int times = 10;
//	static int bitlength = 512;
	long v1 = 0;
	long v2 = 0;
	long v3 = 0;

	SecureRandom rnd;
	
	public static void main(String[] args) throws Exception {
		new TestABB().testCases();
	}
	
	@Test
	public void testCases () throws InterruptedException {
		rnd = new SecureRandom();
			testACase();
	}
	
	public void testACase() throws InterruptedException {
		
		for(int i = 1; i <= 10; ++i) {
			v1 = rnd.nextInt(1 << 30);
			GenRunnable gen = new GenRunnable();
			EvaRunnable eva = new EvaRunnable();

			Thread tGen = new Thread(gen);
			Thread tEva = new Thread(eva);
			tGen.start(); Thread.sleep(5);
			tEva.start();
			tGen.join();
		}
		
	}
	
	public void secureCompute(FHEInteger a, int dim) {
//		a = a.add(a);
		a.multiply(a);
	}
	
	public class GenRunnable extends network.Server implements Runnable {
		
		FUNC_0_Native__Native__Native__Native_Impl func;

		public void run() {
			try {
				listen(54321);
				CompEnv env = CompEnv.getEnv(m, Party.Alice, is, os);
				func = new FUNC_0_Native__Native__Native__Native_Impl(env);

				FHEInteger alice = FHEInteger.newInstance(env, is, os);
				alice.setup();				
				FHEInteger a = alice.input(Party.Alice, v1);
				
				double[] t1 = new double[times];
				for(int i = 0; i < t1.length; ++i) {
					t1[i] = System.nanoTime();
					secureCompute(a, 1);
					t1[i] = System.nanoTime() - t1[i];
				}
				Arrays.sort(t1);
//				System.out.println(Arrays.toString(t1));
				System.out.println(t1[t1.length/2]/1000000000.0);
				
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public class EvaRunnable extends network.Client implements Runnable {
		
		FUNC_0_Native__Native__Native__Native_Impl func;

		public void run() {
			try {
				connect("localhost", 54321);				
				CompEnv env = CompEnv.getEnv(m, Party.Bob, is, os);
				func = new FUNC_0_Native__Native__Native__Native_Impl(env);
				
				FHEInteger alice = FHEInteger.newInstance(env, is, os);
				alice.setup();
				
				
				FHEInteger a = alice.input(Party.Alice, 0);
				for(int i = 0; i < times; ++i) 
					secureCompute(a, 5);
				disconnect();
				
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}