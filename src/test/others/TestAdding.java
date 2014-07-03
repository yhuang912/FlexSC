package test.others;

import java.util.Random;
import circuits.IntegerLib;
import test.Utils;
import gc.GCEva;
import gc.GCGen;
import gc.GCSignal;

public class TestAdding {

	class GenRunnable extends network.Server implements Runnable {
		boolean[] z;
		int intA;
		GenRunnable (int a) {
			this.intA = a;
		}
		public void run() {
			try {
				listen(54321);

				GCGen gen = new GCGen(is, os);
				
				GCSignal[] a = gen.inputOfAlice(Utils.fromInt(intA, 32));
				GCSignal[] b = gen.inputOfBob(new boolean[32]);

				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(gen);
				GCSignal[] d = lib.add(a ,b);
				d = lib.multiply(a, d);
				
				z = gen.outputToAlice(d);
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		int intB;
		EvaRunnable (int b) {
			this.intB = b;
		}

		public void run() {
			try {
				connect("localhost", 54321);				

				GCEva eva = new GCEva(is, os);
				
				GCSignal[] a = eva.inputOfAlice(new boolean[32]);
				GCSignal[] b = eva.inputOfBob(Utils.fromInt(intB, 32));

				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(eva);
				GCSignal[] d = lib.add(a ,b);
				d = lib.multiply(a, d);
				
				eva.outputToAlice(d);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	static public void main(String args[]) throws InterruptedException
	{
		TestAdding test = new TestAdding();
		
		Random rnd = new Random();
		int a = rnd.nextInt()%(1<<15);
		int b = rnd.nextInt()%(1<<15);
		System.out.println(a+" "+b+" "+((a+b)*a));
		GenRunnable gen = test.new GenRunnable(a);
		EvaRunnable eva = test.new EvaRunnable(b);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();

		System.out.println(Utils.toInt(gen.z));
	}
}