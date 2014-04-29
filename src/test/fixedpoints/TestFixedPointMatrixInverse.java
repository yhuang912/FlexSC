package test.fixedpoints;

import java.util.Random;

import flexsc.*;
//import gc.Boolean;
import org.junit.Test;
import test.harness.TestFixedPoint;
import test.harness.TestFixedPointMatrix;
import circuits.FixedPointLib;
import circuits.FixedPointMatrixLib;
import circuits.FloatMatrixLib;


public class TestFixedPointMatrixInverse extends TestFixedPointMatrix<Boolean> {

	@Test
	public void testAllCases() throws Exception {
		Random rng = new Random();
		int testCases = 10;

		for (int i = 0; i < testCases; i++) {
			double[][] d1 = new double[5][5];
			for(int k = 0; k < d1.length; ++k)
				for(int j = 0; j < d1[i].length; ++j)
					d1[k][j] = rng.nextInt(1<<30)%1000000.0/100000.0;
			double[][] d2 = new double[][]{new double[]{rng.nextInt(1<<30)%1000000.0/100000.0}};
			
			runThreads(new Helper(d1, d2, Mode.VERIFY) {

				@Override
				public Boolean[][][] secureCompute(Boolean[][][] a,
						Boolean[][][] b, int offset, CompEnv<Boolean> env)
						throws Exception {
					FixedPointMatrixLib<Boolean> lib = new FixedPointMatrixLib<Boolean>(env);
					return lib.fastInverse(a, offset);
				}

				@Override
				public double[][] plainCompute(double[][] a, double[][] b) {
					return FloatMatrixLib.rref(a);
				}
			});
		}
	}
}