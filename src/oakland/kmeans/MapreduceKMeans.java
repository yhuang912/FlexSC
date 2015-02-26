package oakland.kmeans;

import java.util.Arrays;
import java.util.Random;

import oakland.KeyValue;
import oakland.MapReduceBackEnd;
import util.Utils;
import circuits.arithmetic.FixedPointLib;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;
import gc.GCSignal;

public class MapreduceKMeans<T> extends MapReduceBackEnd<T> {

	static public Mode m = Mode.COUNT;
	FixedPointLib<T> lib;
	IntegerLib<T> integerlib;
	static int max_iter = 1;

	public MapreduceKMeans(CompEnv<T> env, T[][] clusters) {
		super(env);
		lib = new FixedPointLib<T>(env, fixPointLength, offset);
		integerlib = new IntegerLib<T>(env);
		this.clusters = clusters;
	}

	static int offset = 20;
	static int fixPointLength = 32;
	static final int k = 10;
	static final int lengthK = 5;
	static int numEntries;
	T[][] clusters;

	public T[] dist(T[] p1, T[] p2) throws Exception {
		T[] difx = lib.sub(Arrays.copyOf(p1, fixPointLength),
				Arrays.copyOf(p2, fixPointLength));
		T[] dify = lib.sub(
				Arrays.copyOfRange(p1, fixPointLength, fixPointLength * 2),
				Arrays.copyOfRange(p2, fixPointLength, fixPointLength * 2));
		T[] difxsq = lib.multiply(difx, difx);
		T[] difysq = lib.multiply(dify, dify);
		return lib.add(difxsq, difysq);
	}

	@Override
	public KeyValue map(T[] inputs) throws Exception {
		T[] distance = dist(inputs, clusters[0]);
		T[] c = lib.publicValue(0);
		for (int i = 1; i < clusters.length; ++i) {
			T[] tmpDis = dist(inputs, clusters[i]);
			T update = integerlib.leq(tmpDis, distance);
			distance = integerlib.mux(distance, tmpDis, update);
			c = integerlib.mux(c, lib.publicValue(i), update);
		}
		T[] res = integerlib.env.newTArray(inputs.length + fixPointLength);
		System.arraycopy(inputs, 0, res, 0, inputs.length);
		System.arraycopy(lib.publicValue(1), 0, res, inputs.length,
				fixPointLength);
		return new KeyValue<T>(c, res);
	}

	@Override
	public T[] reduce(T[] value1, T[] value2) throws Exception {
		T[] x = Arrays.copyOf(value1, fixPointLength);
		T[] y = Arrays.copyOfRange(value1, fixPointLength, 2 * fixPointLength);
		T[] cnt = Arrays.copyOfRange(value1, fixPointLength * 2,
				3 * fixPointLength);

		T[] x2 = Arrays.copyOf(value2, fixPointLength);
		T[] y2 = Arrays.copyOfRange(value2, fixPointLength, 2 * fixPointLength);
		T[] cnt2 = Arrays.copyOfRange(value2, fixPointLength * 2,
				3 * fixPointLength);

		T[] sumX = lib.add(x, x2);
		T[] sumY = lib.add(y, y2);
		T[] sumCnt = lib.add(cnt, cnt2);

		T[] res = integerlib.env.newTArray(3 * fixPointLength);
		System.arraycopy(sumX, 0, res, 0, fixPointLength);
		System.arraycopy(sumY, 0, res, fixPointLength, fixPointLength);
		System.arraycopy(sumCnt, 0, res, fixPointLength * 2, fixPointLength);

		return res;
	}

	static double[] a;
	static double[] b;
	static double[] cenx;
	static double[] ceny;

	public static void genreateData(int length) {
		numEntries = length;
		Random rnd = new Random();

		a = new double[numEntries];
		b = new double[numEntries];
		cenx = new double[k];
		ceny = new double[k];

		for (int i = 0; i < a.length; ++i) {
			a[i] = rnd.nextDouble() + i % 3 * 23;
			b[i] = rnd.nextDouble() + i % 2 * 11;
		}
		for (int i = 0; i < k; ++i) {
			cenx[i] = rnd.nextDouble() * 100;
			ceny[i] = rnd.nextDouble() * 100;
		}
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
		genreateData(4096);

		m = Mode.REAL;
		GenRunnable env = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();
		Thread tGen = new Thread(env);
		Thread tEva = new Thread(eva);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();
	}

	static class GenRunnable extends network.Server implements Runnable {
		int[] z;
		Statistics sta;

		GenRunnable() {
		}

		public void run() {
			try {
				listen(54321);

				CompEnv<GCSignal> env = CompEnv.getEnv(m, Party.Alice, is, os);
				FixedPointLib<GCSignal> lib = new FixedPointLib(env,
						fixPointLength, offset);

				GCSignal[][] sc = new GCSignal[a.length][];
				for (int i = 0; i < sc.length; ++i) {
					sc[i] = new GCSignal[2 * fixPointLength];
					GCSignal[] x = env.inputOfAlice(Utils.fromFixPoint(a[i],
							fixPointLength, offset));
					GCSignal[] y = env.inputOfAlice(Utils.fromFixPoint(b[i],
							fixPointLength, offset));
					// GCSignal[] x = env.inputOfAliceFixedPoint(a[i],
					// fixPointLength, offset);
					// GCSignal[] y = env.inputOfAliceFixedPoint(b[i],
					// fixPointLength, offset);
					System.arraycopy(x, 0, sc[i], 0, x.length);
					System.arraycopy(y, 0, sc[i], x.length, y.length);
				}

				GCSignal[][] centers = new GCSignal[k][];
				for (int i = 0; i < k; ++i) {
					centers[i] = new GCSignal[2 * fixPointLength];
					GCSignal[] x = env.inputOfAlice(Utils.fromFixPoint(cenx[i],
							fixPointLength, offset));
					GCSignal[] y = env.inputOfAlice(Utils.fromFixPoint(ceny[i],
							fixPointLength, offset));
					System.arraycopy(x, 0, centers[i], 0, x.length);
					System.arraycopy(y, 0, centers[i], x.length, y.length);
				}
				MapreduceKMeans<GCSignal> wc = new MapreduceKMeans<GCSignal>(
						env, centers);
				KeyValue<GCSignal>[] res = null;
				for (int iter = 0; iter < max_iter; ++iter) {
					GCSignal[] cntRes = null;
					res = wc.MapReduce(sc, cntRes, k);
					for (int i = 0; i < k; ++i) {
						GCSignal[] x = Arrays.copyOfRange(res[i].value, 0,
								fixPointLength);
						GCSignal[] y = Arrays.copyOfRange(res[i].value,
								fixPointLength, fixPointLength * 2);
						GCSignal[] cnt = Arrays.copyOfRange(res[i].value,
								fixPointLength * 2, fixPointLength * 3);

						GCSignal[] avex = lib.div(x, cnt);
						GCSignal[] avey = lib.div(y, cnt);
						System.arraycopy(avex, 0, wc.clusters[i], 0,
								fixPointLength);
						System.arraycopy(avey, 0, wc.clusters[i],
								fixPointLength, fixPointLength);
						Double a = Utils.toFixPoint(env.outputToAlice(avex),
								offset);
						Double b = Utils.toFixPoint(env.outputToAlice(avey),
								offset);

						System.out.println("(" + a + "," + b + ")");
					}
					System.out.println(" ");
				}

				if (env.m == Mode.COUNT) {
					// sta = ((PMCompEnv)env).statistic;
					// sta.finalize();
					// System.out.println(sta.andGate);
				} else {
					z = new int[res.length];
					for (int i = 0; i < res.length; ++i) {
						// System.out.print(Utils.toInt(env.outputToAlice(res[i].key))
						// + " ");
						// System.out.println(Utils.toInt(env.outputToAlice(res[i].value)));
					}
					// if(checkResult(z, a, b))
					System.out.println("Verified");
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

				CompEnv<GCSignal> env = CompEnv.getEnv(m, Party.Bob, is, os);
				FixedPointLib<GCSignal> lib = new FixedPointLib<GCSignal>(env,
						fixPointLength, offset);

				GCSignal[][] sc = new GCSignal[a.length][];
				for (int i = 0; i < sc.length; ++i) {
					sc[i] = new GCSignal[2 * fixPointLength];
					GCSignal[] x = env.inputOfAlice(Utils.fromFixPoint(a[i],
							fixPointLength, offset));
					GCSignal[] y = env.inputOfAlice(Utils.fromFixPoint(b[i],
							fixPointLength, offset));
					System.arraycopy(x, 0, sc[i], 0, x.length);
					System.arraycopy(y, 0, sc[i], x.length, y.length);
				}
				GCSignal[][] centers = new GCSignal[k][];
				for (int i = 0; i < k; ++i) {
					centers[i] = new GCSignal[2 * fixPointLength];
					GCSignal[] x = env.inputOfAlice(Utils.fromFixPoint(cenx[i],
							fixPointLength, offset));
					GCSignal[] y = env.inputOfAlice(Utils.fromFixPoint(ceny[i],
							fixPointLength, offset));
					System.arraycopy(x, 0, centers[i], 0, x.length);
					System.arraycopy(y, 0, centers[i], x.length, y.length);
				}
				MapreduceKMeans<GCSignal> wc = new MapreduceKMeans<GCSignal>(
						env, centers);
				KeyValue<GCSignal>[] res = null;

				for (int iter = 0; iter < max_iter; ++iter) {
					GCSignal[] cntRes = null;
					res = wc.MapReduce(sc, cntRes, k);

					for (int i = 0; i < res.length; ++i) {
						GCSignal[] x = Arrays.copyOfRange(res[i].value, 0,
								fixPointLength);
						GCSignal[] y = Arrays.copyOfRange(res[i].value,
								fixPointLength, fixPointLength * 2);
						GCSignal[] cnt = Arrays.copyOfRange(res[i].value,
								fixPointLength * 2, fixPointLength * 3);

						GCSignal[] avex = lib.div(x, cnt);
						GCSignal[] avey = lib.div(y, cnt);
						System.arraycopy(avex, 0, wc.clusters[i], 0,
								fixPointLength);
						System.arraycopy(avey, 0, wc.clusters[i],
								fixPointLength, fixPointLength);
						env.outputToAlice(avex);
						env.outputToAlice(avey);
					}
				}
				if (env.m == Mode.COUNT) {
				} else {
					for (int i = 0; i < res.length; ++i) {
						env.outputToAlice(res[i].key);
						env.outputToAlice(res[i].value);
					}
				}

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
