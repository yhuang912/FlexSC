package matrix;

//import gc.Boolean;
import harness.TestSparseMatrix;

import org.junit.Test;

import circuits.sparse_matrix.MatrixNode;
import circuits.sparse_matrix.SparseMatrixLib;

public class TestSparseMatrixAdd extends TestSparseMatrix<Boolean> {

//	@Test
	public void testAllCases() throws Exception {
		for (int i = 0; i < 1; i++) {
			double[][] d1 = randomMatrix(500, 500, 0.002);
			double[][] d2 = randomMatrix(500, 500, 0.002);

			runThreads(new Helper(d1, d2, 1) {
				@Override
				public MatrixNode<Boolean>[] secureCompute(
						MatrixNode<Boolean>[] a, MatrixNode<Boolean>[] b,
						SparseMatrixLib<Boolean> lib) throws Exception {
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
	
	@Test
	public void test() throws Exception{
		for(int i = 9 ; i < 17; ++i) {
			xlimit = 3*1<<i;
			ylimit = 3*1<<i;
			M = 3*1<<i;
//			double[][] d1 = randomMatrix(1<<i, 1<<i, 0.000);
//			double[][] d2 = randomMatrix(1<<i, 1<<i, 0.000);

			runThreads(new Helper(null, null, 1) {
				@Override
				public MatrixNode<Boolean>[] secureCompute(
						MatrixNode<Boolean>[] a, MatrixNode<Boolean>[] b,
						SparseMatrixLib<Boolean> lib) throws Exception {
					return lib.add(a, b);
				}

				@Override
				public double[][] plainCompute(double[][] a, double[][] b) {
//					double[][] res = new double[a.length][a[0].length];
//					for (int i = 0; i < a.length; ++i)
//						for (int j = 0; j < b.length; ++j)
//							res[i][j] = a[i][j] + b[i][j];
					return a;
				}
			});

		}
	}
}