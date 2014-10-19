package oakland.histogram;

import java.util.Random;

import oakland.KeyValue;
import oakland.MapReduceBackEnd;
import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;

//import gc.Boolean;

public class MapreduceHistogram<T> extends MapReduceBackEnd<T> {

	static public Mode m = Mode.COUNT;
	IntegerLib<T> lib;

	public MapreduceHistogram(CompEnv<T> env) {
		super(env);
		lib = new IntegerLib<T>(env);
	}

	static final int logDomain = 16;
	static int numEntries;

	@Override
	public KeyValue map(T[] inputs) throws Exception {
		return new KeyValue(lib.rightPublicShift(inputs, logDomain),
				lib.toSignals(1, 32));
	}

	@Override
	public T[] reduce(T[] value1, T[] value2) throws Exception {
		return lib.add(value1, value2);
	}

	static int[] a;
	static int[] b;

	public static boolean checkResult(int[] z, int[] h, int[] a, int[] b) {
		int[] result = new int[2048];
		for (int i = 0; i < result.length; ++i)
			result[i] = 0;
		for (int i = 0; i < a.length; ++i) {
			result[a[i] >> logDomain]++;
			result[b[i] >> logDomain]++;
		}
		for (int i = 0; i < result.length; ++i)
			if (result[i] != 0) {
				int res = 0;
				for (int k = 0; k < z.length; ++k)
					if (z[k] == i)
						res = h[k];
				if (res != result[i])
					System.out.println("inconsistence: " + i + " " + result[i]
							+ " " + res);
			}

		System.out.println("Verified");

		return true;
	}

	public static void genreateData(int length) {
		numEntries = length;
		Random rnd = new Random();

		a = new int[numEntries / 2];
		b = new int[numEntries / 2];

		for (int i = 0; i < numEntries / 2; ++i)
			a[i] = rnd.nextInt(1 << 19);
		for (int i = 0; i < numEntries / 2; ++i)
			b[i] = rnd.nextInt(1 << 19);

	}

	public static Statistics getCount(int length) throws InterruptedException {
		genreateData(length);
		GenRunnable env = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();
		Thread tGen = new Thread(env);
		Thread tEva = new Thread(eva);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();

		return env.sta;
	}

	static public void main(String args[]) throws InterruptedException {
		genreateData(1000);
		GenRunnable gen = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();

	}

	static class GenRunnable extends network.Server implements Runnable {
		int[] z;
		int[] h;
		Statistics sta;

		GenRunnable() {
		}

		public void run() {
			try {
				listen(54321);

				CompEnv<Boolean> env = CompEnv.getEnv(m, Party.Alice, is, os);

				Boolean[][] sca = new Boolean[numEntries / 2][];
				Boolean[][] scb = new Boolean[numEntries / 2][];
				Boolean[][] sc = new Boolean[numEntries][];
				for (int i = 0; i < sca.length; ++i)
					sca[i] = env.inputOfAlice(Utils.fromInt(a[i], 32));
				for (int i = 0; i < scb.length; ++i)
					scb[i] = env.inputOfBob(new boolean[32]);

				System.arraycopy(sca, 0, sc, 0, sca.length);
				System.arraycopy(scb, 0, sc, sca.length, scb.length);
				sca=null;scb=null;System.gc();

				MapreduceHistogram<Boolean> wc = new MapreduceHistogram<Boolean>(
						env);				
				sta = ((PMCompEnv) env).statistic;
				sta.flush();
				KeyValue<Boolean>[] res = wc.MapReduce(sc);
				if (env.m == Mode.COUNT) {
					sta.finalize();
				} else {
					z = new int[res.length];
					h = new int[res.length];
					for (int i = 0; i < res.length; ++i) {
						z[i] = Utils.toInt(env.outputToAlice(res[i].key));
						h[i] = Utils.toInt(env.outputToAlice(res[i].value));

						System.out.print(z[i] + " ");
						System.out.println(h[i]);
					}
					env.os.flush();
					checkResult(z, h, a, b);
				}
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	static class EvaRunnable extends network.Client implements Runnable {
		EvaRunnable() {
		}

		public void run() {
			try {
				connect("localhost", 54321);

				CompEnv<Boolean> env = CompEnv.getEnv(m, Party.Bob, is, os);

				Boolean[][] sca = new Boolean[numEntries / 2][];
				Boolean[][] scb = new Boolean[numEntries / 2][];
				Boolean[][] sc = new Boolean[numEntries][];
				for (int i = 0; i < sca.length; ++i)
					sca[i] = env.inputOfAlice(new boolean[32]);
				for (int i = 0; i < scb.length; ++i)
					scb[i] = env.inputOfBob(Utils.fromInt(b[i], 32));

				System.arraycopy(sca, 0, sc, 0, sca.length);
				System.arraycopy(scb, 0, sc, sca.length, scb.length);
				sca=null;scb=null;System.gc();

				MapreduceHistogram<Boolean> wc = new MapreduceHistogram<Boolean>(env);
				KeyValue<Boolean>[] res = wc.MapReduce(sc);

				if (env.m != Mode.COUNT) {
					for (int i = 0; i < res.length; ++i) {
						env.outputToAlice(res[i].key);
						env.outputToAlice(res[i].value);
					}
				}
				env.flush();

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
