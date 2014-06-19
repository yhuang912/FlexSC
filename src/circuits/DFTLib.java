package circuits;

// Reference: https://www.ee.columbia.edu/~ronw/code/MEAPsoft/doc/html/FFT_8java-source.html


import objects.Float.Representation;
import flexsc.CompEnv;

public class DFTLib<T> extends FloatLib<T> {

	public DFTLib(CompEnv<T> e) {
		super(e);
	}
	
	public void InverseFDFT(Representation<T>[]x, Representation<T>[] y) throws Exception {
		FFT(x,y);
		Representation<T> invN = divide(publicFloat(1, x[0].v.length, x[0].p.length),
				publicFloat(x.length, x[0].v.length, x[0].p.length)
				);
		for(int i = 0; i < x.length; ++i)
			x[i] = multiply(invN, x[i]);
		
		for(int i = 0; i < y.length; ++i)
			y[i] = multiply(invN, y[i]);
	}
	
	public void FFT(Representation<T>[] x, Representation<T>[] y) throws Exception {
		int lengthV = x[0].v.length, lengthP = x[0].p.length;;
		int n = x.length;
		int m = (int)(Math.log(n) / Math.log(2));
		int n1;

		// Bit-reverse
		int j = 0;
		int n2 = n/2;
		for (int i = 1; i < n - 1; i++) {
			n1 = n2;
			while ( j >= n1 ) {
				j = j - n1;
				n1 = n1/2;
			}
			j = j + n1;

			if (i < j) {
				Representation<T> t1 = x[i];
				x[i] = x[j];
				x[j] = t1;
				t1 = y[i];
				y[i] = y[j];
				y[j] = t1;
			}
		}

		// FFT
		n1 = 0;
		n2 = 1;

		for (int i=0; i < m; i++) {
			n1 = n2;
			n2 = n2 + n2;
			int a = 0;

			for (j=0; j < n1; j++) {
				Representation<T> c = publicFloat(Math.cos(-2*Math.PI*a/n), lengthV, lengthP);
				Representation<T> s = publicFloat(Math.sin(-2*Math.PI*a/n), lengthV, lengthP);
				a +=  1 << (m-i-1);

				for (int k = j; k < n; k = k + n2) {
					Representation<T> t1 = sub(multiply(c,x[k+n1]), multiply(s, y[k+n1]));
					Representation<T> t2 = add(multiply(s,x[k+n1]), multiply(c, y[k+n1]));
					
					x[k+n1] = sub(x[k], t1);
					y[k+n1] = sub(y[k], t2);
					x[k] = add(x[k], t1);
					y[k] = add(y[k], t2);
				}
			}
		}
		
	}
}
