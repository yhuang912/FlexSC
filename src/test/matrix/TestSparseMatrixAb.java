package test.matrix;

//import gc.Boolean;
import org.junit.Test;

import test.harness.TestSparseMatrix;
import circuits.sparse_matrix.MatrixNode;
import circuits.sparse_matrix.SparseMatrixLib;

public class TestSparseMatrixAb extends TestSparseMatrix<Boolean> {

	@Test
	public void testAllCases() throws Exception {
		for (int i = 0; i < 1; i++) {
			double[][] d1 = randomMatrix(5, 5, 1);
			double[][] d2 = randomMatrix(5, 1, 1);
			for(int k = 0; k < 5; ++k)
				d2[k][0] = k;

			runThreads(new Helper(d1, d2) {
				@Override
				public MatrixNode<Boolean>[] secureCompute(MatrixNode<Boolean>[] a,
						MatrixNode<Boolean>[] b, SparseMatrixLib<Boolean> lib)
						throws Exception {
					return lib.matrix_vector_multiplication(a, b);
				}

				@Override
				public double[][] plainCompute(double[][] a, double[][] b) {
//					double[][] res = new double[a.length][a[0].length];
//					for (int i = 0; i < a.length; ++i)
//						for (int j = 0; j < b.length; ++j)
//							res[i][j] = a[i][j] * b[i][j];
					return a;
				}
			});
		}
	}
}