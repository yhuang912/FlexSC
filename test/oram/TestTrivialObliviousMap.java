package oram;

import java.util.TreeMap;

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
				TrivialObliviousMap<GCSignal> client = new TrivialObliviousMap<GCSignal>(gen);
				
				int[] k = new int[N];
				for(int i = 0; i < N; ++i) k[i] = CompEnv.rnd.nextInt();
				int[] v = new int[N];
				for(int i = 0; i < N; ++i) v[i] = CompEnv.rnd.nextInt();
				
				TreeMap<Long, boolean[]> m = new TreeMap();
				for(int i = 0; i < N; ++i)
					m.put((long) k[i], Utils.fromInt(v[i], 32));
				
				client.init(m, 32, 32);

				
				for (int i = 0; i < N; ++i) {
					GCSignal[] scb = client.read(gen.inputOfAlice(Utils.fromInt(k[i], indexSize)));
					boolean[] b = client.env.outputToAlice(scb);
					if (Utils.toInt(b) != v[i]) {
						System.out.println("inconsistent: " + i + " "
								+ Utils.toInt(b) + " " + v[i]);
					}
					else System.out.println(k[i]+" "+v[i]+" "+Utils.toInt(b));
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
				CompEnv<GCSignal> env = CompEnv.getEnv(Mode.REAL, Party.Bob, is, os);
				TrivialObliviousMap<GCSignal> server = new TrivialObliviousMap<GCSignal>(env);

				int[] keys = new int[N];
				int[] values = new int[N];

				server.init(N, 32,32);

				for (int i = 0; i < N; ++i) {
					GCSignal[] scb = server.read(env.inputOfAlice(new boolean[32]));
					server.env.outputToAlice(scb);
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
		GenRunnable gen = new GenRunnable(54321);
		EvaRunnable eva = new EvaRunnable("localhost", 54321);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start();
		Thread.sleep(10);
		tEva.start();
		tGen.join();
	}
}