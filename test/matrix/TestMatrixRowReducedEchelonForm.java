package matrix;

//import gc.Boolean;
import harness.TestMatrix;

import org.junit.Test;

import circuits.arithmetic.DenseMatrixLib;

public class TestMatrixRowReducedEchelonForm extends TestMatrix<Boolean> {

	@Test
	public void testAllCases() throws Exception {
		for (int i = 0; i < 1; i++) {
			double[][] d1 = randomMatrix(10, 10);
			double[][] d2 = randomMatrix(10, 10);

			runThreads(new Helper(d1, d2) {

				@Override
				public Boolean[][][] secureCompute(Boolean[][][] a,
						Boolean[][][] b, DenseMatrixLib<Boolean> lib)
						throws Exception {
					return lib.rref(a);
				}

				@Override
				public double[][] plainCompute(double[][] a, double[][] b) {
					return DenseMatrixLib.rref(a);
				}
			});
		}
	}
}