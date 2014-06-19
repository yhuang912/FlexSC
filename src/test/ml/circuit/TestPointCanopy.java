package test.ml.circuit;

import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import gc.GCSignal;
import ml.circuit.CanopyLib;
import ml.datastructure.Point;

import org.junit.Test;

import test.harness.Test_NinputMoutputPoint;

public class TestPointCanopy extends Test_NinputMoutputPoint<GCSignal> {

	@Test
	public void testAllCases() throws Exception {
		int testCases = 1;

		int b[][] = {{1,1}, {1,0}, {2,2}, {10,10}, {10,13}, {10, 13}, {10, 15}, {23, 45},{23, 41},{23, 42}};
		int[][] a = new int[1000][2];
		for(int i = 0; i < a.length/b.length; ++i)
		{
			int [][] c = new int[b.length][b[0].length];
			for(int j = 0; j < b.length; ++j)
				for(int k =0; k < 2; ++k)
					c[j][k] = b[j][k] + i;
			
			for(int j = 0; j < c.length; ++j){
				a[i*c.length+j] = c[j];
			}
		}
		for (int i = 0; i < testCases; i++) {
			System.out.println(a.length);
			Helper h = new Helper(a, Mode.REAL, 2 /* dimension */, 16 /* width */) {

				@Override
				public Point<GCSignal>[] secureCompute(Point<GCSignal>[] Signala,
						 CompEnv<GCSignal> e)
						throws Exception {
					return new CanopyLib<GCSignal>(e, 2, 16).map(Signala, 10, 15, 5);
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
