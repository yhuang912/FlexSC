package flexsc;

import gc.GCEva;
import gc.GCGen;
import gc.GCSignal;

import java.util.Arrays;
import java.util.Random;

import pm.PMCompEnv;
import test.Utils;
import circuits.IntegerLib;
import cv.CVCompEnv;


public class Test_2Input1Output<T> {
	
	static int PORT = 51111;


	public class AddGadget extends Gadget<T> {
		@Override
		public Object secureCompute(CompEnv<T> e, Object[] o) throws Exception {
			T[][] x = (T[][]) o[0];

			IntegerLib<T> lib =  new IntegerLib<T>(e);

			T[] result = x[0];
			for(int i = 1; i < x.length; ++i)
				result = lib.add(result, x[i]);

			return result;
		}

		public AddGadget getGadget() {
			return new AddGadget();	
		}
	};

	public class Helper {
		int[] intA;
		boolean[][] a;
		Mode m;
		public Helper(int[] aa, Mode m) {
			this.m = m;
			intA = aa;
			a = new boolean[aa.length][];
			for(int i = 0; i < aa.length; ++i)
				a[i] = Utils.fromInt(aa[i], 32);
		}
	}

	class GenRunnable extends network.Server implements Runnable {
		boolean[] z;
		Helper h;
		GenRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				listen(15432);


				CompEnv<T> gen = null;
				if(h.m == Mode.REAL)
					gen = (CompEnv<T>) new GCGen(is, os);
				else if(h.m == Mode.VERIFY)
					gen = (CompEnv<T>) new CVCompEnv(is, os, Party.Alice);
				else if(h.m == Mode.COUNT) 
					gen = (CompEnv<T>) new PMCompEnv(is, os, Party.Alice);

				T[][] Ta = gen.newTArray(h.a.length,0);
				for(int i = 0; i < Ta.length; ++i)
					Ta[i] = gen.inputOfBob(new boolean[32]);

				CompPool<T> pool = new CompPool(gen, "localhost", PORT);
				
				long t1 = System.nanoTime();

				Object[] input = new Object[CompPool.MaxNumberTask];

				for(int i = 0; i < CompPool.MaxNumberTask; ++i)
					input[i] = new Object[]{Arrays.copyOfRange(Ta, i*Ta.length/CompPool.MaxNumberTask, (i+1)*Ta.length/CompPool.MaxNumberTask)};


				Object[] result = pool.runGadget( new AddGadget(), input);
				IntegerLib<T> lib = new IntegerLib<>(gen);
				T[] finalresult = (T[]) result[0];
				for(int i = 1; i < result.length; ++i){
					finalresult = lib.add(finalresult, (T[])result[i]);
				}

				os.flush();

				long t2 = System.nanoTime();
				System.out.println(Ta.length+"\t"+(t2-t1)/1000000000.0);



				z = gen.outputToAlice((T[]) finalresult);
				pool.finalize();
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
				connect("localhost", 15432);				

				CompEnv<T> eva = null;

				if(h.m == Mode.REAL)
					eva = (CompEnv<T>) new GCEva(is, os);
				else if(h.m == Mode.VERIFY)
					eva = (CompEnv<T>) new CVCompEnv(is ,os, Party.Bob);
				else if (h.m == Mode.COUNT) 
					eva = (CompEnv<T>) new PMCompEnv(is, os, Party.Bob);


				T[][] Ta = eva.newTArray(h.a.length,0);
				for(int i = 0; i < Ta.length; ++i)
					Ta[i] = eva.inputOfBob(h.a[i]);

				CompPool<T> pool = new CompPool(eva, "localhost", PORT);				
				Object[] input = new Object[CompPool.MaxNumberTask];
				for(int i = 0; i < CompPool.MaxNumberTask; ++i)
					input[i] = new Object[]{Arrays.copyOfRange(Ta, i*Ta.length/CompPool.MaxNumberTask, (i+1)*Ta.length/CompPool.MaxNumberTask)};
				os.flush();

				Object[] result = pool.runGadget( new AddGadget(), input);



				IntegerLib<T> lib = new IntegerLib<>(eva);
				T[] finalresult = (T[]) result[0];
				for(int i = 1; i < result.length; ++i){
					finalresult = lib.add(finalresult, 
							(T[])result[i]);
				}

				os.flush();

				eva.outputToAlice((T[]) finalresult);
				os.flush();
				pool.finalize();

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
		tEva.join();	
	}


	public static void main(String args[])throws Exception {
		CompPool.MaxNumberTask = new Integer(args[0]);
		Mode m = Mode.REAL;
		Test_2Input1Output<GCSignal> tt = new Test_2Input1Output<GCSignal>();
		Random rnd = new Random();
		int testCases = 1;
		int res = 0;;

		for (int i = 10000; i < 100000; i+=10000) {
			int a[] = new int[i];
			tt.runThreads(tt.new Helper(a, m));
		}
		
	}	

}