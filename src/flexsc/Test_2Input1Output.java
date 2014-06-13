package flexsc;

import java.util.Random;

import gc.GCEva;
import gc.GCGen;
import circuits.IntegerLib;
import pm.PMCompEnv;
import cv.CVCompEnv;
import test.Utils;


public class Test_2Input1Output<T> {
	
	public class AddGadget extends Gadget<T> {
		public AddGadget(CompEnv<T> e, String host, int port, Object[] input) {
			super(e, host, port, input);
		}

		public AddGadget(CompEnv<T> e, String host, int port) {
			super(e, host, port);
		}

		@Override
		public Object secureCompute(CompEnv<T> e, Object[] o) throws Exception {
			T[] signala = (T[]) o[0];
			T[] signalb = (T[]) o[1];
			return new IntegerLib<T>(e).add(signala ,signalb);
		}
		
	    public AddGadget getGadget(CompEnv<T>e , String host, int port, Object[] inputs2) {
			return new AddGadget(e, host, port, inputs2);	
		}

	};
	
	public class Helper {
		int intA, intB;
		boolean[] a;
		boolean[] b;
		Mode m;
		public Helper(int aa, int bb, Mode m) {
			this.m = m;
			intA = aa;
			intB = bb;

			a = Utils.fromInt(aa, 32);
			b = Utils.fromInt(bb, 32);
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
				
				T[] a = gen.inputOfAlice(h.a);
				T[] b = gen.inputOfBob(new boolean[32]);
				
				AddGadget gadget = new AddGadget(gen, "localhost", 11345);
				Object[] input = new Object[5];
				for(int i = 0; i < 5; ++i)
					input[i] = new Object[]{a, b};
				Object[] result = gadget.runGadget(gadget, input, gen);


				IntegerLib<T> lib = new IntegerLib<>(gen);
				T[] finalresult = (T[]) result[0];
				for(int i = 1; i < result.length; ++i){
					finalresult = lib.add(finalresult, (T[])result[i]);
				}
								
//				T[] d = h.secureCompute(a, b, gen);
				os.flush();

				z = gen.outputToAlice((T[]) finalresult);
				System.out.print(Utils.toInt(z));
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

				T[] a = eva.inputOfAlice(new boolean[32]);
				T[] b = eva.inputOfBob(h.b);

				AddGadget gadget = new AddGadget(eva, "localhost", 11345, new Object[]{a, b});
				Object[] input = new Object[5];
				for(int i = 0; i < 5; ++i)
					input[i] = new Object[]{a, b};
				Object[] result = gadget.runGadget(gadget, input, eva);

				IntegerLib<T> lib = new IntegerLib<>(eva);
				T[] finalresult = (T[]) result[0];
				for(int i = 1; i < result.length; ++i){
					finalresult = lib.add(finalresult, 
							(T[])result[i]);
				}
								
				os.flush();

				eva.outputToAlice((T[]) finalresult);
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
		tEva.join();	
	}

	
	public static void main(String args[])throws Exception {
		Test_2Input1Output<Boolean> tt = new Test_2Input1Output<Boolean>();
		Random rnd = new Random();
		int testCases = 1;

		for (int i = 0; i < testCases; i++) {
			tt.runThreads(tt.new Helper(1000, 2
					, Mode.VERIFY));
		}
		System.out.print("!!");
	}	

}