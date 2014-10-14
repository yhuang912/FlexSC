package matrix;

//import gc.Boolean;
import harness.TestMatrix;

import org.junit.Test;

import circuits.arithmetic.DenseMatrixLib;

public class TestMatrixAdd extends TestMatrix<Boolean> {

	@Test
	public void testAllCases() throws Exception {
		for (int i = 0; i < 1; i++) {
			double[][] d1 = randomMatrix(500, 500);
			double[][] d2 = randomMatrix(500, 500);

			runThreads(new Helper(d1, d2) {
				@Override
				public Boolean[][][] secureCompute(Boolean[][][] a,
						Boolean[][][] b, DenseMatrixLib<Boolean> lib)
						throws Exception {
					return lib.add(a, b);
				}

				@Override
				public double[][] plainCompute(double[][] a, double[][] b) {
					double[][] res = new double[a.length][a[0].length];
					for (int i = 0; i < a.length; ++i)
						for (int j = 0; j < b.length; ++j)
							res[i][j] = a[i][j] + b[i][j];
					return res;
				}
			});
		}
	}
}