package mapreduce;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import test.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import gc.GCEva;
import gc.GCGen;
import gc.GCSignal;

public class WordCount<T> extends MapReduceBackEnd<T>{

	IntegerLib<T> lib;
	public WordCount(CompEnv<T> env) {
		super(env);
		lib = new IntegerLib<T>(env);
	}
	
	@Override
	public KeyValue map(T[] inputs) throws Exception {
		return new KeyValue(inputs, lib.toSignals(1, 10));
	}

	@Override
	public T[] reduce(T[] value1, T[] value2) throws Exception {
		return lib.add(value1, value2);
	}
	
	
	final static int length = 1000;
	
	@Test
	static public void main(String args[]) throws InterruptedException {
		
		Random rnd = new Random();
		int []a = new int[length];
		for(int i = 0; i < length; ++i)
			a[i] = rnd.nextInt(4);
		
		GenRunnable gen = new GenRunnable(a);
		EvaRunnable eva = new EvaRunnable();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();

		System.out.println(Arrays.toString(gen.z));		
	}
	
	static class GenRunnable extends network.Server implements Runnable {
		int[] z;
		int[] intB;
		GenRunnable (int[] b) {
			this.intB = b;
		}
		public void run() {
			try {
				listen(54321);

				GCGen gen = new GCGen(is, os);
				
				GCSignal[][] scb = new GCSignal[length][];
				for(int i = 0; i < scb.length; ++i)
					scb[i] = gen.inputOfAlice(Utils.fromInt(intB[i], 32));

				WordCount<GCSignal> wc = new WordCount<GCSignal>(gen);
				GCSignal[][] res = wc.MapReduce(scb);
				z = new int[res.length];
				for(int i = 0; i < res.length; ++i)
					z[i] = Utils.toInt(gen.outputToAlice(res[i]));
				
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	static class EvaRunnable extends network.Client implements Runnable {
		EvaRunnable () {
		}

		public void run() {
			try {
				connect("localhost", 54321);				

				GCEva eva = new GCEva(is, os);
				
				GCSignal[][] scb = new GCSignal[length][];
				for(int i = 0; i < scb.length; ++i)
					scb[i] = eva.inputOfAlice(new boolean[32]);

				WordCount<GCSignal> wc = new WordCount<GCSignal>(eva);
				GCSignal[][] res = wc.MapReduce(scb);
				for(int i = 0; i < res.length; ++i)
					eva.outputToAlice(res[i]);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
