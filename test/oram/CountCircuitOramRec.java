package oram;

import util.Utils;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.Party;

public class CountCircuitOramRec {

	public  static void main(String args[]) throws Exception {

		for(int d = 5; d < 20; ++d) {
			System.out.print("datasize: "+40+"  ");
			GenRunnable gen = new GenRunnable(12345, 3, 40, 8, 6);
			EvaRunnable eva = new EvaRunnable("localhost", 12345);
			Thread tGen = new Thread(gen);
			Thread tEva = new Thread(eva);
			tGen.start();
			Thread.sleep(10);
			tEva.start();
			tGen.join();
		}
	}


	public CountCircuitOramRec() {
	}

	public static class GenRunnable extends network.Server implements Runnable {
		int port;
		int recurFactor;
		int cutoff;
		int capacity;
		int dataSize;
		int logCutoff;

		GenRunnable(int port, int capacity, int dataSize,
				int recurFactor, int logCutoff) {
			this.port = port;
			this.recurFactor = recurFactor;
			this.logCutoff = logCutoff;
			this.cutoff = 1 << logCutoff;
			this.dataSize = dataSize;
			this.capacity = capacity;
		}

		public void run() {
			try {
				listen(port);

				@SuppressWarnings("unchecked")
				CompEnv<Boolean> env = CompEnv.getEnv(Mode.COUNT, Party.Alice,
						is, os);

				for(int i = 4; i <= 10 ; i++) {
					RecursiveCircuitOram<Boolean> client = new RecursiveCircuitOram<Boolean>(
							env, 1<<i, dataSize, cutoff, recurFactor, capacity, 80);


					Boolean[] scData = client.baseOram.env.inputOfAlice(Utils
							.fromInt(1, dataSize));
					os.flush();
					Flag.sw.ands = 0;
					((PMCompEnv)env).statistic.flush();
					client.write(client.baseOram.lib.toSignals(1), scData);
					double ands = ((PMCompEnv)env).statistic.andGate;

					TrivialPrivateOram<Boolean> client2 = new TrivialPrivateOram<Boolean>(env, 1<<i, dataSize);
					scData = client2.env.inputOfAlice(Utils.fromInt(1, dataSize));
					Flag.sw.ands = 0;
					((PMCompEnv)env).statistic.flush();
					client2.write(client2.lib.toSignals(1), scData);
					if(ands < ((PMCompEnv)env).statistic.andGate) {
						System.out.println("\t"+(i-1));
						break;
					}
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

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}