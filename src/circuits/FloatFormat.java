package circuits;

import java.math.BigDecimal;

import test.Utils;

public class FloatFormat {
	public boolean[] v;
	public boolean[] p;
	public boolean s;
	public boolean z;
	
	/*
	 * s: sign bit;
	 * z: isZero (if z==1, then the value of the float is 0);
	 * 
	 * The value of the floating point is v*2^{p}*(1-z)*s, where p is a signed integer while v unsigned.
	 */
	public FloatFormat(boolean[] v, boolean[] p, boolean s, boolean z) {
		this.v = v;	
		this.p = p;
		this.s = s;
		this.z = z;
	}
	
	/*
	 * Transform a constant double number to its binary representation (not secret shared yet).
	 */
	public FloatFormat(double d, int widthV, int widthP) {
		z = Math.abs(d - 0) < 0.00001;
		
		v = new boolean[widthV];
		p = new boolean[widthP];
		s = d < 0;
		if ( z ) {
		//	   d = 0.000001;
			for(int i  = 0; i < widthV; ++i)
				v[i] = false;
			v[widthV-1]=true;
			for(int i  = 0; i < widthP; ++i)
				p[i] = false;
			p[widthP-1]=true;
			return;
		}
		d = s ? -1*d:d;
		int pInt = 0;
		
		double lower_bound = Math.pow(2, widthV-1);
		double upper_bound = Math.pow(2, widthV);
		while(d < lower_bound) {
			d*=2;
			pInt--;
		}
		
		while(d >= upper_bound) {
			d/=2;
			pInt++;
		}
		
		p = Utils.fromInt(pInt, widthP);
		long tmp = (long) (d+0.000001);//a hack...
		v = Utils.fromLong(tmp, widthV);
	}
	
	public String toString(){
		String res = "";
		for(int i = 0; i < v.length; ++i)
			res = res + (v[i]? "1":"0");
		
		res += "\t";
		for(int i = 0; i < p.length; ++i)
			res = res + (p[i]? "1":"0");
		res += "\t";
		res  = res + (s ? "1":"0");
		res  = res + (z ? "1":"0");
		res += "\t";
		res  = res + toDouble();
		return res;
	}
	
	public double toDouble() {
		return toDouble(this);
	}
	
	public static double toDouble(FloatFormat a) {
		return toDouble(a.v, a.p, a.s, a.z);
	}
	
	private static double toDouble(boolean[] v, boolean[] p, boolean s, boolean z) {
		if(z)
			return 0;
		double result = s ? -1 : 1;
		long value_v = Utils.toUnSignedInt(v);
		long value_p = Utils.toSignedInt(p);
		result = result * value_v;
		result = result * Math.pow(2, value_p);
		//int res = result * 100000;
		BigDecimal b = new BigDecimal(result);
		return b.setScale(v.length/10*3, BigDecimal.ROUND_HALF_UP).doubleValue(); // 6 is should not be fixed.
	}
}
