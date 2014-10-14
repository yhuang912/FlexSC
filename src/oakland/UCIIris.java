package oakland;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import circuits.IntegerLib;
import circuits.arithmetic.ArithmeticLib;
import circuits.arithmetic.FixedPointLib;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.PMCompEnv.Statistics;
import flexsc.Party;
//import gc.Boolean;

public class UCIIris<T> extends MapReduceBackEnd<T> {

	static public Mode m = Mode.VERIFY;
	ArithmeticLib<T> lib;
	IntegerLib<T> integerlib;
	static int max_iter = 200;

	public UCIIris(ArithmeticLib<T> lib, T[][] clusters) {
		super(lib.getEnv());
		this.lib = lib;
		integerlib = new IntegerLib<T>(env);
		this.clusters = clusters;
	}
	
	static final int k = 5;
	static final int lengthK = 5;
	static int numEntries;
	T[][] clusters;

	public T[] dist(T[] p1, T[] p2) throws Exception {
		T[] dif1 = lib.sub(Arrays.copyOf(p1, lib.numBits()),
				Arrays.copyOf(p2, lib.numBits()));
		T[] dif2 = lib.sub(
				Arrays.copyOfRange(p1, lib.numBits(), lib.numBits() * 2),
				Arrays.copyOfRange(p2, lib.numBits(), lib.numBits() * 2));
		
		T[] dif3 = lib.sub(
				Arrays.copyOfRange(p1, 2*lib.numBits(), lib.numBits() * 3),
				Arrays.copyOfRange(p2, 2*lib.numBits(), lib.numBits() * 3));

		T[] dif4 = lib.sub(
				Arrays.copyOfRange(p1, 3*lib.numBits(), lib.numBits() * 4),
				Arrays.copyOfRange(p2, 3*lib.numBits(), lib.numBits() * 4));

		
		T[] dif1sq = lib.multiply(dif1, dif1);
		T[] dif2sq = lib.multiply(dif2, dif2);
		T[] dif3sq = lib.multiply(dif3, dif3);
		T[] dif4sq = lib.multiply(dif4, dif4);
		T[] res = lib.add(dif1sq, dif2sq);
		res = lib.add(res, dif3sq);
		res = lib.add(res, dif4sq);
		return res;
	}

	@Override
	public KeyValue map(T[] inputs) throws Exception {
		T[] distance = dist(inputs, clusters[0]);
		T[] c = lib.publicValue(0);
		for (int i = 1; i < clusters.length; ++i) {
			T[] tmpDis = dist(inputs, clusters[i]);
			T update = lib.leq(tmpDis, distance);
			distance = integerlib.mux(distance, tmpDis, update);
			c = integerlib.mux(c, lib.publicValue(i), update);
		}
		T[] res = integerlib.env.newTArray(inputs.length + lib.numBits());
//		System.out.println(res.length);
		System.arraycopy(inputs, 0, res, 0, inputs.length);
		System.arraycopy(lib.publicValue(1), 0, res, inputs.length,
				lib.numBits());
		return new KeyValue<T>(c, res);
	}

	@Override
	public T[] reduce(T[] value1, T[] value2) throws Exception {
		T[] x = Arrays.copyOf(value1, lib.numBits());
		T[] y = Arrays.copyOfRange(value1, lib.numBits(), 2 * lib.numBits());
		T[] z = Arrays.copyOfRange(value1, 2 * lib.numBits(), 3 * lib.numBits());
		T[] v = Arrays.copyOfRange(value1, 3 * lib.numBits(), 4 * lib.numBits());
		T[] cnt = Arrays.copyOfRange(value1, lib.numBits() * 4,
				5 * lib.numBits());

		T[] x2 = Arrays.copyOf(value2, lib.numBits());
		T[] y2 = Arrays.copyOfRange(value2, lib.numBits(), 2 * lib.numBits());
		T[] z2 = Arrays.copyOfRange(value2, 2 * lib.numBits(), 3 * lib.numBits());
		T[] v2 = Arrays.copyOfRange(value2, 3 * lib.numBits(), 4 * lib.numBits());
		T[] cnt2 = Arrays.copyOfRange(value2, lib.numBits() * 4,
				5 * lib.numBits());

		T[] sumX = lib.add(x, x2);
		T[] sumY = lib.add(y, y2);
		T[] sumZ = lib.add(z, z2);
		T[] sumV = lib.add(v, v2);

		T[] sumCnt = lib.add(cnt, cnt2);

		T[] res = integerlib.env.newTArray(5 * lib.numBits());
		System.arraycopy(sumX, 0, res, 0, lib.numBits());
		System.arraycopy(sumY, 0, res, lib.numBits(), lib.numBits());
		System.arraycopy(sumZ, 0, res, 2*lib.numBits(), lib.numBits());
		System.arraycopy(sumV, 0, res, 3*lib.numBits(), lib.numBits());
		System.arraycopy(sumCnt, 0, res, 4*lib.numBits(), lib.numBits());

		return res;
	}

	static double[][] a;
	static int[] classification;
	static double[][] cen;

	public static void genreateData() throws FileNotFoundException {
		numEntries = 150;
		a = new double[numEntries][4];
		classification = new int[numEntries];
		
		Scanner scanner = new Scanner(new File("/Users/wangxiao/git/FlexSC_rc/src/oakland/iris_data.csv"));
		cen = new double[k][4];
		int[] cnt = new int[]{0,0,0,0};
		int i = 0;
		while(scanner.hasNext()){
		   a[i][0] = scanner.nextDouble();
		   a[i][1] = scanner.nextDouble();
		   a[i][2] = scanner.nextDouble();
		   a[i][3] = scanner.nextDouble();
		   classification[i] = scanner.nextInt();
		   cen[classification[i]][0] += a[i][0];
		   cen[classification[i]][1] += a[i][1];
		   cen[classification[i]][2] += a[i][2];
		   cen[classification[i]][3] += a[i][3];
//		   System.out.println(a[i][0]+" "+a[i][2]+" "+a[i][2]+" "+a[i][3]+" "+classification[i]);
		   cnt[classification[i]]++;
		   ++i;
		}
		
//		for (int l = 0; l < k; ++l) {
//			cen[l][0] /=cnt[l];
//			cen[l][1] /=cnt[l];
//			cen[l][2] /=cnt[l];
//			cen[l][3] /=cnt[l];
//			System.out.println("(" + cen[l][0] + "," + cen[l][1] +","+cen[l][2]+","+cen[l][3]+ " "+cnt[l]+")");
//		}
		

		Random rnd = new Random(); 
		for (int l = 0; l < k; ++l) {
			cen[l][0] = rnd.nextDouble()*20;;
			cen[l][1] = rnd.nextDouble()*20;// * 5;
			cen[l][2] = rnd.nextDouble()*20; //* 7;
			cen[l][3] = rnd.nextDouble()*20;// * 3;
		}
	}

	public static Statistics getCount(int length) throws InterruptedException, FileNotFoundException {
		genreateData();

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

	static public void main(String args[]) throws InterruptedException, FileNotFoundException {
		genreateData();

		m = Mode.VERIFY;
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

				CompEnv<Boolean> env = CompEnv.getEnv(m, Party.Alice, is, os);
			    ArithmeticLib<Boolean> lib = new FixedPointLib<Boolean>(env, 32, 15);

				
				Boolean[][] sc = new Boolean[a.length][];
				for (int i = 0; i < sc.length; ++i) {
					Boolean[][] tmp = new Boolean[4][];
					for(int j = 0; j < 4; ++j){
						tmp[j] = lib.inputOfAlice(a[i][j]);
					}
					sc[i] = new Boolean[4 * lib.numBits()];
							
					System.arraycopy(tmp[0], 0, sc[i], 0, tmp[0].length);
					System.arraycopy(tmp[1], 0, sc[i], tmp[0].length, tmp[1].length);
					System.arraycopy(tmp[2], 0, sc[i], tmp[1].length*2, tmp[2].length);
					System.arraycopy(tmp[3], 0, sc[i], tmp[1].length*3, tmp[3].length);
				}

				Boolean[][] centers = new Boolean[k][];
				for (int i = 0; i < centers.length; ++i) {
					Boolean[][] tmp = new Boolean[4][];
					for(int j = 0; j < 4; ++j){
						tmp[j] = lib.inputOfAlice(cen[i][j]);
					}
					centers[i] = new Boolean[4 * lib.numBits()];
							
					System.arraycopy(tmp[0], 0, centers[i], 0, tmp[0].length);
					System.arraycopy(tmp[1], 0, centers[i], tmp[0].length, tmp[1].length);
					System.arraycopy(tmp[2], 0, centers[i], tmp[1].length*2, tmp[2].length);
					System.arraycopy(tmp[3], 0, centers[i], tmp[1].length*3, tmp[3].length);
				}

				UCIIris<Boolean> wc = new UCIIris<Boolean>(lib, centers);
				KeyValue<Boolean>[] res = null;
				for (int iter = 0; iter < max_iter; ++iter) {
					Boolean[] cntRes = null;
					res = wc.MapReduce(sc, cntRes, k);
					
					for (int i = 0; i < k; ++i) {
						Boolean[] x = Arrays.copyOfRange(res[i].value, 0,
								lib.numBits());
						Boolean[] y = Arrays.copyOfRange(res[i].value,
								lib.numBits(), lib.numBits() * 2);
						Boolean[] z = Arrays.copyOfRange(res[i].value,
								lib.numBits()*2, lib.numBits() * 3);
						Boolean[] v = Arrays.copyOfRange(res[i].value,
								lib.numBits()*3, lib.numBits() * 4);
						Boolean[] cnt = Arrays.copyOfRange(res[i].value,
								lib.numBits() * 4, lib.numBits() * 5);
						Boolean[] avex = lib.div(x, cnt);
						Boolean[] avey = lib.div(y, cnt);
						Boolean[] avez = lib.div(z, cnt);
						Boolean[] avev = lib.div(v, cnt);
						System.arraycopy(avex, 0, wc.clusters[i], 0,
								lib.numBits());
						System.arraycopy(avey, 0, wc.clusters[i],
								lib.numBits(), lib.numBits());
						System.arraycopy(avez, 0, wc.clusters[i],
								lib.numBits()*2, lib.numBits());
						System.arraycopy(avev, 0, wc.clusters[i],
								lib.numBits()*3, lib.numBits());
						Double a = lib.outputToAlice(avex);
						Double b = lib.outputToAlice(avey);
						Double c = lib.outputToAlice(avez);
						Double d = lib.outputToAlice(avev);
						System.out.println("(" + a + "," + b +","+c+","+d+";"+lib.outputToAlice(cnt)+")");
					}
					System.out.println("\n");
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

				CompEnv<Boolean> env = CompEnv.getEnv(m, Party.Bob, is, os);
			    ArithmeticLib<Boolean> lib = new FixedPointLib<Boolean>(env, 32, 15);

				
				Boolean[][] sc = new Boolean[a.length][];
				for (int i = 0; i < sc.length; ++i) {
					Boolean[][] tmp = new Boolean[4][];
					for(int j = 0; j < 4; ++j){
						tmp[j] = lib.inputOfAlice(a[i][j]);
					}
					sc[i] = new Boolean[4 * lib.numBits()];
							
					System.arraycopy(tmp[0], 0, sc[i], 0, tmp[0].length);
					System.arraycopy(tmp[1], 0, sc[i], tmp[0].length, tmp[1].length);
					System.arraycopy(tmp[2], 0, sc[i], tmp[1].length*2, tmp[2].length);
					System.arraycopy(tmp[3], 0, sc[i], tmp[1].length*3, tmp[3].length);
				}

				Boolean[][] centers = new Boolean[k][];
				for (int i = 0; i < centers.length; ++i) {
					Boolean[][] tmp = new Boolean[4][];
					for(int j = 0; j < 4; ++j){
						tmp[j] = lib.inputOfAlice(cen[i][j]);
					}
					centers[i] = new Boolean[4 * lib.numBits()];
							
					System.arraycopy(tmp[0], 0, centers[i], 0, tmp[0].length);
					System.arraycopy(tmp[1], 0, centers[i], tmp[0].length, tmp[1].length);
					System.arraycopy(tmp[2], 0, centers[i], tmp[1].length*2, tmp[2].length);
					System.arraycopy(tmp[3], 0, centers[i], tmp[1].length*3, tmp[3].length);
				}

				UCIIris<Boolean> wc = new UCIIris<Boolean>(lib, centers);
				KeyValue<Boolean>[] res = null;
				for (int iter = 0; iter < max_iter; ++iter) {
					Boolean[] cntRes = null;
					res = wc.MapReduce(sc, cntRes, k);
					for (int i = 0; i < k; ++i) {
						Boolean[] x = Arrays.copyOfRange(res[i].value, 0,
								lib.numBits());
						Boolean[] y = Arrays.copyOfRange(res[i].value,
								lib.numBits(), lib.numBits() * 2);
						Boolean[] z = Arrays.copyOfRange(res[i].value,
								lib.numBits()*2, lib.numBits() * 3);
						Boolean[] v = Arrays.copyOfRange(res[i].value,
								lib.numBits()*3, lib.numBits() * 4);
						Boolean[] cnt = Arrays.copyOfRange(res[i].value,
								lib.numBits() * 4, lib.numBits() * 5);
						Boolean[] avex = lib.div(x, cnt);
						Boolean[] avey = lib.div(y, cnt);
						Boolean[] avez = lib.div(z, cnt);
						Boolean[] avev = lib.div(v, cnt);
						System.arraycopy(avex, 0, wc.clusters[i], 0,
								lib.numBits());
						System.arraycopy(avey, 0, wc.clusters[i],
								lib.numBits(), lib.numBits());
						System.arraycopy(avez, 0, wc.clusters[i],
								lib.numBits()*2, lib.numBits());
						System.arraycopy(avev, 0, wc.clusters[i],
								lib.numBits()*3, lib.numBits());
						Double a = lib.outputToAlice(avex);
						Double b = lib.outputToAlice(avey);
						Double c = lib.outputToAlice(avez);
						Double d = lib.outputToAlice(avev);
						lib.outputToAlice(cnt);
//						System.out.println("(" + a + "," + b +","+c+","+d+")");
					}
//					System.out.println(" ");
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
