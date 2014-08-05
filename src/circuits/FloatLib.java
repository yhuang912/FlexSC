package circuits;

import java.util.Arrays;

import objects.Float.Representation;
import flexsc.CompEnv;

public class FloatLib<T> extends IntegerLib<T> {

	public FloatLib(CompEnv<T> e) {
		super(e);
	}
	
	public Representation<T> one(Representation<T> a) {
		T[] v = zeros(a.v.length);
		v[v.length-1] = SIGNAL_ONE;
		
		T[] p = zeros(a.p.length);
		p = sub(p, toSignals(v.length-1, p.length));
		
		Representation<T> result = new Representation<T>(SIGNAL_ZERO, p, v, SIGNAL_ZERO);
		return result;
	}
	
	public Representation<T> zero(Representation<T> a) {
		T[] v = zeros(a.v.length);
		T[] p = zeros(a.p.length);
		
		Representation<T> result = new Representation<T>(SIGNAL_ZERO, p, v, SIGNAL_ONE);
		return result;
	}
	
	public Representation<T> zero(int lengthV, int lengthP) {
		T[] v = zeros(lengthV);
		T[] p = zeros(lengthP);
		Representation<T> result = new Representation<T>(SIGNAL_ZERO, p, v, SIGNAL_ONE);
		return result;
	}	
	
	public Representation<T> multiply(Representation<T> a, Representation<T> b) {
		assert(a.compatiable(b)) :"floats not compatible";
		
		T new_z = or(a.z, b.z);
		T new_s = mux(xor(a.s, b.s),SIGNAL_ONE,new_z);
		T[] a_multi_b = unSignedMultiply(a.v, b.v);//length 2*v.length
		T[] a_add_b = add(a.p, b.p);
				
		T toShift = not(a_multi_b[a_multi_b.length-1]);
		T[] Shifted = conditionalLeftPublicShift(a_multi_b, 1, toShift);
		
		T[] new_v = Arrays.copyOfRange(Shifted, a.v.length, a.v.length*2);
		T[] new_p = add(a_add_b, toSignals(a.v.length, a_add_b.length));
		
		T[] decrement = zeros(new_p.length);
		decrement[0] = toShift;
		new_p = sub(new_p, decrement);
		return new Representation<T>(new_s, new_p, new_v, new_z);
	}
	
	public Representation<T> divide(Representation<T> a, Representation<T> b) {
		assert(a.compatiable(b)) :"floats not compatible";
		
		T new_z = a.z;
		T new_s = mux(xor(a.s, b.s), SIGNAL_ONE, a.z);
		int length = a.v.length;
		int newLength = 3*a.v.length;
		T[] padded_av = padSignal(a.v, newLength);
		T[] padded_bv = padSignal(b.v, newLength);
		T[] shifted_av = leftPublicShift(padded_av, newLength-length-1);
		T[] new_a_p = sub(a.p, toSignals(newLength-length-1, a.p.length));
		T[] a_div_b = divide(shifted_av, padded_bv);//length 2*NewLength
		T[] a_sub_b = sub(new_a_p, b.p);
		
		T[] leadingzero = leadingZeros(a_div_b);
		T[] ShiftAmount = sub( toSignals(2*newLength-length), padSignal(leadingzero,32));
		T[] normalized_av = mux(rightPrivateShift(a_div_b, ShiftAmount), 
		leftPrivateShift(a_div_b, twosComplement(ShiftAmount)), ShiftAmount[ShiftAmount.length-1] );

		T[] new_v = Arrays.copyOfRange(normalized_av, 0, length);
		T[] new_p = add(a_sub_b, ShiftAmount);

		return new Representation<T>(new_s, new_p, new_v, new_z);
	}
	
	
	public Representation<T> publicFloat(double d, int lengthV, int lengthP) {
		FloatFormat f = new FloatFormat(d, lengthV, lengthP);
		T s = f.s? SIGNAL_ONE : SIGNAL_ZERO;
		T z = f.z? SIGNAL_ONE : SIGNAL_ZERO;
		T[] v = env.newTArray(lengthV);
		T[] p = env.newTArray(lengthP);
		for(int i = 0; i < lengthV; ++i)
			v[i] = f.v[i] ? SIGNAL_ONE : SIGNAL_ZERO;
		for(int i = 0; i < lengthP; ++i)
			p[i] = f.p[i] ? SIGNAL_ONE : SIGNAL_ZERO;
		return new Representation<T>(s, p, v, z);
	}
	
	/* does not handle the case like 100000001 + 10000000001. fix later.
	 * -> fix is newlengthV : 2->3.
	 * */
	public Representation<T> add(Representation<T> a, Representation<T> b) {
		assert(a.compatiable(b)) :"floats not compatible";
		
		int lengthV = a.v.length;
		int newLengthV = 3*a.v.length;
		T[] diffab = sub(a.p, b.p);
		T[] diffba = twosComplement(diffab);//sub(b.p, a.p);
		T aPGreater = diffba[diffba.length-1];
		
		
		//make v signed
		T[] signed_av = padSignal(a.v, newLengthV);
		signed_av = mux(signed_av, twosComplement(signed_av), a.s);
		T[] signed_bv = padSignal(b.v, newLengthV);
		signed_bv = mux(signed_bv, twosComplement(signed_bv), b.s);
				
		//shift them to have same p
		T[] shifted_v =  leftPrivateShift(mux(signed_bv, signed_av, aPGreater), mux(diffba, diffab, aPGreater));
		
		// add v's
		T[] new_v = add(shifted_v, mux(signed_av, signed_bv, aPGreater));
		
		//change back to unsigned ver
		T resultNeg = new_v[new_v.length-1];
		new_v = mux(new_v, twosComplement(new_v), resultNeg);
		
		//get new p, which is the smaller of the two
		T[] new_p = Arrays.copyOf(mux(a.p, b.p, aPGreater), b.p.length);
		
		
		//now do normalize
		T[] leadingzero = leadingZeros(new_v);
		T[] ShiftAmount = sub( toSignals(newLengthV-lengthV), padSignal(leadingzero,32));
		T[] normalized_av = mux( rightPrivateShift(new_v, ShiftAmount), 
				leftPrivateShift(new_v, twosComplement(ShiftAmount)), ShiftAmount[ShiftAmount.length-1] );

		new_v = Arrays.copyOfRange(normalized_av, 0, lengthV);
		new_p = add(new_p, ShiftAmount);
		
		//
		//Signal[] absDiff = mux(diffba, diffab, aPGreater);
		//Signal outBound = geq(absDiff, getPublicSignal(lengthV, absDiff.length));
		//Signal[] result_v = mux(mux(a.v, b.v, aPGreater),outBound);
		Representation<T> result = new Representation<T>(resultNeg, new_p, new_v, eq(new_v, zeros(lengthV)));
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
	
	public Representation<T> sub(Representation<T> a, Representation<T> b) {
		assert(a.compatiable(b)) :"floats not compatible";
		Representation<T> negB = b.clone();
		negB.s = not(negB.s);
		return add(a, negB);
	}
	
	public Representation<T> mux(Representation<T> a, Representation<T> b, T s) {
		return new Representation<T>(mux(a.s, b.s, s), 
						mux(a.p, b.p, s), 
						mux(a.v, b.v, s), 
						mux(a.z, b.z, s));
	}
	
	public Representation<T> xor(Representation<T> a, Representation<T> b){
		T[] v = xor(a.v, b.v);
		T[] p = xor(a.p, b.p);
		T z= xor(a.z, b.z);
		T s= xor(a.s, b.s);
		return new Representation<T>(s, p, v, z);
	}
}
