package oram;

import orambs.TrivialObliviousMap;

import org.junit.Test;

import util.Utils;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;
import gc.GCSignal;

public class TestTrivialObliviousMap {
	final int N = 100;
	int indexSize = 32;
	int dataSize = 32;


	class GenRunnable extends network.Server implements Runnable {
		int port;

		GenRunnable(int port) {
			this.port = port;
		}

		public void run() {
			try {
				listen(port);

				@SuppressWarnings("unchecked")
				CompEnv<GCSignal> gen = CompEnv.getEnv(Mode.REAL, Party.Alice,
						is, os);
				TrivialObliviousMap<GCSignal> client = new TrivialObliviousMap<GCSignal>(gen, N, indexSize, dataSize);

				int[] keys = new int[N];
				int[] values = new int[N];
				for(int i = 0; i < N; ++i) {
					keys[i] = CompEnv.rnd.nextInt();
					values[i] = CompEnv.rnd.nextInt();
				}
				client.initialize(keys, values, Party.Alice);

				for (int i = 0; i < N; ++i) {
					GCSignal[] scb = client.read(gen.inputOfAlice(Utils.fromInt(keys[i], indexSize)));
					boolean[] b = client.env.outputToAlice(scb);
					if (Utils.toInt(b) != values[i]) {
						System.out.println("inconsistent: " + i + " "
								+ Utils.toInt(b) + " " + values[i]);
					}
					else System.out.println(keys[i]+" "+values[i]+" "+Utils.toInt(b));
				}

				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		String host;
		int port;

		EvaRunnable(String host, int port) {
			this.host = host;
			this.port = port;
		}

		public void run() {
			try {
				connect(host, port);

				@SuppressWarnings("unchecked")
				CompEnv<GCSignal> env = CompEnv.getEnv(Mode.REAL, Party.Bob,
						is, os);
				TrivialObliviousMap<GCSignal> client = new TrivialObliviousMap<GCSignal>(env, N, indexSize, dataSize);

				int[] keys = new int[N];
				int[] values = new int[N];

				client.initialize(keys, values, Party.Alice);

				for (int i = 0; i < N; ++i) {
					GCSignal[] scb = client.read(env.inputOfAlice(Utils.fromInt(keys[i], indexSize)));
					client.env.outputToAlice(scb);
					
				}

				os.flush();

				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	@Test
	public void runThreads() throws Exception {
		GenRunnable gen = new GenRunnable(12345);
		EvaRunnable eva = new EvaRunnable("localhost", 12345);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start();
		Thread.sleep(10);
		tEva.start();
		tGen.join();
	}
}