package oakland.ml;

import circuits.arithmetic.ArithmeticLib;
import circuits.arithmetic.FixedPointLib;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.PMCompEnv;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;

//import gc.GCSignal;

public class KNN {

	public static int numEntries;
	public static int VL = 23;
	public static int PL = 8;

	public static <T>T[] distance(T[][] a, T[][] b, ArithmeticLib<T> lib) {
		T[] res = lib.publicValue(0);
		for(int i = 0; i < a.length; ++i){
			T[] diff = lib.sub(a[i], b[i]);
			res = lib.add(lib.multiply(diff, diff), res);
		}
		return res;
	}

	public static int k = 10;
	static public double[][] clearcor;
	static public double[] clearlabel;
	static public double[] clearcen;
	static void preareData(int length, int dim) {
		clearcor = new double[length][dim];
		for(int i = 0; i < length; ++i)
			for(int j = 0; j < dim; ++j)
				clearcor[i][j] = CompEnv.rnd.nextDouble()*10;

		clearlabel = new double[length];
		for(int i = 0; i < length; ++i)
			clearlabel[i] = CompEnv.rnd.nextDouble()*10;

		clearcen = new double[dim];
		for(int i = 0; i < dim; ++i)
			clearcen[i] = CompEnv.rnd.nextDouble()*10;
	}

	public static <T>T[] secureCompute(ArithmeticLib<T> lib) {
		T[] res = null;
		double[] time = new double[3];
//		for(int tt = 0; tt < 3; ++tt) {
			double d1 = System.nanoTime();
			IntegerLib<T> ilib = new IntegerLib<T>(lib.getEnv());
			T[][][] cor = lib.getEnv().newTArray(clearcor.length, clearcor[0].length, 1);
			for(int i = 0; i < clearcor.length; ++i)
				for(int j = 0; j < clearcor[0].length; ++j)
					cor[i][j] = lib.inputOfAlice(clearcor[i][j]);

			T[][] cen = lib.getEnv().newTArray(clearcen.length, 1);
			for(int i = 0; i <cen.length;++i)
				cen[i] = lib.inputOfAlice(clearcen[i]);

			T[][] label = lib.getEnv().newTArray(clearlabel.length, 1);
			for(int i = 0; i <label.length;++i)
				label[i] = lib.inputOfAlice(clearlabel[i]);

			T[][] dis = lib.getEnv().newTArray(cor.length, 0);
			for(int i = 0; i < cor.length; ++i) {
				dis[i] = distance(cor[i], cen, lib); 
			}
			T[] used = ilib.zeros(cor.length);
			T[][] mini = lib.getEnv().newTArray(k, 0);
			for(int i = 0; i < k; ++i) {
				T[] curDis = dis[0];
				T[] ITH = ilib.zeros(15);
				for(int l = 1; l < cor.length; ++l) {
					T better = lib.leq(dis[i], curDis);
					better = ilib.and(better, ilib.not(used[i]));
					curDis = ilib.mux(curDis, dis[i], better);
					ITH = ilib.mux(ITH, ilib.toSignals(i, 15), better);
				}
				mini[i] = label[0];
				for(int l = 1; l < cor.length; ++l) {
					T eq = ilib.eq(ITH, ilib.toSignals(i, 15));
					used[i] = ilib.mux(used[i], ilib.SIGNAL_ONE,eq);
					mini[i] = ilib.mux(label[i],  mini[i], eq);
				}
			}

			res =mini[0];
			for(int i = 1; i < k; ++i ){
				res = ilib.add(res, mini[i]);
			}
			res = lib.div(res, lib.publicValue(k));
			time[0] = System.nanoTime() - d1;
//		}
//		Arrays.sort(time);
//		if(lib.getEnv().getParty() == Party.Alice && m == Mode.REAL)
			System.out.print(" "+time[0]/1000000000.0);
		return res;
	}

	static class GenRunnable<T> extends network.Server implements Runnable {
		int[] z;
		int[] h;
		Statistics sta;

		GenRunnable() {
		}

		public void run() {
			try {
				listen(54321);

				CompEnv<T> env = CompEnv.getEnv(Party.Alice, is, os);
				//				ArithmeticLib<T> lib = new FloatLib<T>(env, VL, PL);
				ArithmeticLib<T> lib = new FixedPointLib<T>(env, 32, 20);

				if(Flag.mode == Mode.COUNT) {
					sta = ((PMCompEnv)env).statistic;
					sta.flush();
				}
				secureCompute(lib);
				if(Flag.mode == Mode.COUNT) {
					sta.finalize();
				}
				env.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	static class EvaRunnable<T> extends network.Client implements Runnable {
		public void run() {
			try {
				connect("localhost", 54321);
				CompEnv<T> env = CompEnv.getEnv(Party.Bob, is, os);
				//				ArithmeticLib<T> lib = new FloatLib<T>(env, VL, PL);
				ArithmeticLib<T> lib = new FixedPointLib<T>(env, 32, 20);
				secureCompute(lib);
				env.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static Statistics getCount(int length) throws InterruptedException {
		preareData(2000, k);
		GenRunnable<Boolean> gen = new GenRunnable<Boolean>();
		EvaRunnable<Boolean> eva = new EvaRunnable<Boolean>();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();
		return gen.sta;
	}

	static public void main1(String args[]) throws InterruptedException {
		for(int dim = 2; dim <= 2; ++dim) {
			for(int i =  2; i <= 2; i+=3) {
				KNN.k = 3;
				preareData(1<<17, dim);
				GenRunnable gen = new GenRunnable();
				EvaRunnable eva = new EvaRunnable();
				Thread tGen = new Thread(gen);
				Thread tEva = new Thread(eva);
				tGen.start();
				Thread.sleep(5);
				tEva.start();
				tGen.join();
				tEva.join();
				if(Flag.mode == Mode.COUNT)
					System.out.print(" "+gen.sta.andGate);
			}
			System.out.print("\n");
		}
	}

	public static void main(String args[]) throws Exception {
		KNN.k = 3;
		preareData(1<<17, 2);
		if(args.length==0) {
			GenRunnable gen = new GenRunnable();
			EvaRunnable env = new EvaRunnable();
			Thread tGen = new Thread(gen);
			Thread tEva = new Thread(env);
			tGen.start();
			Thread.sleep(5);
			tEva.start();
			tGen.join();
			tEva.join();
		}
		else if(new Integer(args[0]) == 0)
			new GenRunnable().run();
		else new EvaRunnable().run();
		System.out.println(" "+Flag.sw.ands);
	}
}