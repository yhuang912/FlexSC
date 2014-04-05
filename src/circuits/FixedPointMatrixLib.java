package circuits;

import flexsc.CompEnv;
import gc.Signal;

public class FixedPointMatrixLib extends FixedPointLib {

	public FixedPointMatrixLib(CompEnv<Signal> e) {
		super(e);
	}
	
	public Signal[][][] add(Signal[][][] a, Signal[][][] b, int offset) throws Exception {
		int n = a.length;
		int m = a[0].length;
		int l = a[0][0].length;
		Signal[][][] result = new Signal[n][m][l];
		for(int i = 0; i < n; ++i)
			for(int j = 0; j < m; ++j)
				result[i][j] = add(a[i][j], b[i][j], offset);
		return result;
	}
	
	public Signal[][][] multiply(Signal[][][] a, Signal[][][] b, int offset) throws Exception {
		int n = a.length;
		int m = a[0].length;
		int l = b[0].length;
		Signal[][][]result = new Signal[n][m][l];
		for(int i = 0; i < n; ++i)
			for(int j = 0; j < l; ++l) {
				result[i][j] = multiply(a[i][0], b[0][l], offset);
				for(int k = 1; j < m; ++k)
					result[i][j] = add(result[i][j], multiply(a[i][k], b[k][l]));
			}
		return result;		
	}
	
	public Signal[][][] transpose(Signal[][][] a){
		int n = a.length;
		int m = a[0].length;
		Signal[][][] result = new Signal[n][m][a[0][0].length];
		for(int i = 0; i < n; ++i)
			for(int j = 0; j < m; ++j)
				result[i][j] = a[j][i];
		return result;
	}
	
	public Signal[][][] xor(Signal[][][] a, Signal[][][] b){
		int n = a.length;
		int m = a[0].length;
		Signal[][][]result = new Signal[n][m][a[0][0].length];
		for(int i = 0; i < a.length; ++i)
			for(int j = 0; j < a[i].length; ++j)
				result[i][j] = xor(a[i][j], b[i][j]);
		return result;
	}
	

	public Signal[][][] fastInverse(Signal[][][] m, int offset) throws Exception {
		int dimension = m.length;
		int width = m[0][0].length;
		Signal[][][] extended = new Signal[dimension][2*dimension][width];
		Signal[] zeroFloat = publicFixPoint(0, width, offset);
		Signal[] oneFloat = publicFixPoint(1, width, offset);
		for(int i = 0 ; i < dimension; ++i){
			for(int j = 0; j < dimension; ++j)
				extended[i][j] = m[i][j];
			for(int j = 0; j < dimension; ++j)
				extended[i][dimension+j] = zeroFloat;
			extended[i][dimension+i] = oneFloat;
		}
		extended = rref(extended, offset);
		Signal[][][] result = new Signal[dimension][dimension][width];
		for(int i = 0 ; i < dimension; ++i) {
			for(int j = 0; j < dimension; ++j)
				result[i][j] = extended[i][dimension+j];
		}
		return result;
	}
	
	public Signal[][] Solve(Signal[][][] A, Signal[][]b, int offset) throws Exception {
		int dimension = A.length;
		int width = A[0][0].length;
		Signal[][][] extended = new Signal[dimension][1+dimension][width];
		for(int i = 0 ; i < dimension; ++i){
			for(int j = 0; j < dimension; ++j)
				extended[i][j] = A[i][j];
			extended[i][dimension] = b[i];
		}
		extended = rref(extended, offset);
		Signal[][] result = new Signal[dimension][width];
		for(int i = 0 ; i < dimension; ++i) {
				result[i] = extended[i][dimension];
		}
		return result;
	}
	
	public Signal[][][] rref(Signal[][][] m, int offset) throws Exception {
		Signal[][][] result = new Signal[m.length][m[0].length][m[0][0].length];
		for (int r = 0; r < m.length; ++r)
	        for (int c = 0; c < m[r].length; ++c)
	            result[r][c] = m[r][c];
		
	    for (int p = 0; p <  result.length; ++p)
	    {
	        /* Make this pivot 1 */
	    	Signal[] pv = result[p][p];
	    	Signal pvZero = eq(pv, zeros(pv.length));
	    	Signal[] pvInv = divide(publicFixPoint(1.0, pv.length, offset), pv, offset);
	        for (int i = 0; i < result[p].length; ++i)
	        	result[p][i] = mux(multiply(result[p][i], pvInv, offset),result[p][i], pvZero);

	        /* Make other rows zero */
	        for (int r = 0; r < result.length; ++r)
	        {
	            if (r != p)
	            {
	            	Signal[] f = result[r][p];
	                for (int i = 0; i < result[r].length; ++i)
	                {
	                	result[r][i] = sub(result[r][i], multiply(f, result[p][i]));
	                }
	            }
	        }
	    }
	    
		return result;
	}

}
