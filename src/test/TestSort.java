package test;

import flexsc.CompEnv;
import flexsc.Mode;
import gc.GCSignal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import test.harness.TestSortHarness;
import test.parallel.GraphNode;
import circuits.BitonicSortLib;
import circuits.TestComparator;

public class TestSort extends TestSortHarness<GCSignal> {
	@Test
	public void testAllCases() throws Exception {
		Random rnd = new Random();
		int testCases = 1;

		for (int i = 0; i < testCases; i++) {
			int [] a = new int[512];
			for(int j = 0; j < a.length; ++j) {
				a[j] = rnd.nextInt() % (1 << 30);
			}

			Helper helper = new Helper(a, Mode.REAL) {
				public GCSignal[][] secureCompute(GCSignal[][] Signala, final CompEnv<GCSignal> e) throws Exception {
					TestGraphNode<GCSignal>[] input = (TestGraphNode<GCSignal>[]) Array.newInstance(TestGraphNode.class, Signala.length);
					for (int i = 0; i < input.length; i++) {
						input[i] = new TestGraphNode<>(Signala[i], Signala[i], Signala[i][0]);
					}
					BitonicSortLib<GCSignal> lib =  new BitonicSortLib<GCSignal>(e, new TestComparator<>(e));
					lib.sort(input, lib.SIGNAL_ONE);
					for (int i = 0; i < input.length; i++) {
						Signala[i] = input[i].getU();
					}
					return Signala;
				}
				
				@Override
				public int[] plainCompute(int[] intA) {
					Arrays.sort(intA);
					return intA;
				}
			};
			runThreads(helper);
		}
	}

	public class TestGraphNode<T> extends GraphNode<T> {

		public TestGraphNode(T[] u, T[] v, T isVertex) {
			super(u, v, isVertex);
		}

		@Override
		public void send(OutputStream os, CompEnv<T> env) throws IOException {
			super.send(os, env);
		}

		@Override
		public void read(InputStream is, CompEnv<T> env) throws IOException {
			super.read(is, env);
		}

		@Override
		public GraphNode<T> mux(GraphNode<T> b, T condition, CompEnv<T> env) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public GraphNode<T> getCopy(CompEnv<T> env) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public T[] flatten(CompEnv<T> env) {
			T[] vert = env.newTArray(1);
			vert[0] = (T) isVertex;
			return Utils.flatten(env, u, v, vert);
		}

		@Override
		public void unflatten(T[] flat, CompEnv<T> env) {
			T[] vert = env.newTArray(1);
			Utils.unflatten(flat, u, v, vert);
			isVertex = vert[0];
		}

		public T[] getU() {
			return u;
		}
	}
}