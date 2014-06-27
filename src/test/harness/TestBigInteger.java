package test.harness;

import java.math.BigInteger;

import org.junit.Assert;

import pm.PMCompEnv;
import test.Utils;
import cv.CVCompEnv;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.Party;
import gc.GCEva;
import gc.GCGen;



public class TestBigInteger<T> {
	public final int LENGTH = 10;
	final int RANGE = LENGTH;
	public abstract class Helper {
		BigInteger intA, intB;
		boolean[] a;
		boolean[] b;
		Mode m;
		public Helper(BigInteger aa, BigInteger bb, Mode m) {
			intA = aa;
			intB = bb;
			this.m = m;

			a = Utils.fromBigInteger(aa, RANGE);
			b = Utils.fromBigInteger(bb, RANGE);
		}
		public abstract T[] secureCompute(T[] Signala, T[] Signalb, CompEnv<T> e) throws Exception;
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

				CompEnv<T> gen = null;
				if(h.m == Mode.REAL)
					gen = (CompEnv<T>) new GCGen(is, os);
				else if(h.m == Mode.VERIFY)
					gen = (CompEnv<T>) new CVCompEnv(is, os, Party.Alice);
				else if(h.m == Mode.COUNT) 
					gen = (CompEnv<T>) new PMCompEnv(is, os, Party.Alice);
				
				Flag.sw.startTotal();
				T [] a = gen.inputOfAlice(h.a);
				
				T[]b = gen.inputOfBob(new boolean[h.b.length]);
				//new java.util.Scanner(System.in).nextLine();
				T[] d = h.secureCompute(a, b, gen);
				os.flush();
		          
				z = gen.outputToAlice(d);
				double t = Flag.sw.stopTotal();
				Flag.sw.addCounter();
//				System.out.println(t/1000000.0);
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

				CompEnv<T> eva = null;
				if(h.m == Mode.REAL)
					eva = (CompEnv<T>) new GCEva(is, os);
				else if(h.m == Mode.VERIFY)
					eva = (CompEnv<T>) new CVCompEnv(is ,os, Party.Bob);
				else if (h.m == Mode.COUNT) 
					eva = (CompEnv<T>) new PMCompEnv(is, os, Party.Bob);

				T [] a = eva.inputOfAlice(new boolean[h.a.length]);
				T [] b = eva.inputOfBob(h.b);
				
				T[] d = h.secureCompute(a, b, eva);
				
				eva.outputToAlice(d);
				os.flush();
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

		/*System.out.println(Utils.toBigInteger(h.a)+" "+Utils.toBigInteger(h.b)+" "+
		h.intA+" "+h.intB+"\n");
		System.out.println(Arrays.toString(h.a));
		System.out.println(Arrays.toString(h.b));
		System.out.println(Arrays.toString( Utils.fromBigInteger(h.plainCompute(h.intA, h.intB),gen.z.length)));
		System.out.println(Arrays.toString(Utils.fromBigInteger(Utils.toBigInteger(gen.z),gen.z.length)));
		*/
		Assert.assertEquals(h.plainCompute(h.intA, h.intB), Utils.toBigInteger(gen.z));
	}
}