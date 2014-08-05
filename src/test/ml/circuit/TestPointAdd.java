package test.ml.circuit;

import java.util.Random;

import ml.circuit.PointLib;
import ml.datastructure.Point;

import org.junit.Test;

import test.harness.Test_2input1outputPoint;
import flexsc.CompEnv;
import flexsc.Mode;

public class TestPointAdd extends Test_2input1outputPoint<Boolean> {

	@Test
	public void testAllCases() throws InterruptedException {
		Random rnd = new Random();
		int testCases = 1;

		for (int i = 0; i < testCases; i++) {
			int a[] = {3, 2};
			int b[] = {7, 4};
			Helper h = new Helper(a, b, Mode.VERIFY, 2 /* dimension */, 30 /* width */) {
				public Point<Boolean> secureCompute(Point<Boolean> signalA, Point<Boolean> signalB, CompEnv<Boolean> e) throws Exception {
					return new PointLib<Boolean>(e, 2, 30).add(signalA, signalB);}

				public int[] plainCompute(int[] x, int[] y) {
					for (int i = 0; i < x.length; i++) {
						x[i] = x[i] + y[i];
					}
					return x;
				}
			};
			runThreads(h);
		}		
	}
}
