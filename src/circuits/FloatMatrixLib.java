package circuits;

import flexsc.CompEnv;
import objects.Float.Representation;

public class FloatMatrixLib<T> extends FloatLib<T> {

	public FloatMatrixLib(CompEnv<T> e) {
		super(e);
	}
	
	@SuppressWarnings("unchecked")
	public Representation<T>[] representationArray(int len) {
		return new Representation[len];
	}
	
	@SuppressWarnings("unchecked")
	public Representation<T>[][] representationMatrix(int len1, int len2) {
		return new Representation[len1][len2];
	}
	
	public Representation<T>[][] add(Representation<T>[][] a, Representation<T>[][] b) throws Exception {
		int n = a.length;
		int m = a[0].length;
		Representation<T> result[][] = representationMatrix(n, m);//new Representation[n][m];
		for(int i = 0; i < n; ++i)
			for(int j = 0; j < m; ++j)
				result[i][j] = add(a[i][j], b[i][j]);
		return result;
	}
	
	public Representation<T>[][] multiply(Representation<T>[][] a, Representation<T>[][] b) throws Exception {
		int n = a.length;
		int m = a[0].length;
		int l = b[0].length;
		Representation<T> result[][] = representationMatrix(n, m);
		for(int i = 0; i < n; ++i)
			for(int j = 0; j < l; ++l) {
				result[i][j] = multiply(a[i][0], b[0][l]);
				for(int k = 1; j < m; ++k)
					result[i][j] = add(result[i][j], multiply(a[i][k], b[k][l]));
			}
		return result;		
	}
	
	public Representation<T>[][] transpose(Representation<T>[][] a){
		int n = a.length;
		int m = a[0].length;
		Representation<T> result[][] = representationMatrix(n, m);
		for(int i = 0; i < n; ++i)
			for(int j = 0; j < m; ++j)
				result[i][j] = a[j][i];
		return result;
	}
	
	public Representation<T>[][] xor(Representation<T>[][] a, Representation<T>[][] b){
		int n = a.length;
		int m = a[0].length;
		Representation<T> result[][] = representationMatrix(n, m);
		for(int i = 0; i < a.length; ++i)
			for(int j = 0; j < a[i].length; ++j)
				result[i][j] = xor(a[i][j], b[i][j]);
		return result;
	}
	
	public Representation<T> determinant(Representation<T>[][] a) throws Exception{
		if(a.length == 1)
			return a[0][0];
		if(a.length == 2){
			return sub(multiply(a[0][0], a[1][1]), multiply(a[0][1], a[1][0]));
		}
		else {
			Representation<T> result = sub(a[0][0], a[0][0]);
			for (int i = 0; i < a[0].length; ++i) {
				Representation<T> tmp = determinant(createSubMatrix(a, 0, i));
				tmp = multiply(a[0][i], tmp);
				T t = (i % 2 == 1) ? SIGNAL_ONE : SIGNAL_ZERO;
				tmp.s = xor(t, tmp.s);
		        result = add(result, tmp);
		    }
		    return result;
		}
	}
	
	public Representation<T>[][] createSubMatrix(Representation<T>[][] a, int row, int col) throws Exception {
		int n = a.length;
		int m = a[0].length;
		int r = -1;
		Representation<T> result[][] = representationMatrix(n, m);
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
			
	public Representation<T>[][] inverse(Representation<T>[][] a) throws Exception {
		Representation<T>[][] result = transpose(cofactor(a));
		
		Representation<T> c = determinant(a);
		Representation<T> cInv = divide(publicFloat(1.0, c.v.length, c.p.length), c);
		for(int i = 0; i < a.length; ++i)
			for(int j = 0; j < a[i].length; ++j)
				result[i][j] = multiply(result[i][j], cInv);
		return result;
	}
	
	public Representation<T>[][] cofactor(Representation<T>[][] a) throws Exception  {
		int n = a.length;
		int m = a[0].length;
		Representation<T> result[][] = representationMatrix(n, m);
		for(int i = 0; i < n; ++i)
			for(int j = 0; j < m; ++j){
				Representation<T> tmp = determinant(createSubMatrix(a, i, j));
				T t = ((j+i) % 2 == 1) ?  SIGNAL_ONE : SIGNAL_ZERO;
				tmp.s = xor(tmp.s, t);
				result[i][j] = tmp; 
			}
		return result;
	}
	
	public Representation<T>[][] fastInverse(Representation<T>[][] m) throws Exception {
		int dimension = m.length;
		Representation<T>[][] extended = representationMatrix(dimension, 2*dimension);
		Representation<T> zeroFloat = publicFloat(0, m[0][0].v.length, m[0][0].p.length);
		Representation<T> oneFloat = publicFloat(1, m[0][0].v.length, m[0][0].p.length);
		for(int i = 0 ; i < dimension; ++i){
			for(int j = 0; j < dimension; ++j)
				extended[i][j] = m[i][j];
			for(int j = 0; j < dimension; ++j)
				extended[i][dimension+j] = zeroFloat;
			extended[i][dimension+i] = oneFloat;
		}
		extended = rref(extended);
		Representation<T>[][] result = representationMatrix(dimension, dimension);
		for(int i = 0 ; i < dimension; ++i) {
			for(int j = 0; j < dimension; ++j)
				result[i][j] = extended[i][dimension+j];
		}
		return result;
	}
	
	public Representation<T>[][] rref(Representation<T>[][] m) throws Exception {
		Representation<T>[][] result = representationMatrix(m.length, m[0].length);
		for (int r = 0; r < m.length; ++r)
	        for (int c = 0; c < m[r].length; ++c)
	            result[r][c] = m[r][c];
		
	    for (int p = 0; p <  result.length; ++p)
	    {
	        /* Make this pivot 1 */
	        Representation<T> pv = result[p][p];
	        Representation<T> pvInv = divide(publicFloat(1.0, pv.v.length, pv.p.length), pv);
	        for (int i = 0; i < result[p].length; ++i)
	        	result[p][i] = mux(multiply(result[p][i], pvInv),result[p][i],pv.z);

	        /* Make other rows zero */
	        for (int r = 0; r < result.length; ++r)
	        {
	            if (r != p)
	            {
	                Representation<T> f = result[r][p];
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
