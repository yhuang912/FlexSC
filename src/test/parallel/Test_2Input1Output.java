package test.parallel;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import network.Master;
import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.CompPool;
import flexsc.Gadget;
import flexsc.Mode;
import flexsc.Party;
import gc.GCSignal;


public class Test_2Input1Output<T> {

	int ORIGINAL_PORT = 54322;
	static int PORT = 51122;
	int START_PORT;

	public Test_2Input1Output(int startPort) {
		START_PORT = startPort;
	}

	public class AddGadget extends Gadget<T> {

		@Override
		public Object secureCompute(CompEnv<T> e, Object[] o) throws Exception {
			connectMachines();
			// connectToPeer("localhost", port);

			T[][] x = (T[][]) o[0];

			IntegerLib<T> lib =  new IntegerLib<T>(e);

			T[] result = x[0];
			for(int i = 1; i < x.length; ++i)
				result = lib.add(result, x[i]);

			// System.out.println("Sum = " + Utils.toInt(lib.getBooleans((T[]) result)));
			prefixSum(result, lib);
			return null;
		}

		private void prefixSum(T[] sum, IntegerLib<T> lib) throws Exception {
			int noOfIncomingConnections = numberOfIncomingConnections;
			int noOfOutgoingConnections = numberOfOutgoingConnections;
			GCSignal[] prefixSum = (GCSignal[]) sum;
			for (int k = 0; k < Master.LOG_MACHINES; k++) {
				synchronized(this) {
					if (noOfOutgoingConnections > 0) {
						for (int i = 0; i < prefixSum.length; i++) {
							prefixSum[i].send(peerOsClient[k]);
						}
						try {
							System.out.println(" flushing " + machineId);
							((BufferedOutputStream) peerOsClient[k]).flush();
							System.out.println(" flushing' " + machineId);
						} catch (IOException e) {
							System.out.println(" hahhdhhah " + machineId + " Iteration = " + k);
							e.printStackTrace();
						}
						noOfOutgoingConnections--;
					}
				}
				// System.out.println(machineId + " Sent Iteration = " + k);
				// Thread.sleep(5000);
				synchronized(this) {
					System.out.println(" Listening " + machineId + " Iteration = " + k);
					if (noOfIncomingConnections > 0) {
						GCSignal[] read = new GCSignal[prefixSum.length];
						for (int i = 0; i < prefixSum.length; i++) {
							read[i] = receive(peerIsServer[k]);
						}
						prefixSum = (GCSignal[]) lib.add((T[]) prefixSum, (T[]) read);
						noOfIncomingConnections--;
					}
				}
				// masterOs.write(sum);
				System.out.println(machineId + " Iteration = " + k);
			}
			synchronized(this) {
				System.out.println(machineId + " Iterations done");
				System.out.println(machineId + " Sum = " + Utils.toInt(lib.getBooleans((T[]) prefixSum)));
				// Thread.sleep(10000);
				System.out.println("Threads done " + machineId);
			}
			disconnectFromPeers();
		}

		private GCSignal receive(InputStream ois) {
			byte[] b = null;
			try {
				b = readBytes(ois, GCSignal.len);
				//	ois.read(b);	
			}
			catch (Exception e) { e.printStackTrace(); }
			return new GCSignal(b);
		}

		private byte[] readBytes(InputStream is, int len) throws IOException, InterruptedException
		{
			byte[] temp = new byte[len];
			int remain = len;
			// System.out.println("remain out " + remain);
			while(0 < remain)
			{
				// System.out.println("test read = " + remain + " " + len);
				int readBytes = is.read(temp, len-remain, remain);
				if (readBytes != -1) {
					remain -= readBytes;
				}/* else {
					Thread.sleep(1000);
				}*/
			}
			return temp;
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
				listen(ORIGINAL_PORT);

				System.out.println("Initial connect gen");
				CompEnv<T> gen = CompEnv.getEnv(h.m, Party.Alice, is, os);

				T[][] Ta = gen.newTArray(h.a.length, 0);
				for(int i = 0; i < Ta.length; ++i)
					Ta[i] = gen.inputOfBob(new boolean[32]);

				CompPool<T> pool = new CompPool(gen, "localhost", PORT);
				
				long t1 = System.nanoTime();

				Object[] input = new Object[CompPool.MaxNumberTask];

				for(int i = 0; i < CompPool.MaxNumberTask; ++i)
					input[i] = new Object[]{Arrays.copyOfRange(Ta, i*Ta.length/CompPool.MaxNumberTask, (i+1)*Ta.length/CompPool.MaxNumberTask)};

				System.out.println("Gen comp pool established");

				Object[] result = pool.runGadget(new AddGadget(), input, START_PORT);
				// IntegerLib<T> lib = new IntegerLib<>(gen);
				/*T[] finalresult = (T[]) result[0];
				for(int i = 1; i < result.length; ++i){
					finalresult = lib.add(finalresult, (T[])result[i]);
				}*/

				// os.flush();

				// long t2 = System.nanoTime();
				// System.out.println(Ta.length+"\t"+(t2-t1)/1000000000.0);



				/*z = gen.outputToAlice((T[]) finalresult);
				System.out.println("result:"+Utils.toInt(z));*/
				// pool.finalize();
				// disconnect();
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
				connect("localhost", ORIGINAL_PORT);				

				System.out.println("Initial connect eva");
				CompEnv<T> eva = CompEnv.getEnv(h.m, Party.Bob, is, os);

				T[][] Ta = eva.newTArray(h.a.length, 0);
				for(int i = 0; i < Ta.length; ++i)
					Ta[i] = eva.inputOfBob(h.a[i]);

				CompPool<T> pool = new CompPool(eva, "localhost", PORT);				
				Object[] input = new Object[CompPool.MaxNumberTask];

				for(int i = 0; i < CompPool.MaxNumberTask; ++i)
					input[i] = new Object[]{Arrays.copyOfRange(Ta, i*Ta.length/CompPool.MaxNumberTask, (i+1)*Ta.length/CompPool.MaxNumberTask)};
				os.flush();

				System.out.println("Eva comp pool established");
				Object[] result = pool.runGadget(new AddGadget(), input, START_PORT + 10000);

				// IntegerLib<T> lib = new IntegerLib<>(eva);
				/*T[] finalresult = (T[]) result[0];
				for(int i = 1; i < result.length; ++i){
					finalresult = lib.add(finalresult, 
							(T[])result[i]);
				}*/

				// os.flush();

				//eva.outputToAlice((T[]) finalresult);
				// os.flush();
				// pool.finalize();

				// disconnect();

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


	/*public static void main(String args[])throws Exception {
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
		
	}	*/

}
