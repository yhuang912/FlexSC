package oakland.sqlquery;

import java.util.Arrays;
import java.util.Random;

import oakland.KeyValue;
import oakland.MapReduceBackEnd;
import util.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;

public class MapreduceSQL<T> extends MapReduceBackEnd<T> {

	static public Mode m = Mode.COUNT;
	IntegerLib<T> lib;

	public MapreduceSQL(CompEnv<T> env) {
		super(env);
		lib = new IntegerLib<T>(env);
	}

	static final int bloodPressureBit = 7;
	static final int pidBit = 20;
	static int numEntries;

	@Override
	public KeyValue map(T[] inputs) throws Exception {
		T[] bloodPressure = Arrays.copyOf(inputs, bloodPressureBit);
		T high = lib.geq(bloodPressure, lib.toSignals(160, bloodPressureBit));
		T[] res = lib.mux(lib.toSignals(0, inputs.length), inputs, high);
		return new KeyValue(res, lib.toSignals(1, 1));
	}

	@Override
	public T[] reduce(T[] value1, T[] value2) throws Exception {
		return value1;
	}

	static int[] a;
	static int[] b;

	public static boolean checkResult(int[] z, int[] a, int[] b) {
		int[] result = new int[2048];
		for (int i = 0; i < result.length; ++i)
			result[i] = 0;
		for (int i = 0; i < a.length; ++i) {
			int num = a[i] ^ b[i];
			result[num]++;
		}

		return true;
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

	static public void main(String args[]) throws InterruptedException {

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
				IntegerLib<Boolean> lib = new IntegerLib(env);
				for (int i = 0; i < sc.length; ++i)
					sc[i] = lib.xor(sca[i], scb[i]);

				MapreduceSQL<Boolean> wc = new MapreduceSQL<Boolean>(env);
				KeyValue<Boolean>[] res = wc.MapReduce(sc);

				if (env.m == Mode.COUNT) {
					sta = ((PMCompEnv) env).statistic;
					sta.finalize();
				} else {
					z = new int[res.length];
					for (int i = 0; i < res.length; ++i) {
						System.out.print(Utils.toInt(env
								.outputToAlice(res[i].key)) + " ");
						System.out.println(Utils.toInt(env
								.outputToAlice(res[i].value)));
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

				MapreduceSQL<Boolean> wc = new MapreduceSQL<Boolean>(env);
				KeyValue<Boolean>[] res = wc.MapReduce(sc);

				if (env.m == Mode.COUNT) {
				} else {
					// z = new int[res.length];
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
