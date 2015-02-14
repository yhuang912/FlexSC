package harness;

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Assert;

import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.Party;



public class TestBigInteger extends TestHarness {
	public static final int LENGTH = 409600;
	final static int RANGE = LENGTH/2;
	public static abstract class Helper {
		BigInteger intA, intB;
		boolean[] a;
		boolean[] b;
		public Helper(BigInteger aa, BigInteger bb) {
			intA = aa;
			intB = bb;

			a = Utils.fromBigInteger(aa, LENGTH);
			b = Utils.fromBigInteger(bb, LENGTH);
		}
		public abstract <T>T[] secureCompute(T[] Signala, T[] Signalb, CompEnv<T> e) throws Exception;
		public abstract BigInteger plainCompute(BigInteger x, BigInteger y);
	}

	public static class GenRunnable<T> extends network.Server implements Runnable {
		boolean[] z;
		Helper h;
		GenRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> gen = CompEnv.getEnv(Party.Alice, this);

				T [] a = gen.inputOfAlice(h.a);
				System.out.println("gen1");
				T[]b = gen.inputOfBob(new boolean[h.b.length]);
				System.out.println("gen2");

				T[] d = h.secureCompute(a, b, gen);
				System.out.println("gen3");
				os.flush();

				z = gen.outputToAlice(d);
				Flag.sw.print();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static class EvaRunnable<T> extends network.Client implements Runnable {
		Helper h;
		public double andgates;
		public double encs;
		EvaRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				connect("localhost", 54321);				
				@SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(Party.Bob, this);

				T [] a = env.inputOfAlice(new boolean[h.a.length]);
				System.out.println("eva1");
				T [] b = env.inputOfBob(h.b);
				System.out.println("eva2");
				flush();
				if (Flag.mode == Mode.COUNT) {
					((PMCompEnv) env).statistic.flush();
				}


				T[] d = h.secureCompute(a, b, env);
				System.out.println("eva3");
				//				if (Flag.mode == Mode.COUNT) {
				//					((PMCompEnv) env).statistic.finalize();
				//					andgates = ((PMCompEnv) env).statistic.andGate;
				//					encs = ((PMCompEnv) env).statistic.NumEncAlice;
				//				}

				env.outputToAlice(d);
				os.flush();
				Flag.sw.print();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	static public <T>void runThreads(Helper h) throws Exception {
		GenRunnable<T> gen = new GenRunnable<T>(h);
		EvaRunnable<T> eva = new EvaRunnable<T>(h);

		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(5);
		tEva.start();
		tGen.join();
		if(Flag.mode == Mode.COUNT)
			System.out.println(eva.andgates);
		//		System.out.println(Utils.toBigInteger(h.a)+" "+Utils.toBigInteger(h.b)+" "+
		//		h.intA+" "+h.intB+"\n");
		if(!h.plainCompute(h.intA, h.intB).equals(Utils.toBigInteger(gen.z))) 
		{
			System.out.println(Arrays.toString(h.a));
			System.out.println(Arrays.toString(h.b));
			System.out.println(Arrays.toString( Utils.fromBigInteger(h.plainCompute(h.intA, h.intB),gen.z.length)));
			System.out.println(Arrays.toString(Utils.fromBigInteger(Utils.toBigInteger(gen.z),gen.z.length)));
		}

		Assert.assertEquals(h.plainCompute(h.intA, h.intB), Utils.toBigInteger(gen.z));
	}

	public static void main(String[] args) throws InterruptedException {
		BigInteger a = new BigInteger(TestBigInteger.LENGTH, CompEnv.rnd);
		BigInteger b = new BigInteger(TestBigInteger.LENGTH, CompEnv.rnd);

		Helper h  = new TestBigInteger.Helper(a, b) {
			public <T>T[] secureCompute(T[] Signala, T[] Signalb, CompEnv<T> e) throws Exception {
				return new IntegerLib<T>(e).hammingDistance(Signala, Signalb);}

			public BigInteger plainCompute(BigInteger x, BigInteger y) {
				BigInteger rb = x.xor(y);
				BigInteger res = new BigInteger("0");
				for(int i = 0; i < rb.bitLength(); ++i) {
					if( rb.testBit(i) )
						res = res.add(new BigInteger("1"));
				}
				return res;
			}
		};
		if(1 == 1) {
			GenRunnable gen = new GenRunnable(h);
			EvaRunnable eva = new EvaRunnable(h);

			Thread tGen = new Thread(gen);
			Thread tEva = new Thread(eva);
			tGen.start(); Thread.sleep(5);
			tEva.start();
			tGen.join();
		}
		else {
			if(new Integer(args[0]) == 0) {
				GenRunnable gen = new GenRunnable(h);
				Thread tGen = new Thread(gen);
				tGen.run(); 
			} else {
				EvaRunnable eva = new EvaRunnable(h);
				Thread tEva = new Thread(eva);
				tEva.run(); 
			}
		}
	}
}
