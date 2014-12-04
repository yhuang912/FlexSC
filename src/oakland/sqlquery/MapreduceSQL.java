package oakland.sqlquery;

import java.util.Arrays;
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
import gc.GCSignal;

public class MapreduceSQL<T> extends MapReduceBackEnd<T> {

	static public Mode m = Mode.REAL;
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

		GenRunnable env = new GenRunnable(numEntries);
		EvaRunnable eva = new EvaRunnable(numEntries);
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
		numEntries = 1<<14;
		Random rnd = new Random();

		a = new int[numEntries];
		b = new int[numEntries];

		for (int i = 0; i < numEntries; ++i)
			a[i] = rnd.nextInt(1 << 32);
		for (int i = 0; i < numEntries; ++i)
			b[i] = rnd.nextInt(1 << 32);

		GenRunnable gen = new GenRunnable(numEntries);
		EvaRunnable eva = new EvaRunnable(numEntries);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();
		flexsc.Flag.sw.addCounter();
		flexsc.Flag.sw.print();
		System.out.println(flexsc.Flag.sw.ands);

	}

	static public class GenRunnable extends network.Server implements Runnable {
		int[] z;
		Statistics sta;

		GenRunnable(int n ) {
			numEntries = n;
			Random rnd = new Random();

			a = new int[numEntries];
			b = new int[numEntries];

			for (int i = 0; i < numEntries; ++i)
				a[i] = rnd.nextInt(1 << 32);
			for (int i = 0; i < numEntries; ++i)
				b[i] = rnd.nextInt(1 << 32);
		}

		public void run() {
			try {
				listen(54325);

				CompEnv<GCSignal> env = CompEnv.getEnv(m, Party.Alice, is, os);

				GCSignal[][] sca = new GCSignal[numEntries][];
				GCSignal[][] scb = new GCSignal[numEntries][];
				GCSignal[][] sc = new GCSignal[numEntries][];
				for (int i = 0; i < scb.length; ++i)
					sca[i] = env.inputOfAlice(Utils.fromInt(b[i],
							bloodPressureBit + pidBit));
				for (int i = 0; i < scb.length; ++i)
					scb[i] = env.inputOfBob(new boolean[bloodPressureBit
							+ pidBit]);
				IntegerLib<GCSignal> lib = new IntegerLib(env);
				for (int i = 0; i < sc.length; ++i)
					sc[i] = lib.xor(sca[i], scb[i]);

				if (env.m == Mode.COUNT) {
//					sta = ((PMCompEnv) env).statistic;
//					sta.flush();
				}
				MapreduceSQL<GCSignal> wc = new MapreduceSQL<GCSignal>(env);
				KeyValue<GCSignal>[] res = wc.MapReduce(sc);

				if (env.m == Mode.COUNT) {
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

	static public class EvaRunnable extends network.Client implements Runnable {
		EvaRunnable(int n) {
				numEntries = n;
				Random rnd = new Random();

				a = new int[numEntries];
				b = new int[numEntries];

				for (int i = 0; i < numEntries; ++i)
					a[i] = rnd.nextInt(1 << 32);
				for (int i = 0; i < numEntries; ++i)
					b[i] = rnd.nextInt(1 << 32);
		}

		public void run() {
			try {
				connect("localhost", 54325);

				CompEnv<GCSignal> env = CompEnv.getEnv(m, Party.Bob, is, os);
				GCSignal[][] sca = new GCSignal[numEntries][];
				GCSignal[][] scb = new GCSignal[numEntries][];
				GCSignal[][] sc = new GCSignal[numEntries][];
				for (int i = 0; i < scb.length; ++i)
					sca[i] = env.inputOfAlice(Utils.fromInt(b[i],
							bloodPressureBit + pidBit));
				for (int i = 0; i < scb.length; ++i)
					scb[i] = env.inputOfBob(new boolean[bloodPressureBit
							+ pidBit]);
				IntegerLib<GCSignal> lib = new IntegerLib(env);
				for (int i = 0; i < sc.length; ++i)
					sc[i] = lib.xor(sca[i], scb[i]);

				MapreduceSQL<GCSignal> wc = new MapreduceSQL<GCSignal>(env);
				KeyValue<GCSignal>[] res = wc.MapReduce(sc);

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
