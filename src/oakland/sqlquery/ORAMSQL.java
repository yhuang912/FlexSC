package oakland.sqlquery;

import java.util.Arrays;
import java.util.Random;

import oram.RecursiveCircuitOram;
import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;

public class ORAMSQL {
	static public Mode m = Mode.COUNT;
	static final int bloodPressureBit = 7;
	static final int pidBit = 20;
	static int numEntries;
	static int[] a;
	static int[] b;

	static public void main(String args[]) throws InterruptedException {

		Random rnd = new Random();

		a = new int[numEntries];
		b = new int[numEntries];
		for (int i = 0; i < numEntries; ++i)
			a[i] = rnd.nextInt();
		for (int i = 0; i < numEntries; ++i)
			b[i] = rnd.nextInt();

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
			a[i] = rnd.nextInt();
		for (int i = 0; i < numEntries; ++i)
			b[i] = rnd.nextInt();

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
				listen(54325);

				CompEnv<Boolean> env = CompEnv.getEnv(m, Party.Alice, is, os);

				Boolean[][] sca = new Boolean[numEntries][];
				Boolean[][] scb = new Boolean[numEntries][];
				Boolean[][] sc = new Boolean[numEntries][];
				for (int i = 0; i < scb.length; ++i)
					sca[i] = env.inputOfAlice(Utils.fromInt(b[i],
							bloodPressureBit + pidBit));
				for (int i = 0; i < scb.length; ++i)
					scb[i] = env.inputOfBob(new boolean[bloodPressureBit
							+ pidBit]);
				IntegerLib<Boolean> lib = new IntegerLib<Boolean>(env);
				for (int i = 0; i < sc.length; ++i)
					sc[i] = lib.xor(sca[i], scb[i]);

				RecursiveCircuitOram<Boolean> oram = new RecursiveCircuitOram<Boolean>(
						env, numEntries, bloodPressureBit + pidBit);
				sta = ((PMCompEnv) env).statistic;
				sta.flush();
				Boolean[] counter = lib.toSignals(0);
				for (int i = 0; i < 1; ++i) {
					Boolean[] inputs = sc[i];
					Boolean[] bloodPressure = Arrays.copyOf(inputs,
							bloodPressureBit);
					Boolean high = lib.geq(bloodPressure,
							lib.toSignals(160, bloodPressureBit));
					oram.write(counter, inputs);
					counter = lib.conditionalIncreament(counter, high);
				}

				sta = ((PMCompEnv) env).statistic;
				sta.finalize();
				sta.andGate*=sc.length;
				sta.NumEncAlice*=sc.length;
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
				connect("localhost", 54325);

				CompEnv<Boolean> env = CompEnv.getEnv(m, Party.Bob, is, os);

				Boolean[][] sca = new Boolean[numEntries][];
				Boolean[][] scb = new Boolean[numEntries][];
				Boolean[][] sc = new Boolean[numEntries][];
				for (int i = 0; i < scb.length; ++i)
					sca[i] = env.inputOfAlice(Utils.fromInt(b[i],
							bloodPressureBit + pidBit));
				for (int i = 0; i < scb.length; ++i)
					scb[i] = env.inputOfBob(new boolean[bloodPressureBit
							+ pidBit]);
				IntegerLib<Boolean> lib = new IntegerLib(env);
				for (int i = 0; i < sc.length; ++i)
					sc[i] = lib.xor(sca[i], scb[i]);

				RecursiveCircuitOram<Boolean> oram = new RecursiveCircuitOram<Boolean>(
						env, numEntries, bloodPressureBit + pidBit);
				Boolean[] counter = lib.toSignals(0);
				for (int i = 0; i < sc.length; ++i) {
					Boolean[] inputs = sc[i];
					Boolean[] bloodPressure = Arrays.copyOf(inputs,
							bloodPressureBit);
					Boolean high = lib.geq(bloodPressure,
							lib.toSignals(160, bloodPressureBit));
					oram.write(counter, inputs);
					counter = lib.conditionalIncreament(counter, high);
				}

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
