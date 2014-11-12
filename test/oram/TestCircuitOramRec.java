package oram;

import org.junit.Test;

import util.Utils;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.Party;
import gc.GCSignal;

public class TestCircuitOramRec {

	public  static void main(String args[]) throws Exception {
		for(int i = 20; i <=20 ; i++) {
			GenRunnable gen = new GenRunnable(12345, i, 3, 32, 8, 6);
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
	}
	@Test
	public void runThreads() throws Exception {
		GenRunnable gen = new GenRunnable(12345, 20, 3, 32, 8, 6);
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

	public TestCircuitOramRec() {
	}

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
				os.write(dataSize);
				os.flush();

				System.out.println("\nlogN recurFactor  cutoff capacity dataSize");
				System.out.println(logN + " " + recurFactor + " " + cutoff
						+ " " + capacity + " " + dataSize);

				@SuppressWarnings("unchecked")
				CompEnv<GCSignal> env = CompEnv.getEnv(Mode.REAL, Party.Alice,
						is, os);
				RecursiveCircuitOram<GCSignal> client = new RecursiveCircuitOram<GCSignal>(
						env, N, dataSize, cutoff, recurFactor, capacity, 80);

				for (int i = 0; i < writeCount; ++i) {
					int element = i % N;


					Flag.sw.ands = 0;
					GCSignal[] scData = client.baseOram.env.inputOfAlice(Utils
							.fromInt(element, dataSize));
					os.flush();
					Flag.sw.startTotal();
					client.write(client.baseOram.lib.toSignals(element), scData);
					double t = Flag.sw.stopTotal();
//					System.out.println(Flag.sw.ands + " " + t / 1000000000.0
//							+ " " + Flag.sw.ands / t * 1000);
					Flag.sw.addCounter();

					Runtime rt = Runtime.getRuntime();
					double usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024.0 / 1024.0;
//					System.out.println("mem: " + usedMB);
				}

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
				int dataSize = is.read();

				int N = 1 << logN;
				System.out
						.println("\nlogN recurFactor  cutoff capacity dataSize");
				System.out.println(logN + " " + recurFactor + " " + cutoff
						+ " " + capacity + " " + dataSize);

				@SuppressWarnings("unchecked")
				CompEnv<GCSignal> env = CompEnv.getEnv(Mode.REAL, Party.Bob,
						is, os);
				RecursiveCircuitOram<GCSignal> server = new RecursiveCircuitOram<GCSignal>(
						env, N, dataSize, cutoff, recurFactor, capacity, 80);
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