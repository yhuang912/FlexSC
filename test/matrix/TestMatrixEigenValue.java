package matrix;

import gc.GCSignal;
import harness.TestMatrix;

import org.junit.Test;

import circuits.arithmetic.DenseMatrixLib;

public class TestMatrixEigenValue extends TestMatrix<GCSignal> {

	@Test
	public void testAllCases() throws Exception {
		for (int i = 0; i < 1; i++) {
			double[][] d1 = randomMatrix(10, 10);
			for (int k = 0; k < 10; k++)
				for (int l = 0; l < 10; ++l)
					d1[k][l] = d1[l][k];

			double[][] d2 = randomMatrix(10, 10);

			runThreads(new Helper(d1, d2) {

				@Override
				public GCSignal[][][] secureCompute(GCSignal[][][] a,
						GCSignal[][][] b, DenseMatrixLib<GCSignal> lib)
						throws Exception {
					return lib.eigenValues(a, 10);
				}

				@Override
				public double[][] plainCompute(double[][] a, double[][] b) {
					return DenseMatrixLib.rref(a);
				}
			});
		}
	}
}