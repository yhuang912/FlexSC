package test.others;

import gc.GCEva;
import gc.GCGen;
import gc.GCSignal;

import java.util.Random;

import test.Utils;
import circuits.IntegerLib;


public class TestSearch {
	
	public GCSignal[] search(IntegerLib<GCSignal> lib, GCSignal[] x, GCSignal[] y, int n) throws Exception{
		GCSignal[] l = lib.toSignals(0, 32);
		GCSignal[] r = lib.toSignals(n, 32);
		int i = n;
		while(i > 1) {
			//mid = (l + r) / 2;
			GCSignal[] mid = lib.add(l, r);
			mid = lib.rightPublicShift(mid, 1);
			//mid*mid+y <= x
			GCSignal[] mmm = lib.multiply(mid, mid);
			mmm = lib.add(mmm, y);
			GCSignal guard = lib.leq(mmm, x);
			
			//if statement
			l = lib.mux(l, mid, guard);
			r = lib.mux(r, mid, lib.not(guard));
			
			i = (i + 1) / 2;					
		}
	    GCSignal[] lml = lib.multiply(l, l);
	    lml = lib.add(lml, y);
	    GCSignal guard = lib.eq(lml, x);
	    
	    //if statement
	    GCSignal[] ret = lib.mux(lib.toSignals(0,32), l, guard);
		return ret;
	}

	class GenRunnable extends network.Server implements Runnable {
		boolean[] z;
		int inty;
		int n;
		GenRunnable (int n, int y) {
			this.inty =y;
			this.n= n;
		}

		
		public void run() {
			try {
				listen(54321);
				GCGen gen = new GCGen(is, os);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(gen);
				GCSignal[] y = gen.inputOfAlice(Utils.fromInt(inty, 32));
				GCSignal[] x = gen.inputOfBob(new boolean[32]);
				GCSignal[] ret = search(lib, x, y, n);
				os.flush();
				z = gen.outputToAlice(ret);
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	class EvaRunnable extends network.Client implements Runnable {
		int n, intx;
		EvaRunnable (int n, int x) {
			this.n = n;
			intx = x;
		}

		public void run() {
			try {
				connect("localhost", 54321);				

				GCEva eva = new GCEva(is, os);
				IntegerLib<GCSignal> lib = new IntegerLib<GCSignal>(eva);
				GCSignal[] y = eva.inputOfAlice(new boolean[32]);
				GCSignal[] x = eva.inputOfBob(Utils.fromInt(intx, 32));
				GCSignal[] ret = search(lib, x, y, n);
				eva.outputToAlice(ret);
				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void runThreads(int n, int x, int y) throws Exception {
		GenRunnable gen = new GenRunnable(n, y);
		EvaRunnable eva = new EvaRunnable(n, x);
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();

		System.out.println(search(n,y,x)+" "+Utils.toSignedInt(gen.z));
	}
	
	public static void main(String[] args) throws Exception
	{
		TestSearch test = new TestSearch();
		Random rnd = new Random();
		int n = rnd.nextInt(32), x = rnd.nextInt(32), y = rnd.nextInt(32);
		System.out.println(n+" "+y+" "+x);
		test.runThreads(n, x, y);
	}

	static public int search(int n,  int y,  int x) {
		int i;
		int l;
		int r;
		int mid;
		int ret;
	      i = n;
	      l = 0;
	      r = n;
	      while(i>1) {
	                  mid = (l + r) / 2;
	                  if(mid*mid+y <= x)
	                        l = mid;
	                  else
	                        r = mid;
	            i = (i + 1) / 2;
	      }
	      if(l*l+y == x)
	            ret = l;
	      else
	            ret = 0;
	      return ret;
	}

}