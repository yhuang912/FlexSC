package test.harness;

import org.junit.Assert;

import test.Utils;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.Party;


public class TestFloat<T> extends TestHarness<T>{
	public static int widthV = 23, widthP = 7;
	public abstract class Helper {
		double a,b;

		public Helper(double a, double b) {
			this.b = b;
			this.a = a;
		}
		public abstract T[] secureCompute(T[] a, T[] b, CompEnv<T> env) throws Exception;
		public abstract double plainCompute(double a, double b);
	}

	class GenRunnable extends network.Server implements Runnable {
		Helper h;
		double z;

		GenRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				listen(54321);
				@SuppressWarnings("unchecked")
				CompEnv<T> gen = CompEnv.getEnv(m, Party.Alice, is, os);

				T[] f1 = gen.inputOfAlice(Utils.fromFloat(h.a, widthV, widthP));
				T[] f2 = gen.inputOfBob(Utils.fromFloat(0, widthV, widthP));
				T[] re = h.secureCompute(f1, f2, gen);
				Assert.assertTrue(re.length == widthP+widthV+1);
				z = Utils.toFloat(gen.outputToAlice(re), widthV, widthP);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		Helper h;
		public double andgates;
		public double encs;

		EvaRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				connect("localhost", 54321);	
				@SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(m, Party.Bob, is, os);

				T[] f1 = env.inputOfAlice(Utils.fromFloat(0, widthV, widthP));
				T[] f2 = env.inputOfBob(Utils.fromFloat(h.b, widthV, widthP));
				if(m == Mode.COUNT) {
					((PMCompEnv)env).statistic.flush();;
				}

				T[] re = h.secureCompute(f1, f2, env);

				if(m == Mode.COUNT) {
					((PMCompEnv)env).statistic.finalize();
					andgates = ((PMCompEnv)env).statistic.andGate;
					encs = ((PMCompEnv)env).statistic.NumEncAlice;
				}					

				env.outputToAlice(re);
				
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void runThreads(Helper h) throws Exception {
		GenRunnable gen = new GenRunnable(h);
		EvaRunnable env = new EvaRunnable(h);

		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(env);
		tGen.start(); Thread.sleep(1);
		tEva.start();
		tGen.join();
		if(m == Mode.COUNT) {
			System.out.println(env.andgates+" "+env.encs);
		} 
		else {
			double error = 0;
			if(gen.z != 0)
				error = Math.abs((h.plainCompute(h.a, h.b)-gen.z)/gen.z);
			else error = Math.abs((h.plainCompute(h.a, h.b)-gen.z));
			
			if(Math.abs((h.plainCompute(h.a, h.b)-gen.z)/gen.z)>1E-3)
				System.out.print(error+" "+gen.z+" "+h.plainCompute(h.a, h.b)+" "+h.a+" "+h.b+"\n");
			Assert.assertTrue(error<=1E-3);
		}
	}
}