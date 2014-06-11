package test.ml.circuit;

import java.util.Arrays;

import ml.circuit.CanopyLib;
import ml.datastructure.Point;

import org.junit.Test;

import test.harness.Test_NinputMoutputPoint;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import gc.GCSignal;

public class TestPointCanopy extends Test_NinputMoutputPoint<GCSignal> {

	@Test
	public void testAllCases() throws Exception {
		int testCases = 1;

		int a[][] = {
				{1,1}, {1,0}, {2,2}, {10,10}, {10,13}, {10, 13}, {10, 15}, {23, 45},
				{1,1}, {1,0}, {2,2}, {10,10}, {10,13}, {10, 13}, {10, 15}, {23, 45},
				{1,1}, {1,0}, {2,2}, {10,10}, {10,13}, {10, 13}, {10, 15}, {23, 45},
				{1,1}, {1,0}, {2,2}, {10,10}, {10,13}, {10, 13}, {10, 15}, {23, 45},
				{1,1}, {1,0}, {2,2}, {10,10}, {10,13}, {10, 13}, {10, 15}, {23, 45},
				{1,1}, {1,0}, {2,2}, {10,10}, {10,13}, {10, 13}, {10, 15}, {23, 45},
				{1,1}, {1,0}, {2,2}, {10,10}, {10,13}, {10, 13}, {10, 15}, {23, 45},
				{1,1}, {1,0}, {2,2}, {10,10}, {10,13}, {10, 13}, {10, 15}, {23, 45},
				{1,1}, {1,0}, {2,2}, {10,10}, {10,13}, {10, 13}, {10, 15}, {23, 45},
				{1,1}, {1,0}, {2,2}, {10,10}, {10,13}, {10, 13}, {10, 15}, {23, 45}
				};
		for (int i = 1; i < 80; i+=10) {

			Helper h = new Helper(Arrays.copyOf(a, i), Mode.REAL, 2 /* dimension */, 16 /* width */) {

				@Override
				public Point<GCSignal>[] secureCompute(Point<GCSignal>[] Signala,
						 CompEnv<GCSignal> e)
						throws Exception {
					return new CanopyLib<GCSignal>(e, 2, 16).map(Signala, 4, 15, 5);
				}

				@Override
				public int[][] plainCompute(int[][] x) {
					// TODO Auto-generated method stub
					return null;
				}
			};
			runThreads(h);
			Flag.sw.ands = 0;
		}		
	}
}
