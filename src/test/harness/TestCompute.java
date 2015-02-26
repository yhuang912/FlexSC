package test.harness;
import java.math.BigInteger;

import org.junit.Test;

import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.Party;
import gc.GCSignal;
import gc.Garbler;


public class TestCompute extends TestHarness {

	public static int PORT = -1;
	public static int ID = -1;
	public <T>T[] secureCompute(T[] a, T[] b, CompEnv<T> env) {
		IntegerLib<T> lib = new IntegerLib<T>(env);
		T[] res = null;
		
		Garbler gb = new Garbler();
		double t1 = System.nanoTime();
		for(int i = 0; i < 40894460; ++i) {
			gb.enc(GCSignal.ZERO, GCSignal.ZERO, 0, GCSignal.ZERO);
		}
		double t2 = System.nanoTime();
		double t = (t2-t1)/1000000000.0;
		System.out.println(ID + "\t" + t +"\t"+ 40894460.0 * 0.75 * 80 * 3/t);
		return res;
	}
	int LEN = 4089446;
	class GenRunnable<T> extends network.Server implements Runnable {
		boolean[] z;

		public void run() {
			try {
				System.out.println("hello");
				listen(PORT);
				System.out.println("connected");
				@SuppressWarnings("unchecked")
				CompEnv<T> gen = CompEnv.getEnv(Mode.REAL, Party.Alice, is, os);

				T[] a = gen.inputOfAlice(Utils.fromBigInteger(BigInteger.ONE, LEN));
				T[] b = gen.inputOfBob(new boolean[LEN]);

				T[] d = secureCompute(a, b, gen);
				os.flush();

				z = gen.outputToAlice(d);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable<T> extends network.Client implements Runnable {
		public double andgates;
		public double encs;

		public void run() {
			try {
				System.out.println("hello");
				connect("localhost", PORT);
				System.out.println("connected");
				@SuppressWarnings("unchecked")
				CompEnv<T> env = CompEnv.getEnv(Mode.REAL, Party.Bob, is, os);

				T[] a = env.inputOfAlice(new boolean[LEN]);
				T[] b = env.inputOfBob(Utils.fromBigInteger(BigInteger.ONE, LEN));

//				if (Flag.mode == Mode.COUNT) {
//					((PMCompEnv) env).statistic.flush();
//					;
//				}
				T[] d = secureCompute(a, b, env);
//				if (Flag.mode == Mode.COUNT) {
//					((PMCompEnv) env).statistic.finalize();
//					andgates = ((PMCompEnv) env).statistic.andGate;
//					encs = ((PMCompEnv) env).statistic.NumEncAlice;
//				}

				env.outputToAlice(d);
				os.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	@Test
	public <T>void runThreads() throws Exception {
		GenRunnable<T> gen = new GenRunnable<T>();
		EvaRunnable<T> env = new EvaRunnable<T>();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(env);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();
	}
	
	public static void main(String args[]) throws Exception {
		TestCompute test = new TestCompute();
		TestCompute.PORT = Integer.parseInt(args[1]);
		TestCompute.ID = Integer.parseInt(args[2]); 
		 if(new Integer(args[0]) == 0)
			 test.new GenRunnable().run();
		 else test.new EvaRunnable().run();
	}
}