package test.harness;

import java.math.BigInteger;
import java.util.Arrays;
import flexsc.CompEnv;
import gc.GCEva;
import gc.GCGen;
import gc.Signal;
import org.junit.Assert;
import test.Utils;



public class TestBigInteger {
	public final int LENGTH = 100;
	final int RANGE = 100;
	public abstract class Helper {
		BigInteger intA, intB;
		boolean[] a;
		boolean[] b;
		public Helper(BigInteger aa, BigInteger bb) {
			intA = aa;
			intB = bb;

			a = Utils.fromBigInteger(aa, RANGE);
			b = Utils.fromBigInteger(bb, RANGE);
		}
		public abstract Signal[] secureCompute(Signal[] Signala, Signal[] Signalb, CompEnv<Signal> e) throws Exception;
		public abstract BigInteger plainCompute(BigInteger x, BigInteger y);
	}

	class GenRunnable extends network.Server implements Runnable {
		boolean[] z;
		Helper h;
		GenRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				listen(54321);

				GCGen gen = new GCGen(is, os);
				Signal[] a = gen.inputOfGen(h.a);
				Signal [] b = gen.inputOfEva(new boolean[h.b.length]);
				
				//new java.util.Scanner(System.in).nextLine();
				Signal[] d = h.secureCompute(a, b, gen);
				os.flush();
				
		          
				z = gen.outputToGen(d);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		Helper h;
		EvaRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				connect("localhost", 54321);				

				GCEva eva = new GCEva(is, os);
				
				Signal [] a = eva.inputOfGen(new boolean[h.a.length]);
				Signal [] b = eva.inputOfEva(h.b);
				
				Signal[] d = h.secureCompute(a, b, eva);
				
				eva.outputToGen(d);
				os.flush();
				//System.out.println("numberofAnd:"+eva.numberOfAnd);
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void runThreads(Helper h) throws Exception {
		GenRunnable gen = new GenRunnable(h);
		EvaRunnable eva = new EvaRunnable(h);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(5);
		tEva.start();
		tGen.join();

		System.out.println(Utils.toBigInteger(h.a)+" "+Utils.toBigInteger(h.b)+" "+
		h.intA+" "+h.intB+"\n");
		System.out.println(Arrays.toString(h.a));
		System.out.println(Arrays.toString(h.b));
		System.out.println(Arrays.toString( Utils.fromBigInteger(h.plainCompute(h.intA, h.intB),gen.z.length)));
		System.out.println(Arrays.toString(Utils.fromBigInteger(Utils.toBigInteger(gen.z),gen.z.length)));
		
		Assert.assertEquals(h.plainCompute(h.intA, h.intB), Utils.toBigInteger(gen.z));
	}
}