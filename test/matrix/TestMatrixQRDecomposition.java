package matrix;

//import gc.Boolean;
import harness.TestMatrix;

import org.junit.Test;

import circuits.arithmetic.DenseMatrixLib;

public class TestMatrixQRDecomposition extends TestMatrix<Boolean> {

	@Test
	public void testAllCases() throws Exception {
		for (int i = 0; i < 1; i++) {
			double[][] d1 = new double[][] { new double[] { 52, 30, 49, 28 },
					new double[] { 30, 50, 8, 44 },
					new double[] { 49, 8, 46, 16 },
					new double[] { 28, 44, 16, 22 } };// randomMatrix(10, 10);
			double[][] d2 = randomMatrix(10, 10);

			runThreads(new Helper(d1, d2) {

				@Override
				public Boolean[][][] secureCompute(Boolean[][][] a,
						Boolean[][][] b, DenseMatrixLib<Boolean> lib)
						throws Exception {
					return lib.QRDecomposition(a).getElement0();
				}

				@Override
				public double[][] plainCompute(double[][] a, double[][] b) {
					return DenseMatrixLib.rref(a);
				}
			});
		}
	}
}