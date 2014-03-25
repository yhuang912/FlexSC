package gc;

import java.util.Arrays;

import objects.Float.GCFloat;
import gc.*;

public class FloatLib extends IntegerLib {

	public FloatLib(CompEnv<Signal> e) {
		super(e);
	}
	
	private Signal[] padSignal(Signal[] a, int length) {
		Signal[] res = zeroSignal(length);
		for(int i = 0; i < a.length; ++i)
			res[i] = a[i];
		return res;
	}
	
	public GCFloat makeFloatOne(GCFloat a) throws Exception {
		Signal[] v = zeroSignal(a.v.length);
		v[v.length-1] = SIGNAL_ONE;
		
		Signal[] p = zeroSignal(a.p.length);
		p = sub(p, getPublicSignal(v.length-1, p.length));
		
		GCFloat result = new GCFloat(SIGNAL_ZERO, p, v, SIGNAL_ZERO);
		return result;
	}
	
	public GCFloat makeFloatZero(GCFloat a) throws Exception {
		Signal[] v = zeroSignal(a.v.length);
		Signal[] p = zeroSignal(a.p.length);
		
		GCFloat result = new GCFloat(SIGNAL_ZERO, p, v, SIGNAL_ONE);
		return result;
	}
	
	public GCFloat multiply(GCFloat a, GCFloat b) throws Exception {
		assert(a.compatiable(b)) :"floats not compatible";
		
		Signal new_z = or(a.z, b.z);
		Signal new_s = mux(xor(a.s, b.s),SIGNAL_ONE,new_z);
		Signal[] a_multi_b = multiply(a.v, b.v);//length 2*v.length
		Signal[] a_add_b = add(a.p, b.p);
				
		Signal toShift = not(a_multi_b[a_multi_b.length-1]);
		Signal[] Shifted = conditionalLeftPublicShift(a_multi_b, 1, toShift);
		
		Signal[] new_v = Arrays.copyOfRange(Shifted, a.v.length, a.v.length*2);
		Signal[] new_p = add(a_add_b, getPublicSignal(a.v.length, a_add_b.length));
		
		Signal[] decrement = zeroSignal(new_p.length);
		decrement[0] = toShift;
		new_p = sub(new_p, decrement);
		return new GCFloat(new_s, new_p, new_v, new_z);
	}
	
	public GCFloat divide(GCFloat a, GCFloat b) throws Exception {
		assert(a.compatiable(b)) :"floats not compatible";
		
		Signal new_z = a.z;
		Signal new_s = mux(xor(a.s, b.s), SIGNAL_ONE, a.z);
		int length = a.v.length;
		int newLength = 3*a.v.length;
		Signal[] padded_av = padSignal(a.v, newLength);
		Signal[] padded_bv = padSignal(b.v, newLength);
		Signal[] shifted_av = leftPublicShift(padded_av, newLength-length-1);
		Signal[] new_a_p = sub(a.p, getPublicSignal(newLength-length-1, a.p.length));
		Signal[] a_div_b = divide(shifted_av, padded_bv);//length 2*NewLength
		Signal[] a_sub_b = sub(new_a_p, b.p);
		
		Signal[] leadingzero = leadingZeros(a_div_b);
		Signal[] ShiftAmount = sub( getPublicSignal(2*newLength-length), leadingzero);
		Signal[] normalized_av = mux( rightPrivateShift(a_div_b, ShiftAmount), 
		leftPrivateShift(a_div_b, TwoComplement(ShiftAmount)), ShiftAmount[ShiftAmount.length-1] );

		Signal[] new_v = Arrays.copyOfRange(normalized_av, 0, length);
		Signal[] new_p = add(a_sub_b, ShiftAmount);

		return new GCFloat(new_s, new_p, new_v, new_z);
	}
	
	public Signal[] TwoComplement(Signal[] x) throws Exception {
		Signal reachOne = SIGNAL_ZERO;
		Signal[] result = new Signal[x.length];
		for(int i = 0; i < x.length; ++i) {
			result[i] = xor(x[i], reachOne);
			reachOne = or(reachOne, x[i]);
		}
		return result;
	}
	
	public GCFloat getPublicFloat(double d, int lengthV, int lengthP) {
		FloatFormat f = new FloatFormat(d, lengthV, lengthP);
		Signal s = f.s? SIGNAL_ONE : SIGNAL_ZERO;
		Signal z = f.z? SIGNAL_ONE : SIGNAL_ZERO;
		Signal[] v = new Signal[lengthV];
		Signal[] p = new Signal[lengthP];
		for(int i = 0; i < lengthV; ++i)
			v[i] = f.v[i] ? SIGNAL_ONE : SIGNAL_ZERO;
		for(int i = 0; i < lengthP; ++i)
			p[i] = f.p[i] ? SIGNAL_ONE : SIGNAL_ZERO;
		return new GCFloat(s, p, v, z);
	}
	
	/* does not handle the case like 100000001 + 10000000001. fix later.
	 * -> fix is newlengthV : 2->3.
	 * */
	public GCFloat add(GCFloat a, GCFloat b) throws Exception {
		assert(a.compatiable(b)) :"floats not compatible";
		
		int lengthV = a.v.length;
		int newLengthV = 3*a.v.length;
		Signal[] diffab = sub(a.p, b.p);
		Signal[] diffba = TwoComplement(diffab);//sub(b.p, a.p);
		Signal aPGreater = diffba[diffba.length-1];
		
		
		//make v signed
		Signal[] signed_av = padSignal(a.v, newLengthV);
		signed_av = mux(signed_av, TwoComplement(signed_av), a.s);
		Signal[] signed_bv = padSignal(b.v, newLengthV);
		signed_bv = mux(signed_bv, TwoComplement(signed_bv), b.s);
				
		//shift them to have same p
		Signal[] shifted_v =  leftPrivateShift(mux(signed_bv, signed_av, aPGreater), mux(diffba, diffab, aPGreater));
		
		// add v's
		Signal[] new_v = add(shifted_v, mux(signed_av, signed_bv, aPGreater));
		
		//change back to unsigned ver
		Signal resultNeg = new_v[new_v.length-1];
		new_v = mux(new_v, TwoComplement(new_v), resultNeg);
		
		//get new p, which is the smaller of the two
		Signal[] new_p = Arrays.copyOf(mux(a.p, b.p, aPGreater), b.p.length);
		
		
		//now do normalize
		Signal[] leadingzero = leadingZeros(new_v);
		Signal[] ShiftAmount = sub( getPublicSignal(newLengthV-lengthV), leadingzero);
		Signal[] normalized_av = mux( rightPrivateShift(new_v, ShiftAmount), 
				leftPrivateShift(new_v, TwoComplement(ShiftAmount)), ShiftAmount[ShiftAmount.length-1] );

		new_v = Arrays.copyOfRange(normalized_av, 0, lengthV);
		new_p = add(new_p, ShiftAmount);
		
		//
		//Signal[] absDiff = mux(diffba, diffab, aPGreater);
		//Signal outBound = geq(absDiff, getPublicSignal(lengthV, absDiff.length));
		//Signal[] result_v = mux(mux(a.v, b.v, aPGreater),outBound);
		GCFloat result = new GCFloat(resultNeg, new_p, new_v, eq(new_v, zeroSignal(lengthV)));
		return mux(mux(result,a,b.z),b,a.z);
	}
	
//	public GCFloat sqrt(GCFloat a) {
//		//http://www.codeproject.com/Articles/69941/Best-Square-Root-Method-Algorithm-Function-Precisi
//		//http://en.wikipedia.org/wiki/Methods_of_computing_square_roots#Babylonian_method
//		//GCFloat zeroFloat = getPublicFloat(0, a.v.length, a.p.length);
//		/*   float sqrt5(const float m)
//		   {
//		      float i=0;
//		      float x1,x2;
//		      while( (i*i) <= m )
//		             i+=0.1f;
//		      x1=i;
//		      for(int j=0;j<10;j++)
//		      {
//		          x2=m;
//		         x2/=x1;
//		         x2+=x1;
//		         x2/=2;
//		         x1=x2;
//		      }
//		      return x2;
//		   }   */ 
//		return null;
//	}
	
	public GCFloat sub(GCFloat a, GCFloat b) throws Exception {
		assert(a.compatiable(b)) :"floats not compatible";
		GCFloat negB = b.clone();
		negB.s = not(negB.s);
		return add(a, negB);
	}
	
	public GCFloat mux(GCFloat a, GCFloat b, Signal s) throws Exception{
		return new GCFloat(mux(a.s, b.s, s), 
						mux(a.p, b.p, s), 
						mux(a.v, b.v, s), 
						mux(a.z, b.z, s));
	}
}
