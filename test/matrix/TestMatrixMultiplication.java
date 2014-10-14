package matrix;

//import gc.Boolean;
import harness.TestMatrix;

import org.junit.Test;

import circuits.arithmetic.DenseMatrixLib;

public class TestMatrixMultiplication extends TestMatrix<Boolean> {

	@Test
	public void testAllCases() throws Exception {

		for (int i = 0; i < 1; i++) {
			double[][] d1 = randomMatrix(100, 100);
			double[][] d2 = randomMatrix(100, 100);

			runThreads(new Helper(d1, d2) {

				@Override
				public Boolean[][][] secureCompute(Boolean[][][] a,
						Boolean[][][] b, DenseMatrixLib<Boolean> lib)
						throws Exception {
					return lib.multiply(a, b);
				}

				@Override
				public double[][] plainCompute(double[][] a, double[][] b) {
					double[][] res = new double[a.length][b[0].length];
					for (int i = 0; i < a.length; ++i)
						for (int j = 0; j < b[0].length; ++j) {
							res[i][j] = 0;
							for (int k = 0; k < a[0].length; ++k)
								res[i][j] += (a[i][k] * b[k][j]);
						}
					return res;
				}
			});
		}
	}
}