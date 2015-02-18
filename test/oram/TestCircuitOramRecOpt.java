package oram;

//import orambs.BSCircuitOram;
import java.nio.ByteBuffer;

import network.Server;
import orambs.BSCircuitOram;
import util.Utils;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Party;
import gc.GCSignal;

public class TestCircuitOramRecOpt {

	public  static void main(String args[]) throws Exception {
//			GenRunnable gen = new GenRunnable(12345, new Integer(args[0]), 3, 1024-32, 8, 6);
		GenRunnable gen = new GenRunnable(12345, 10, 3, 32, 8, 6);
			EvaRunnable eva = new EvaRunnable("localhost", 12345);
			Thread tGen = new Thread(gen);
			Thread tEva = new Thread(eva);
			tGen.start();
			Thread.sleep(10);
			tEva.start();
			tGen.join();
			Flag.sw.print();
			System.out.print("\n");
	}
	
	final static int writeCount = 10;//1 << 7;
	final static int readCount = 0;//(1 << 7);

	public TestCircuitOramRecOpt() { }

	public static class GenRunnable extends network.Server implements Runnable {
		int port;
		int logN;
		int N;
		int recurFactor;
		int cutoff;
		int capacity;
		int dataSize;
		int logCutoff;

		GenRunnable(int port, int logN, int capacity, int dataSize,
				int recurFactor, int logCutoff) {
			Flag.tableName = "table_"+logN;
			this.port = port;
			this.logN = logN;
			this.N = 1 << logN;
			this.recurFactor = recurFactor;
			this.logCutoff = logCutoff;
			this.cutoff = 1 << logCutoff;
			this.dataSize = dataSize;
			this.capacity = capacity;
		}

		public void run() {
			try {
				listen(port);

				os.write(logN);
				os.write(recurFactor);
				os.write(logCutoff);
				os.write(capacity);
				os.write(ByteBuffer.allocate(4).putInt(dataSize).array());
				os.flush();
System.out.println("OTPreProcessing");
				System.out.println("\nlogN recurFactor  cutoff capacity dataSize");
				System.out.println(logN + " " + recurFactor + " " + cutoff
						+ " " + capacity + " " + dataSize);

				@SuppressWarnings("unchecked")
				CompEnv<GCSignal> env = CompEnv.getEnv(Party.Alice,
						is, os);
				BSCircuitOram<GCSignal> client = new BSCircuitOram<GCSignal>(
						env, N, dataSize,  32, cutoff, recurFactor, capacity, 80);
System.gc();
//				RecursiveOptCircuitOram<GCSignal>client = new RecursiveOptCircuitOram<GCSignal>(
//						env, N, dataSize,  cutoff, recurFactor, capacity, 80);
double t1 = 0, t2;
				for (int i = 0; i < writeCount; ++i) {
					int element = i % N;

					Flag.sw.ands = 0;
					GCSignal[] scData = client.baseOram.env.inputOfAlice(Utils
							.fromInt(element, dataSize));
					os.flush();
	System.out.println("Access: "+i +" start. currentTime" + System.nanoTime());
					Flag.sw.startTotal();
					double t11 = System.nanoTime();
					client.write(client.baseOram.lib.toSignals(element), scData);
	System.out.println("Access: "+i +" finish. currentTime" + System.nanoTime());
System.out.println("Running time for the access:\t"+(System.nanoTime()-t11)/1000000000.0+" "+Flag.sw.ands+"\n");
					double t = Flag.sw.stopTotal();
//					System.out.println(( (gc.offline.GCGen)env ).t);
//		:			System.out.println(Flag.sw.ands + " " + t / 1000000000.0
//							+ " " + Flag.sw.ands / t * 1000);
					Flag.sw.addCounter();

//					Runtime rt = Runtime.getRuntime();
//					double usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024.0 / 1024.0;
//					System.out.println("mem: " + usedMB);
				}
				System.out.println((System.nanoTime()-t1)/1000000000.0/(writeCount-10));

				for (int i = 0; i < readCount; ++i) {
					int element = i % N;
					GCSignal[] scb = client.read(client.baseOram.lib
							.toSignals(element));
					boolean[] b = client.baseOram.env.outputToAlice(scb);

					// Assert.assertTrue(Utils.toInt(b) == element);
					if (Utils.toInt(b) != element)
						System.out.println("inconsistent: " + element + " "
								+ Utils.toInt(b));
				}

				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static class EvaRunnable extends network.Client implements Runnable {

		String host;
		int port;

		EvaRunnable(String host, int port) {
			this.host = host;
			this.port = port;
		}

		public void run() {
			try {
				connect(host, port);

				int logN = is.read();
				int recurFactor = is.read();
				int logCutoff = is.read();
				int cutoff = 1 << logCutoff;
				int capacity = is.read();
				int dataSize = ByteBuffer.wrap(Server.readBytes(is, 4)).getInt();

				int N = 1 << logN;
				System.out
						.println("\nlogN recurFactor  cutoff capacity dataSize");
				System.out.println(logN + " " + recurFactor + " " + cutoff
						+ " " + capacity + " " + dataSize);
				@SuppressWarnings("unchecked")
				CompEnv<GCSignal> env = CompEnv.getEnv(Party.Bob,
						is, os);
				BSCircuitOram<GCSignal> server = new BSCircuitOram<GCSignal>(
						env, N, dataSize, 32,  cutoff, recurFactor, capacity, 80);
				
System.gc();
//				RecursiveOptCircuitOram<GCSignal>server = new RecursiveOptCircuitOram<GCSignal>(
//						env, N, dataSize,  cutoff, recurFactor, capacity, 80);

				
				for (int i = 0; i < writeCount; ++i) {
					int element = i % N;
					GCSignal[] scData = server.baseOram.env
							.inputOfAlice(new boolean[dataSize]);
					Flag.sw.startTotal();
					server.write(server.baseOram.lib.toSignals(element), scData);
					 Flag.sw.stopTotal();
					 Flag.sw.addCounter();
//					printStatistic();
				}

				int cnt = 0;
				for (int i = 0; i < readCount; ++i) {
					int element = i % N;
					GCSignal[] scb = server.read(server.baseOram.lib
							.toSignals(element));
					server.baseOram.env.outputToAlice(scb);
					if (i % N == 0)
						System.out.println(cnt++);
				}

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
