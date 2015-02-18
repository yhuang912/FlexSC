package harness;

import org.junit.Assert;

import util.Utils;
import circuits.arithmetic.FloatLib;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.Party;

public class TestFloat extends TestHarness {
	public static int widthV = 24, widthP = 8;

	public static abstract class Helper {
		public String host;
		public int port;
		public double a, b;

		public Helper(double a, double b) {
			host = "localhost";
			port = 54321;
			this.b = b;
			this.a = a;
		}
		
		public Helper(double a, double b, String s, int p) {
			host = s;
			port = p;
			this.b = b;
			this.a = a;
		}

		public abstract<T> T[] secureCompute(T[] a, T[] b, FloatLib<T> env)
				throws Exception;

		public abstract double plainCompute(double a, double b);
	}

	static public class GenRunnable<T> extends network.Server implements Runnable {
		Helper h;
		double z;

		GenRunnable(Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				listen(h.port);
				@SuppressWarnings("unchecked")
				CompEnv<T> gen = CompEnv.getEnv(Party.Alice, is, os);

				FloatLib<T> lib = new FloatLib<T>(gen, widthV, widthP);
				T[] f1 = lib.inputOfAlice(h.a);
				T[] f2 = lib.inputOfBob(0);
				T[] re = h.secureCompute(f1, f2, lib);
				Assert.assertTrue(re.length == widthP + widthV + 1);
				z = Utils.toFloat(gen.outputToAlice(re), widthV, widthP);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	static public class EvaRunnable<T> extends network.Client implements Runnable {
		Helper h;
		public double andgates;
		public double encs;

		EvaRunnable(Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				connect(h.host, h.port);
				@SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(Party.Bob, is, os);

				FloatLib<T> lib = new FloatLib<T>(env, widthV, widthP);
				T[] f1 = lib.inputOfAlice(0);
				T[] f2 = lib.inputOfBob(h.b);
				if (Flag.mode == Mode.COUNT) {
					((PMCompEnv) env).statistic.flush();
				}

				T[] re = h.secureCompute(f1, f2, lib);

				if (Flag.mode == Mode.COUNT) {
					((PMCompEnv) env).statistic.finalize();
					andgates = ((PMCompEnv) env).statistic.andGate;
					encs = ((PMCompEnv) env).statistic.NumEncAlice;
				}

				env.outputToAlice(re);

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
		Thread.sleep(1);
		tEva.start();
		tGen.join();
		if (Flag.mode == Mode.COUNT) {
			System.out.println(env.andgates + " " + env.encs);
		} else {
			double error = 0;
			if (gen.z != 0)
				error = Math.abs((h.plainCompute(h.a, h.b) - gen.z) / gen.z);
			else
				error = Math.abs((h.plainCompute(h.a, h.b) - gen.z));

			if (Math.abs((h.plainCompute(h.a, h.b) - gen.z) / gen.z) > 1E-3)
				System.out.print(error + " " + gen.z + " "
						+ h.plainCompute(h.a, h.b) + " " + h.a + " " + h.b
						+ "\n");
			Assert.assertTrue(error <= 1E-3);
		}
	}
	
	static public void main(String[] arg) {
		double a = CompEnv.rnd.nextDouble() * (1 << 20) - (1 << 19);
		double b = CompEnv.rnd.nextDouble() * (1 << 20) - (1 << 19);
		Helper h = new Helper(a, b, arg[1], new Integer(arg[2])) {
			@Override
			public double plainCompute(double a, double b) {
				return a+b;
			}
			@Override
			public <T> T[] secureCompute(T[] a, T[] b, FloatLib<T> env)
					throws Exception {
				double t1 = System.nanoTime();
				for(int i = 0; i < 1000; ++i)
//					a = env.add(a, b);
					a = env.multiply(a, b);
				System.out.println((System.nanoTime() - t1)/1000/1000000000);
				return a;
			}
		};
		if(new Integer(arg[0]) == 0) {
			GenRunnable gen = new GenRunnable(h);
			Thread tGen = new Thread(gen);
			tGen.run();

		} else {
			EvaRunnable env = new EvaRunnable(h);
			Thread tEva = new Thread(env);
			tEva.run();
		}
	}
}