package harness;

import org.junit.Assert;

import util.Utils;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.Party;

public class Test_2Input1Output extends TestHarness {

	public static abstract class Helper {
		int intA, intB;
		boolean[] a;
		boolean[] b;

		public Helper(int aa, int bb) {
			intA = aa;
			intB = bb;

			a = Utils.fromInt(aa, 32);
			b = Utils.fromInt(bb, 32);
		}

		public abstract<T> T[] secureCompute(T[] Signala, T[] Signalb, CompEnv<T> e)
				throws Exception;

		public abstract int plainCompute(int x, int y);
	}

	public static class GenRunnable<T> extends network.Server implements Runnable {
		boolean[] z;
		Helper h;

		GenRunnable(Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				listen(54321);
				System.out.println(socketChannel);
				@SuppressWarnings("unchecked")
				CompEnv<T> gen = CompEnv.getEnv(Party.Alice, this);
				System.out.println("gen1");
				T[] a = gen.inputOfAlice(h.a);
				System.out.println("gen2");
				T[] b = gen.inputOfBob(new boolean[32]);
				System.out.println("gen3");
				flush();
				T[] d = h.secureCompute(a, b, gen);
				System.out.println("gen4");
				flush();

				z = gen.outputToAlice(d);
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static class EvaRunnable<T> extends network.Client implements Runnable {
		Helper h;
		public double andgates;
		public double encs;

		EvaRunnable(Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				connect("localhost", 54321);
				System.out.println(socketChannel);
				@SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(Party.Bob, this);

				System.out.println("eva1");
				T[] a = env.inputOfAlice(new boolean[32]);
				System.out.println("eva2");
				T[] b = env.inputOfBob(h.b);
				System.out.println("eva3");
				flush();

				T[] d = h.secureCompute(a, b, env);
				System.out.println("eva4");
				flush();

				env.outputToAlice(d);
				flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	static public <T>void runThreads(Helper h) throws Exception {
		GenRunnable<T> gen = new GenRunnable<T>(h);
		EvaRunnable<T> env = new EvaRunnable<T>(h);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(env);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();

		if (Flag.mode == Mode.COUNT) {
			System.out.println(env.andgates + " " + env.encs);
		} else {
			System.out.println(h.plainCompute(h.intA, h.intB)+" "+Utils.toSignedInt(gen.z));
			Assert.assertEquals(h.plainCompute(h.intA, h.intB),
					Utils.toSignedInt(gen.z));
		}
		// System.out.println(Arrays.toString(gen.z));

	}

}