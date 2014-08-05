package test.parallel;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import network.Constants;
import network.Master;
import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.CompPool;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.Party;
import gc.GCSignal;


public class Test_2Input1Output<T> {
	
	static int PORT = 51111;
	static int MASTER_GEN_PORT;
	static int MASTER_EVA_PORT;
	static int THREAD_MASTER_SERVER_CLIENT_PORT = 15432;


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
				listen(THREAD_MASTER_SERVER_CLIENT_PORT);

				CompEnv<T> gen = CompEnv.getEnv(h.m, Party.Alice, is, os);

				T[][] Ta = gen.newTArray(h.a.length, 0);
				for(int i = 0; i < Ta.length; ++i)
					Ta[i] = gen.inputOfBob(new boolean[32]);

				CompPool<T> pool = new CompPool(gen, Constants.LOCALHOST, PORT, MASTER_GEN_PORT);
				
				long t1 = System.nanoTime();

				Object[] input = new Object[Master.MACHINES];

				for(int i = 0; i < Master.MACHINES; ++i)
					input[i] = new Object[]{Arrays.copyOfRange(Ta, i*Ta.length/Master.MACHINES, (i+1)*Ta.length/Master.MACHINES)};


				Object[] result = pool.runGadget("test.parallel.AddGadget", input);
				IntegerLib<T> lib = new IntegerLib<>(gen);

				os.flush();

				long t2 = System.nanoTime();
				System.out.println(Ta.length+"\t"+(t2-t1)/1000000000.0);



				/*z = gen.outputToAlice((T[]) finalresult);
				System.out.println("result:"+Utils.toInt(z));*/
				// pool.finalize();
				disconnect();
			} catch(ExecutionException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				System.out.println("Gadget probably does not exist. Reflection issue");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				connect(Constants.LOCALHOST, THREAD_MASTER_SERVER_CLIENT_PORT);				

				CompEnv<T> eva = CompEnv.getEnv(h.m, Party.Bob, is, os);

				T[][] Ta = eva.newTArray(h.a.length, 0);
				for(int i = 0; i < Ta.length; ++i)
					Ta[i] = eva.inputOfBob(h.a[i]);

				CompPool<T> pool = new CompPool(eva, Constants.LOCALHOST, PORT, MASTER_EVA_PORT);				
				Object[] input = new Object[Master.MACHINES];

				for(int i = 0; i < Master.MACHINES; ++i)
					input[i] = new Object[]{Arrays.copyOfRange(Ta, i*Ta.length/Master.MACHINES, (i+1)*Ta.length/Master.MACHINES)};
				os.flush();
				
				Object[] result = pool.runGadget("test.parallel.AddGadget", input);

				IntegerLib<T> lib = new IntegerLib<>(eva);

				os.flush();

				//eva.outputToAlice((T[]) finalresult);
				os.flush();
				// pool.finalize();

				disconnect();

			} catch(ExecutionException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				System.out.println("Gadget probably does not exist. Reflection issue");
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		// CompPool.MaxNumberTask = 2;//new Integer(args[0]);

		Mode m = Mode.REAL;
		Test_2Input1Output<GCSignal> tt = new Test_2Input1Output<GCSignal>();

		for (int i = 100; i <= 100; i+=100) {
			int a[] = new int[i];
			a[0] = 1;
			tt.runThreads(tt.new Helper(a, m));
			Flag.sw.addCounter();
			Flag.sw.print();
			Flag.sw.flush();
		}
		
	}	

}
