package oramgc.pathoram;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import circuits.IntegerLib;
import flexsc.CompEnv;
import gc.GCEva;
import gc.GCGen;
import gc.GCSignal;
import test.Utils;


public class TestPathOramLib {
	int N = 16;
	public abstract class Helper {
		int[] intA, intB;
		boolean[][] a;
		boolean[][] b;
		public Helper(int[] aa, int [] bb) {
			intA = aa;
			intB = bb;
			a = new boolean[aa.length][6];
			for(int i = 0; i < aa.length; ++i)
				a[i] = Utils.fromInt(aa[i], 6);
			
			b = new boolean[bb.length][6];
			for(int i = 0; i < bb.length; ++i)
				b[i] = Utils.fromInt(bb[i], 6);
		}
		public abstract GCSignal[][] secureCompute(GCSignal[][] Signala, GCSignal[][] Signalb, CompEnv<GCSignal> e) throws Exception;
		public abstract int[] plainCompute(int[] x);
	}

	class GenRunnable extends network.Server implements Runnable {
		int[] z;
		Helper h;
		GenRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				listen(54321);

				GCGen gen = new GCGen(is, os);
				GCSignal [][] a = new GCSignal[16][];
				for(int i = 0; i < N; ++i)
					a[i] = gen.inputOfGen(h.a[i]);
				
				GCSignal [][] b = new GCSignal[16][];
				for(int i = 0; i < N; ++i)
					b[i] = gen.inputOfGen(h.b[i]);
				
				GCSignal[][] d = h.secureCompute(a, b, gen);
				os.flush();

				z = new int[d.length];
				for(int i = 0; i < d.length; ++i)
					z[i] = Utils.toInt(gen.outputToGen(d[i]));

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		Helper h;
		EvaRunnable (Helper h) {
			this.h = h;
		}

		public void run() {
			try {
				connect("localhost", 54321);				

				
				GCEva eva = new GCEva(is, os);
				GCSignal [][] a = new GCSignal[16][];
				for(int i = 0; i < N; ++i)
					a[i] = eva.inputOfGen(h.a[i]);
				
				GCSignal [][] b = new GCSignal[16][];
				for(int i = 0; i < N; ++i)
					b[i] = eva.inputOfGen(h.b[i]);

				GCSignal[][] d = h.secureCompute(a, b, eva);
				os.flush();

				for(int i = 0; i < d.length; ++i)
					eva.outputToGen(d[i]);
				
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void runThreads(Helper h) throws Exception {
		GenRunnable gen = new GenRunnable(h);
		EvaRunnable eva = new EvaRunnable(h);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(5);
		tEva.start();
		tGen.join();

		System.out.println(Arrays.toString(gen.z));
	}

	@Test
	public void test() throws Exception{
		Random rnd = new Random();
		int testCases = 1;

		int[] ina = new int[]{3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0,0};
		int[] inb = Arrays.copyOf(ina, ina.length);
		int c = 0; int[]b = new int[ina.length];
		b[0] = ina[0];
		int level = 4;
		for(int i = 0; i < ina.length; ++i) {
			if(c==4 || ina[i]<level) {
				level--;
				c=1;
				}
			else c++;
			level = level < ina[i] ? level: ina[i];
			level = level < 0 ? 0 : level;
			b[i] = level;
		}
		int[] bb = new int[ina.length];
		for(int i = 0; i < b.length;++i)
			bb[i] = b[i]*4;
		c=0;
		for(int i  = 1; i < ina.length; ++i) {
			if(b[i-1] == b[i]){
				++c;
			}
			else{
				c=0;
			}
			if(b[i] == 0)
				c=0;
			bb[i]+=c;
		}
		int[]bbb = new int[2*ina.length];
		for(int i = 0; i < ina.length; ++i){
			bbb[i] = bb[i];
			bbb[i+ina.length] = i;
		}
		Arrays.sort(bbb);
		
		
		System.out.println(Arrays.toString(ina));
		System.out.println(Arrays.toString(b));
		System.out.println(Arrays.toString(bb));
		System.out.println(Arrays.toString(bbb));
		System.out.println();
		for (int i = 0; i < testCases; i++) {
			runThreads(
				new Helper(ina, inb) {
					@Override
					public GCSignal[][] secureCompute(GCSignal[][] Signala, GCSignal[][] Signalb,
							CompEnv<GCSignal> e) throws Exception {
						PathOramLib lib = new PathOramLib(3, 3, 3, 4, e);
						return lib.pushDownHelp(Signala, Signalb);
					}

					@Override
					public int[] plainCompute(int[] x) {
						return null;
					}
				});
		}		
	}

}