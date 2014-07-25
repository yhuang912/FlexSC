package test.others;

import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;
import gc.GCSignal;

import java.util.Random;

import org.junit.Test;

import test.Utils;
import PrivateOram.RecursiveCircuitOram;
public class TestOram {

	static int N = 100;
	static int dataSize = 32;
	static int loop = 10;

	public static void main(String[] args) throws Exception
	{
		TestOram test = new TestOram();
		Random rnd = new Random();
		int[] array = new int[N];
		for(int i = 0; i < N; ++i)
			array[i] = rnd.nextInt(N);
		int start = rnd.nextInt(N);
		test.runThreads(array, start, loop);
	}


	public static int plain(int[] data, int start, int loop) {
		int result = start;
		for(int i = 0; i < loop; ++i) {
			result = data[result];
		}
		return result;
	}
	@Test
	public void runThreads(int[] x, int y, int loop) throws Exception {
		GenRunnable gen = new GenRunnable(x, loop);
		EvaRunnable eva = new EvaRunnable(y, loop);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(10);
		tEva.start();
		tGen.join();

		System.out.println(plain(x, y, loop)+" "+Utils.toSignedInt(gen.z));
	}

	public TestOram() {
	}

	class GenRunnable extends network.Server  implements Runnable{
		int[]x;
		boolean[] z;
		int loop;
		GenRunnable (int[] x, int loop) {
			this.x = x;
			this.loop = loop;
		}
		public void run() {
			try {
				listen(54321);
				CompEnv<GCSignal> env = CompEnv.getEnv(Mode.REAL, Party.Alice, is, os);
				RecursiveCircuitOram<GCSignal> client = new RecursiveCircuitOram<GCSignal>(env, N, dataSize);
				for(int i = 0; i < x.length; ++i) {
					GCSignal[] scData = client.baseOram.env.inputOfAlice(Utils.fromInt(x[i], dataSize));
					client.write(client.baseOram.lib.toSignals(i), scData);
				}

				GCSignal[] scStart = client.baseOram.env.inputOfBob(new boolean[32]);
				for(int i = 0; i < loop; ++i){
					scStart = client.read(scStart);
				}

				z = client.baseOram.env.outputToAlice(scStart);
				
				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable{
		int y;
		int loop;
		EvaRunnable (int y, int loop) {
			this.y = y;
			this.loop = loop;
		}
		public void run() {
			try {
				connect("localhost", 54321);
				CompEnv<GCSignal> env = CompEnv.getEnv(Mode.REAL, Party.Bob, is, os);
				RecursiveCircuitOram<GCSignal> server = new RecursiveCircuitOram<GCSignal>(env, N, dataSize);

				
				for(int i = 0; i < N; ++i) {
					GCSignal[] scData = server.baseOram.env.inputOfAlice(new boolean[dataSize]);
					server.write(server.baseOram.lib.toSignals(i), scData);
				}

				GCSignal[] scStart = server.baseOram.env.inputOfBob(Utils.fromInt(y, 32));
				for(int i = 0; i < loop; ++i){
					scStart = server.read(scStart);
				}
				server.baseOram.env.outputToAlice(scStart);
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}