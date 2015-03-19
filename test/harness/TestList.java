package harness;

import java.util.Arrays;

import circuits.arithmetic.FloatLib;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Party;


public class TestList extends TestHarness{
	public static <T>void secureCompute(CompEnv<T> e) throws Exception{
		IntegerLib<T> lib = new IntegerLib<T>(e);
		FloatLib<T> flib = new FloatLib<T>(e, 52, 11);
		double t1, t2 , t;
		int iter = 50;
		T[] a = e.inputOfAlice(new boolean[1024]);
		T[] b = e.inputOfBob(new boolean[1024]);
		double[] time = new double[iter];
		//int add
		
		T[] c = null;
		for(int i = 0; i < iter; ++i){
			t1 = System.nanoTime();
			c = lib.add(a, b);
			t2 = System.nanoTime();
			time[i] = (t2-t1)/1000000000;
		}
		Flag.sw.ands = 0;System.gc();
		for(int i = 0; i < iter; ++i){
			
			t1 = System.nanoTime();
			c = lib.add(a, b);
			t2 = System.nanoTime();
			time[i] = (t2-t1)/1000000000;
		}
		System.out.println("1024add: " + (Flag.sw.ands/iter));
		for(int i = 0; i < iter; ++i)
			System.out.println(time[i]);

		//int mul
		Flag.sw.ands = 0;System.gc();
		
		for(int i = 0; i < iter; ++i){
			t1 = System.nanoTime();
			c = lib.multiply(a, b);
			t2 = System.nanoTime();
			time[i] = (t2-t1)/1000000000;
		}
		System.out.println("1024mul: " + (Flag.sw.ands/iter));
		for(int i = 0; i < iter; ++i)
			System.out.println(time[i]);

		//int comp
		a = e.inputOfAlice(new boolean[16384]);
		b = e.inputOfBob(new boolean[16384]);
		Flag.sw.ands = 0;System.gc();
		for(int i = 0; i < iter; ++i){
			t1 = System.nanoTime();
			c[0] = lib.geq(a, b);
			t2 = System.nanoTime();
			time[i] = (t2-t1)/1000000000;
		}
		System.out.println("16384comp: " + (Flag.sw.ands/iter));
		for(int i = 0; i < iter; ++i)
			System.out.println(time[i]);



		//int hamming
		a = e.inputOfAlice(new boolean[1600]);
		b = e.inputOfBob(new boolean[1600]);
		Flag.sw.ands = 0;System.gc();
		
		for(int i = 0; i < iter; ++i){
			t1 = System.nanoTime();
			c = lib.hammingDistance(a, b);
			t2 = System.nanoTime();
			time[i] = (t2-t1)/1000000000;
		}
		System.out.println("1600hamming: " + (Flag.sw.ands/iter));
		for(int i = 0; i < iter; ++i)
			System.out.println(time[i]);

		//float
		a = flib.inputOfAlice(0.5);
		b = flib.inputOfBob(0.5);
		Flag.sw.ands = 0;System.gc();
		for(int i = 0; i < iter; ++i) {
			t1 = System.nanoTime();
			c = flib.add(a, b);
			t2 = System.nanoTime();
			time[i] = (t2-t1)/1000000000;
		}
		System.out.println("f64add: " + (Flag.sw.ands/iter));
		for(int i = 0; i < iter; ++i)
			System.out.println(time[i]);

		//float
		a = flib.inputOfAlice(0.5);
		b = flib.inputOfBob(0.5);
		Flag.sw.ands = 0;System.gc();
		for(int i = 0; i < iter; ++i){
			t1 = System.nanoTime();
			c = flib.multiply(a, b);
			t2 = System.nanoTime();
			t = (t2-t1)/1000000000;
		}
		System.out.println("f64multi: " + (Flag.sw.ands/iter));
		for(int i = 0; i < iter; ++i)
			System.out.println(time[i]);

		//float
		a = flib.inputOfAlice(0.5);
		b = flib.inputOfBob(0.5);
		Flag.sw.ands = 0;System.gc();
		for(int i = 0; i < iter; ++i){
			t1 = System.nanoTime();
			c = flib.div(a, b);
			t2 = System.nanoTime();
			t = (t2-t1)/1000000000;
		}
		System.out.println("f64div: " + (Flag.sw.ands/iter));
		for(int i = 0; i < iter; ++i)
			System.out.println(time[i]);
	
		
		//AMS: 20*(2^11)^2*16/1024/1024/1024
		//CM: 20*2^20*16/1024/1024/1024
	}


	public static class GenRunnable<T> extends network.Server implements Runnable {
		boolean[] z;

		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> gen = CompEnv.getEnv(Party.Alice, is, os);

				secureCompute(gen);
				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static class EvaRunnable<T> extends network.Client implements Runnable {
		public double andgates;
		public double encs;

		public void run() {
			try {
				connect("localhost", 54321);				
				@SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(Party.Bob, is, os);

				secureCompute(env);
				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}


	public static void main(String args[]) throws Exception {
		if(args.length==0) {
			GenRunnable gen = new GenRunnable();
			EvaRunnable env = new EvaRunnable();
			Thread tGen = new Thread(gen);
			Thread tEva = new Thread(env);
			tGen.start();
			Thread.sleep(5);
			tEva.start();
			tGen.join();
			tEva.join();
		}
		else if(new Integer(args[0]) == 0)
			new GenRunnable().run();
		else new EvaRunnable().run();
	}
}
