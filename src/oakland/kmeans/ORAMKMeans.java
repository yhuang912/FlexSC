package oakland.kmeans;

import java.util.Arrays;
import java.util.Random;

import oram.RecursiveCircuitOram;
import oram.SecureArray;
import util.Utils;
import circuits.arithmetic.FixedPointLib;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;

public class ORAMKMeans<T> {
	static public Mode m = Mode.COUNT;
	static double[] a;
	static double[] b;
	static double[] cenx;
	static double[] ceny;
	static int offset = 20;
	static int fixPointLength = 32;
	static  int k = 5;
	static  int lengthK = 5;
	static int numEntries;
	T[][] clusters;

	FixedPointLib<T> lib;
	IntegerLib<T> integerlib;
	static int max_iter = 1;

	public ORAMKMeans(CompEnv<T> env, T[][] clusters) {
		lib = new FixedPointLib<T>(env, fixPointLength, offset);
		integerlib = new IntegerLib<T>(env);
		this.clusters = clusters;
	}
	
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
	public T[] dist(T[] p1, T[] p2) {
		T[] difx = lib.sub(Arrays.copyOf(p1, fixPointLength),
				Arrays.copyOf(p2, fixPointLength));
		T[] dify = lib.sub(
				Arrays.copyOfRange(p1, fixPointLength, fixPointLength * 2),
				Arrays.copyOfRange(p2, fixPointLength, fixPointLength * 2));
		T[] difxsq = lib.multiply(difx, difx);
		T[] difysq = lib.multiply(dify, dify);
		return lib.add(difxsq, difysq);
	}
	
	public void kmeans(T[][] data) throws Exception {
		SecureArray<T> newClusters = new SecureArray(integerlib.getEnv(), k, lib.publicValue(0).length);
		SecureArray<T> newCounter = new SecureArray(integerlib.getEnv(), k, lib.publicValue(0).length);
		for(int i = 0; i < 1; ++i) {
			T[][] dis = lib.getEnv().newTArray(clusters.length, 1);
			for(int j = 0; j < clusters.length; ++j)
				dis[j] = dist(data[i], clusters[j]);
			T[] mini = dis[0];
			T[] minidex = integerlib.toSignals(0);
			for(int j = 1; j < clusters.length; ++j) {
				T change = lib.leq(dis[j], mini);
				mini = integerlib.mux(mini, dis[j], change);
				minidex = integerlib.mux(minidex, integerlib.toSignals(j), change);
			}
			T[] newC = newClusters.read(minidex);
			newClusters.write(minidex, lib.add(mini, newC));
			newC = newCounter.read(minidex);
			newCounter.write(minidex, lib.add(lib.publicValue(1), newC));
		}
//		for(int i = 0; i < k; ++i) {
//			clusters[i] = lib.div(newClusters.read(integerlib.toSignals(i)),
//								  newCounter.read(integerlib.toSignals(i)));
//		}
	}
	
	static public void runonce() throws InterruptedException {
		GenRunnable env = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();
		Thread tGen = new Thread(env);
		Thread tEva = new Thread(eva);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();
		System.out.print(env.sta.andGate);
	}
	
	static public void main(String args[]) throws InterruptedException {
		for(int i = 1024; i <= 1024; i*=2) {
			for(int l = 1024; l <= 1024; l*=2){
			k = i;
			genreateData(l);
			m = Mode.COUNT;
			runonce();//runonce();//runonce();
			}
			System.out.println(" ");
		}
	}

	public static Statistics getCount(int length) throws InterruptedException {
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

	static class GenRunnable<T> extends network.Server implements Runnable {
		int[] z;
		Statistics sta;

		GenRunnable() {
		}

		public void run() {
			try {
				listen(54321);

				CompEnv<T> env = CompEnv.getEnv(m, Party.Alice, is, os);

				T[][] sc = env.newTArray(a.length, 2 * fixPointLength);
				for (int i = 0; i < sc.length; ++i) {
					T[] x = env.inputOfAlice(Utils.fromFixPoint(a[i],
							fixPointLength, offset));
					T[] y = env.inputOfAlice(Utils.fromFixPoint(b[i],
							fixPointLength, offset));
					System.arraycopy(x, 0, sc[i], 0, x.length);
					System.arraycopy(y, 0, sc[i], x.length, y.length);
				}

				T[][] centers = env.newTArray(k, 2 * fixPointLength);
				for (int i = 0; i < k; ++i) {
					T[] x = env.inputOfAlice(Utils.fromFixPoint(cenx[i],
							fixPointLength, offset));
					T[] y = env.inputOfAlice(Utils.fromFixPoint(ceny[i],
							fixPointLength, offset));
					System.arraycopy(x, 0, centers[i], 0, x.length);
					System.arraycopy(y, 0, centers[i], x.length, y.length);
				}
				sta = ((PMCompEnv) env).statistic;
				sta.flush();
				ORAMKMeans kk = new ORAMKMeans<T>(env, centers);
				kk.kmeans(sc);
				sta = ((PMCompEnv) env).statistic;
				sta.finalize();
				sta.andGate*=sc.length;
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	static class EvaRunnable<T> extends network.Client implements Runnable {
		EvaRunnable() {
		}

		public void run() {
			try {
				connect("localhost", 54321);

				CompEnv<T> env = CompEnv.getEnv(m, Party.Bob, is, os);

				T[][] sc = env.newTArray(a.length, 2 * fixPointLength);
				for (int i = 0; i < sc.length; ++i) {
					T[] x = env.inputOfAlice(Utils.fromFixPoint(a[i],
							fixPointLength, offset));
					T[] y = env.inputOfAlice(Utils.fromFixPoint(b[i],
							fixPointLength, offset));
					System.arraycopy(x, 0, sc[i], 0, x.length);
					System.arraycopy(y, 0, sc[i], x.length, y.length);
				}

				T[][] centers = env.newTArray(k, 2 * fixPointLength);
				for (int i = 0; i < k; ++i) {
					T[] x = env.inputOfAlice(Utils.fromFixPoint(cenx[i],
							fixPointLength, offset));
					T[] y = env.inputOfAlice(Utils.fromFixPoint(ceny[i],
							fixPointLength, offset));
					System.arraycopy(x, 0, centers[i], 0, x.length);
					System.arraycopy(y, 0, centers[i], x.length, y.length);
				}
				ORAMKMeans kk = new ORAMKMeans<T>(env, centers);
				kk.kmeans(sc);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
