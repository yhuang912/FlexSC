package oakland.histogram;

import java.util.Random;

import oram.RecursiveCircuitOram;
import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;

public class ORAMHistogram {
	static public Mode m = Mode.COUNT;
	static int numEntries;
	final static int logDomain = MapreduceHistogram.logDomain;
	static int[] a;
	static int[] b;

	static public void main(String args[]) throws InterruptedException {

		Random rnd = new Random();

		a = new int[numEntries];
		b = new int[numEntries];
		for (int i = 0; i < numEntries; ++i)
			a[i] = rnd.nextInt(1 << 32);
		for (int i = 0; i < numEntries; ++i)
			b[i] = rnd.nextInt(1 << 32);

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
		numEntries = length;
		Random rnd = new Random();

		a = new int[numEntries];
		b = new int[numEntries];
		for (int i = 0; i < numEntries; ++i)
			a[i] = rnd.nextInt(1 << 32);
		for (int i = 0; i < numEntries; ++i)
			b[i] = rnd.nextInt(1 << 32);

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

	static class GenRunnable extends network.Server implements Runnable {
		int[] z;
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
				for (int i = 0; i < scb.length; ++i)
					sca[i] = env.inputOfAlice(Utils.fromInt(b[i], 32));
				for (int i = 0; i < scb.length; ++i)
					scb[i] = env.inputOfBob(new boolean[32]);
				IntegerLib<Boolean> lib = new IntegerLib(env);
				System.arraycopy(sca, 0, sc, 0, sca.length);
				System.arraycopy(scb, 0, sc, sca.length, scb.length);

				RecursiveCircuitOram<Boolean> oram = new RecursiveCircuitOram<Boolean>(
						env, 1 << logDomain, 32);
				sta = ((PMCompEnv) env).statistic;
				sta.flush();
				for (int i = 0; i < sc.length; ++i) {
					Boolean[] temp = lib.rightPublicShift(sc[i], 32 - 11);
					Boolean[] cnt = oram.read(temp);
					cnt = lib.incrementByOne(cnt);
					oram.write(temp, cnt);
				}
				sta.finalize();
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

				for (int i = 0; i < scb.length; ++i)
					sca[i] = env.inputOfAlice(new boolean[32]);
				for (int i = 0; i < scb.length; ++i)
					scb[i] = env.inputOfBob(Utils.fromInt(b[i], 32));

				IntegerLib<Boolean> lib = new IntegerLib(env);
				System.arraycopy(sca, 0, sc, 0, sca.length);
				System.arraycopy(scb, 0, sc, sca.length, scb.length);

				RecursiveCircuitOram<Boolean> oram = new RecursiveCircuitOram<Boolean>(
						env, 1 << logDomain, 32);
				for (int i = 0; i < sc.length; ++i) {
					Boolean[] temp = lib.rightPublicShift(sc[i], 32 - 11);
					Boolean[] cnt = oram.read(temp);
					cnt = lib.incrementByOne(cnt);
					oram.write(temp, cnt);
				}

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
