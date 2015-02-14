package harness;

import org.junit.Assert;

import util.Utils;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.PMCompEnv;
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

				T[] a = gen.inputOfAlice(h.a);

				T[] b = gen.inputOfBob(new boolean[32]);
				flush();
				T[] d = h.secureCompute(a, b, gen);
				flush();
				System.out.println("...");

				z = gen.outputToAlice(d);
				System.out.println("...");

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

				System.out.println("...1");

				T[] a = env.inputOfAlice(new boolean[32]);

				System.out.println("...2");
				T[] b = env.inputOfBob(h.b);
				flush();
				System.out.println("...3");


				T[] d = h.secureCompute(a, b, env);
				flush();
				System.out.println("...4");

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