package test.parallel;

import java.util.Random;

import oram.SecureArray;
import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;

public class HistogramOram<T> {
	static public Mode m = Mode.REAL;
	static int NUMBER_OF_ACCESSES;
	static int DATASIZE = 10;
	static int[] a;
	static int[] b;

	static public void main(String args[]) throws InterruptedException {

		Random rnd = new Random();

		a = new int[NUMBER_OF_ACCESSES];
		b = new int[NUMBER_OF_ACCESSES];
		for (int i = 0; i < NUMBER_OF_ACCESSES; ++i)
			a[i] = rnd.nextInt(1 << DATASIZE);
		for (int i = 0; i < NUMBER_OF_ACCESSES; ++i)
			b[i] = rnd.nextInt(1 << DATASIZE);

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

	public static Statistics getCount(int length) throws InterruptedException {
		NUMBER_OF_ACCESSES = length;
		Random rnd = new Random();

		a = new int[NUMBER_OF_ACCESSES];
		b = new int[NUMBER_OF_ACCESSES];
		for (int i = 0; i < NUMBER_OF_ACCESSES; ++i)
			a[i] = rnd.nextInt(1 << DATASIZE);
		for (int i = 0; i < NUMBER_OF_ACCESSES; ++i)
			b[i] = rnd.nextInt(1 << DATASIZE);

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

				T[][] sca = env.newTArray(NUMBER_OF_ACCESSES / 2, 0);
				T[][] scb = env.newTArray(NUMBER_OF_ACCESSES / 2, 0);
				T[][] sc = env.newTArray(NUMBER_OF_ACCESSES, 0);
				for (int i = 0; i < scb.length; ++i)
					sca[i] = env.inputOfAlice(Utils.fromInt(b[i], DATASIZE));
				for (int i = 0; i < scb.length; ++i)
					scb[i] = env.inputOfBob(new boolean[DATASIZE]);
				IntegerLib<T> lib = new IntegerLib(env);
				System.arraycopy(sca, 0, sc, 0, sca.length);
				System.arraycopy(scb, 0, sc, sca.length, scb.length);

				SecureArray<T> oram = new SecureArray<T>(
						env, 1 << DATASIZE, DATASIZE);
				for (int i = 0; i < NUMBER_OF_ACCESSES; ++i) {
					T[] cnt = oram.read(sc[i]);
					cnt = lib.incrementByOne(cnt);
					oram.write(sc[i], cnt);
				}
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

				T[][] sca = env.newTArray(NUMBER_OF_ACCESSES / 2, 0);
				T[][] scb = env.newTArray(NUMBER_OF_ACCESSES / 2, 0);
				T[][] sc = env.newTArray(NUMBER_OF_ACCESSES, 0);

				for (int i = 0; i < scb.length; ++i)
					sca[i] = env.inputOfAlice(new boolean[DATASIZE]);
				for (int i = 0; i < scb.length; ++i)
					scb[i] = env.inputOfBob(Utils.fromInt(b[i], DATASIZE));

				IntegerLib<T> lib = new IntegerLib(env);
				System.arraycopy(sca, 0, sc, 0, sca.length);
				System.arraycopy(scb, 0, sc, sca.length, scb.length);

				SecureArray<T> oram = new SecureArray<T>(
						env, 1 << DATASIZE, DATASIZE);
				long startTime = System.nanoTime();
				for (int i = 0; i < NUMBER_OF_ACCESSES; ++i) {
					T[] cnt = oram.read(sc[i]);
					cnt = lib.incrementByOne(cnt);
					oram.write(sc[i], cnt);
					if (((i + 1) & i) == 0) {
						long endTime = System.nanoTime();
						System.out.println(i + 1 + "," + (endTime - startTime)/1000000000.0);
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