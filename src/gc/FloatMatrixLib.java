package gc;

import objects.Float.GCFloat;

public class FloatMatrixLib extends FloatLib {

	public FloatMatrixLib(CompEnv<Signal> e) {
		super(e);
	}
	
	public GCFloat[][] add(GCFloat[][] a, GCFloat[][] b) throws Exception {
		int n = a.length;
		int m = a[0].length;
		GCFloat result[][] = new GCFloat[n][m];
		for(int i = 0; i < n; ++i)
			for(int j = 0; j < m; ++j)
				result[i][j] = add(a[i][j], b[i][j]);
		return result;
	}
	
	public GCFloat[][] multiply(GCFloat[][] a, GCFloat[][] b) throws Exception {
		int n = a.length;
		int m = a[0].length;
		int l = b[0].length;
		GCFloat result[][] = new GCFloat[n][m];
		for(int i = 0; i < n; ++i)
			for(int j = 0; j < l; ++l) {
				result[i][j] = multiply(a[i][0], b[0][l]);
				for(int k = 1; j < m; ++k)
					result[i][j] = add(result[i][j], multiply(a[i][k], b[k][l]));
			}
		return result;		
	}
	
	public GCFloat[][] transpose(GCFloat[][] a){
		int n = a.length;
		int m = a[0].length;
		GCFloat result[][] = new GCFloat[n][m];
		for(int i = 0; i < n; ++i)
			for(int j = 0; j < m; ++j)
				result[i][j] = a[j][i];
		return result;
	}
	
	public GCFloat determinant(GCFloat[][] a) throws Exception{
		if(a.length == 1)
			return a[0][0];
		if(a.length == 2){
			return sub(multiply(a[0][0], a[1][1]), multiply(a[0][1], a[1][0]));
		}
		else {
			GCFloat result = sub(a[0][0], a[0][0]);
			for (int i = 0; i < a[0].length; ++i) {
				GCFloat tmp = determinant(createSubMatrix(a, 0, i));
				tmp = multiply(a[0][i], tmp);
				Signal t = (i % 2 == 1) ? SIGNAL_ONE : SIGNAL_ZERO;
				tmp.s = xor(t, tmp.s);
		        result = add(result, tmp);
		    }
		    return result;
		}
	}
	
	public GCFloat[][] createSubMatrix(GCFloat[][] a, int row, int col) throws Exception {
		int n = a.length;
		int m = a[0].length;
		int r = -1;
		GCFloat result[][] = new GCFloat[n-1][m-1];
		for(int i = 0; i < n; ++i){
			if(i == row)continue;
			
			++r;
			int c = -1;
			for(int j = 0; j < m; ++j) {
				if(j == col)continue;
				result[r][++c] = a[i][j];
			}
		}

		return result;
	}
			
	public GCFloat[][] inverse(GCFloat[][] a) throws Exception {
		GCFloat[][] result = transpose(cofactor(a));
		
		GCFloat c = determinant(a);
		GCFloat cInv = divide(getPublicFloat(1.0, c.v.length, c.p.length), c);
		for(int i = 0; i < a.length; ++i)
			for(int j = 0; j < a[i].length; ++j)
				result[i][j] = multiply(result[i][j], cInv);
		return result;
	}
	
	public GCFloat[][] cofactor(GCFloat[][] a) throws Exception  {
		int n = a.length;
		int m = a[0].length;
		GCFloat[][] result = new GCFloat[n][m];
		for(int i = 0; i < n; ++i)
			for(int j = 0; j < m; ++j){
				GCFloat tmp = determinant(createSubMatrix(a, i, j));
				Signal t = ((j+i) % 2 == 1) ?  SIGNAL_ONE : SIGNAL_ZERO;
				tmp.s = xor(tmp.s, t);
				result[i][j] = tmp; 
			}
		return result;
	}
	
	public GCFloat[][] fastInverse(GCFloat[][] m) throws Exception {
		int dimension = m.length;
		GCFloat[][] extended = new GCFloat[dimension][2*dimension];
		GCFloat zeroFloat = getPublicFloat(0, m[0][0].v.length, m[0][0].p.length);
		GCFloat oneFloat = getPublicFloat(1, m[0][0].v.length, m[0][0].p.length);
		for(int i = 0 ; i < dimension; ++i){
			for(int j = 0; j < dimension; ++j)
				extended[i][j] = m[i][j];
			for(int j = 0; j < dimension; ++j)
				extended[i][dimension+j] = zeroFloat;
			extended[i][dimension+i] = oneFloat;
		}
		extended = rref(extended);
		GCFloat[][] result = new GCFloat[dimension][dimension];
		for(int i = 0 ; i < dimension; ++i) {
			for(int j = 0; j < dimension; ++j)
				result[i][j] = extended[i][dimension+j];
		}
		return result;
	}
	
	public GCFloat[][] rref(GCFloat[][] m) throws Exception {
		GCFloat[][] result = new GCFloat[m.length][m[0].length];
		for (int r = 0; r < m.length; ++r)
	        for (int c = 0; c < m[r].length; ++c)
	            result[r][c] = m[r][c];
		
	    for (int p = 0; p <  result.length; ++p)
	    {
	        /* Make this pivot 1 */
	        GCFloat pv = result[p][p];
	        GCFloat pvInv = divide(getPublicFloat(1.0, pv.v.length, pv.p.length), pv);
	        for (int i = 0; i < result[p].length; ++i)
	        	result[p][i] = mux(multiply(result[p][i], pvInv),result[p][i],pv.z);

	        /* Make other rows zero */
	        for (int r = 0; r < result.length; ++r)
	        {
	            if (r != p)
	            {
	                GCFloat f = result[r][p];
	                for (int i = 0; i < result[r].length; ++i)
	                {
	                	result[r][i] = sub(result[r][i], multiply(f, result[p][i]));
	                }
	            }
	        }
	    }
	    
		return result;
	}
	
	public static double[][] rref(double[][] mat)
	{
	    double[][] rref = new double[mat.length][mat[0].length];

	    /* Copy matrix */
	    for (int r = 0; r < rref.length; ++r)
	    {
	        for (int c = 0; c < rref[r].length; ++c)
	        {
	            rref[r][c] = mat[r][c];
	        }
	    }

	    for (int p = 0; p < 1/*rref.length*/; ++p)
	    {
	        /* Make this pivot 1 */
	        double pv = rref[p][p];
	        if (pv != 0)
	        {
	            double pvInv = 1.0 / pv;
	            for (int i = 0; i < rref[p].length; ++i)
	            {
	                rref[p][i] *= pvInv;
	            }
	        }

	        /* Make other rows zero */
	        for (int r = 0; r < rref.length; ++r)
	        {
	            if (r != p)
	            {
	                double f = rref[r][p];
	                for (int i = 0; i < rref[r].length; ++i)
	                {
	                    rref[r][i] -= f * rref[p][i];
	                }
	            }
	        }
	    }

	    return rref;
	}

}
