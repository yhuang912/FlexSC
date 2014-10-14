package compiledlib;

import oram.RecursiveCircuitOram;
import util.Utils;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;

public class TestORAMPriorityQueue {
	int cutoff, recurFactor, add;
	int logN;
	int capacity;
	int dataSize;
	public Statistics statistic;
	int ands;
	int encs;

	public TestORAMPriorityQueue(int logN, int keySize) {
		this.add = 12345;
		this.logN = logN;
		this.dataSize = keySize * 2 + logN * 2;
		statistic = new Statistics();
	}

	class GenRunnable extends network.Server implements Runnable {
		GenRunnable() {
		}

		public void run() {
			try {
				listen(add);
				CompEnv<Boolean> env = CompEnv.getEnv(Mode.COUNT, Party.Alice,
						is, os);
				RecursiveCircuitOram<Boolean> client = new RecursiveCircuitOram<Boolean>(
						env, 1 << logN, dataSize);
				Boolean[] scIden = env.inputOfAlice(Utils.fromInt(1,
						client.clients.get(0).lengthOfIden));
				client.read(scIden);
				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		EvaRunnable() {
		}

		public void run() {
			try {
				connect("localhost", add);
				CompEnv<Boolean> env = CompEnv.getEnv(Mode.COUNT, Party.Bob,
						is, os);
				RecursiveCircuitOram<Boolean> client = new RecursiveCircuitOram<Boolean>(
						env, 1 << logN, dataSize);
				Boolean[] scIden = env.inputOfAlice(Utils.fromInt(1,
						client.clients.get(0).lengthOfIden));

				PMCompEnv mce = (PMCompEnv) env;
				mce.statistic.flush();

				client.read(scIden);
				os.flush();
				mce.statistic.finalize();
				ands = (int) (mce.statistic.andGate * logN * 8);// 2 for
																// read/write 4
																// for paths
				encs = (int) (mce.statistic.NumEncAlice * logN * 8);

				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void count() throws Exception {
		GenRunnable gen = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start();
		Thread.sleep(10);
		tEva.start();
		tGen.join();
	}

	public static void main(String[] args) throws Exception {
		// args = new String[1];
		// args[0] = "10";
		TestORAMPriorityQueue c = new TestORAMPriorityQueue(
				new Integer(args[0]), 32);
		c.count();
		System.out.print(c.ands + "\t" + c.encs);
	}
}