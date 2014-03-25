package circuits;

import flexsc.CompEnv;
import gc.Signal;

import java.util.Arrays;

public class IntegerLib extends CircuitLib {

	public IntegerLib(CompEnv<Signal> e) {
		super(e);
	}

	static final int S = 0;
	static final int COUT = 1;
	protected Signal[] add(Signal x, Signal y, Signal cin) throws Exception {
		Signal[] res = new Signal[2];

		Signal t1 = env.xor(x, cin);
		Signal t2 = env.xor(y, cin);
		res[S] = env.xor(x, t2);
		t1 = env.and(t1, t2);
		res[COUT] = env.xor(cin, t1);

		return res;
	}

	Signal[] add(Signal[] x, Signal[] y, boolean cin) throws Exception {
		assert(x != null && y != null && x.length == y.length) : "add: bad inputs.";
		Signal[] res = new Signal[x.length];

		Signal[] t = add(x[0], y[0], new Signal(cin));
		res[0] = t[S];
		for (int i = 0; i < x.length-1; i++) {
			t = add(x[i+1], y[i+1], t[COUT]);
			res[i+1] = t[S];
		}

		return res;
	}

	//tested
	public Signal[] add(Signal[] x, Signal[] y) throws Exception {

		return add(x, y, false);
	}

	//tested
	public Signal[] sub(Signal[] x, Signal[] y) throws Exception {
		assert(x != null && y != null && x.length == y.length) : "sub: bad inputs.";

		return add(x, not(y), true);
	}

	//tested
	public Signal[] incrementByOne(Signal[] x) throws Exception {
		Signal[] one = zeros(x.length);
		one[0] = SIGNAL_ONE;
		return add(x, one);
	}

	//tested
	public Signal[] decrementByOne(Signal[] x) throws Exception {
		Signal[] one = zeros(x.length);
		one[0] = SIGNAL_ONE;
		return sub(x, one);
	}

	//tested
	public Signal[] conditionalIncreament(Signal[] x, Signal flag) throws Exception {
		Signal[] one = zeros(x.length);
		one[0] = mux(SIGNAL_ZERO, SIGNAL_ONE, flag);
		return add(x, one);
	}

	//tested
	public Signal[] conditionalDecrement(Signal[] x, Signal flag) throws Exception {
		Signal[] one = zeros(x.length);
		one[0] = mux(SIGNAL_ZERO, SIGNAL_ONE, flag);
		return sub(x, one);
	}

	//tested
	public Signal geq(Signal[] x, Signal[] y) throws Exception {
		assert(x.length == y.length) : "bad input";

		Signal[] result = sub(x, y);
		return not(result[result.length-1]);
	}
	
	//tested
	public Signal leq(Signal[] x, Signal[] y) throws Exception {
		return geq(y, x);
	}

	//tested
	public Signal[] multiply(Signal[] x, Signal[] y) throws Exception {
		assert(x != null && y!= null) : "multiply: bad inputs";	

		Signal[] res = zeros(x.length+y.length);
		Signal[] zero = zeros(res.length);
		Signal longerX[] = zeros(res.length);
		System.arraycopy(x, 0, longerX, 0, x.length);

		for(int i = 0; i < y.length; ++i) {
			res = add(res, mux(zero, longerX, y[i]));
			longerX = leftShift(longerX);
		}
		return res;
	}

	//tested
	public Signal[] absolute(Signal[] x) throws Exception {
		Signal reachedOneSignal = SIGNAL_ZERO;
		Signal[] result = zeros(x.length);
		for(int i = 0; i < x.length; ++i) {
			Signal comp = eq(SIGNAL_ONE, x[i]);
			result[i] = xor(x[i], reachedOneSignal);
			reachedOneSignal = or(reachedOneSignal, comp);
		}
		return mux(x, result, x[x.length-1]);
	}

	
	//tested
	public Signal[] divide(Signal[] x, Signal[] y) throws Exception {
		Signal[] absoluteX = absolute(x);
		Signal[] dividend = zeros(x.length + y.length);
		System.arraycopy(absoluteX, 0, dividend, 0, absoluteX.length);
		Signal[] absoluteY = absolute(y);
		Signal[] divisor = zeros(x.length + y.length);
		System.arraycopy(absoluteY, 0, divisor, x.length, absoluteY.length);

		Signal[] quotient = zeros(dividend.length);
		Signal[] zero = zeros(dividend.length);
		for(int i = 0; i < x.length+1; ++i) {
			quotient = leftShift(quotient);

			Signal divisorIsLEQ = leq(divisor, dividend);
			Signal[] temp = mux(zero, divisor, divisorIsLEQ);
			dividend = sub(dividend, temp);
			quotient[0] = divisorIsLEQ;

			divisor = rightShift(divisor);
		}
		return addSign(quotient, xor(x[x.length-1], y[y.length-1]));
	}

	
	//tested
	public Signal[] reminder(Signal[] x, Signal[] y) throws Exception {
		Signal[] absoluteX = absolute(x);
		Signal[] dividend = zeros(x.length + y.length);
		System.arraycopy(absoluteX, 0, dividend, 0, absoluteX.length);
		Signal[] absoluteY = absolute(y);
		Signal[] divisor = zeros(x.length + y.length);
		System.arraycopy(absoluteY, 0, divisor, x.length, absoluteY.length);

		Signal[] zero = zeros(dividend.length);
		for(int i = 0; i < x.length+1; ++i) {
			Signal divisorIsLEQ = leq(divisor, dividend);
			Signal[] temp = mux(zero, divisor, divisorIsLEQ);
			dividend = sub(dividend, temp);	
			divisor = rightShift(divisor);
		}

		return dividend;
	}

	public Signal[] addSign(Signal[] x, Signal sign) throws Exception {
		Signal reachedOneSignal = SIGNAL_ZERO;
		Signal[] result = new Signal[x.length];
		for(int i = x.length-1; i >=0; --i) {
			Signal comp = eq(SIGNAL_ONE, x[i]);
			result[i] = xor(x[i], reachedOneSignal);
			reachedOneSignal = or(reachedOneSignal, comp);
		}
		return mux(x, result, sign);
	}

	final static Signal[][] B = {
		toSignals(0x55555555),
		toSignals(0x33333333),
		toSignals(0x0F0F0F0F),
		toSignals(0x00FF00FF),
		toSignals(0x0000FFFF)
	};

	//tested
	public Signal[] commonPrefix(Signal[] x, Signal[] y) throws Exception {
		assert(x != null && y!= null) : "multiply: bad inputs";
		Signal[] result = xor(x, y);

		for(int i = x.length-2; i>=0; --i) {
			result[i] = or(result[i], result[i+1]);
		}
		return result;
	}

	//tested
	public Signal[] leadingZeros(Signal[] x) throws Exception {
		assert(x!= null) : "leading zeros: bad inputs";

		Signal[] result = Arrays.copyOf(x, x.length);
		for(int i = result.length-2; i>=0; --i) {
			result[i] = or(result[i], result[i+1]);
		}	

		return numberOfOnes(not(result));
	}

	public Signal[] numberOfOnes(Signal[] x) throws Exception {
		int length = ((x.length+31)/32) * 32;

		Signal[] vec = zeros(length);
		for(int i = 0; i < x.length; ++i)
			vec[i] = x[i];

		Signal[] result = zeros(32);
		for(int i = 0; i < length; i+=32) {
			result = add(result, numberOfOnes32(Arrays.copyOfRange(vec, i, i+32)));
		}

		return result;

	}

	public Signal[] numberOfOnes32(Signal[] x) throws Exception {
		assert(x!= null): "numberOfOnes : bad input";
		assert(x.length == 32) : "numberOfOnes : input should be of length 32";

		Signal[] c = sub(x, and(rightShift(x), B[0]));//c = v - ((v >> 1) & B[0]);
		c = add(and(rightPublicShift(c, (1<<1) ), B[1]), and(c, B[1]));//c = ((c >> S[1]) & B[1]) + (c & B[1]);
		c = and(add(rightPublicShift(c, (1<<2) ), c), B[2]);//c = ((c >> S[2]) + c) & B[2];
		c = and(add(rightPublicShift(c, (1<<3) ), c), B[3]);//c = ((c >> S[3]) + c) & B[3];
		Signal[] result = and(add(rightPublicShift(c, (1<<4) ), c), B[4]);//c = ((c >> S[4]) + c) & B[4];

		return result; 

	}

	//tested
	public Signal[] lengthOfCommenPrefix(Signal[] x, Signal [] y) throws Exception {
		assert(x!= null) : "lengthOfCommenPrefix : bad inputs";

		return leadingZeros(xor(x, y));
	}
	

	/* Integer manipulation
	 * */
	public Signal[] leftShift(Signal[] x){
		assert(x!= null) : "leftShift: bad inputs";
		return leftPublicShift(x, 1);
	}

	public Signal[] rightShift(Signal[] x){
		assert(x!= null) : "rightShift: bad inputs";
		return rightPublicShift(x, 1);
	}

	//tested
	public Signal[] leftPublicShift(Signal[] x, int s) {
		assert(x!= null && s < x.length) : "leftshift: bad inputs";

		Signal res[] = new Signal[x.length];
		System.arraycopy(zeros(s), 0, res, 0, s);
		System.arraycopy(x, 0, res, s, x.length-s);

		return res;
	}

	//tested
	public Signal[] rightPublicShift(Signal[] x, int s) {
		assert(x!= null && s < x.length) : "leftshift: bad inputs";

		Signal res[] = new Signal[x.length];
		System.arraycopy(x, s, res, 0, x.length-s);
		System.arraycopy(zeros(s), 0, res, x.length-s, s);//assume that this function is operated on 32bit word

		return res;
	}

	//tested
	public Signal[] conditionalLeftPublicShift(Signal[] x, int s, Signal sign) throws Exception {
		assert(x!= null && s < x.length) : "leftshift: bad inputs";

		Signal res[] = new Signal[x.length];
		System.arraycopy(mux(Arrays.copyOfRange(x, 0, s), zeros(s), sign), 0, res, 0, s);
		//System.arraycopy(sign, s, res, s, s);
		System.arraycopy(mux(Arrays.copyOfRange(x, s, x.length), Arrays.copyOfRange(x, 0, x.length)
				, sign), 0, res, s, x.length-s);
		return res;
	}

	//tested
	public Signal[] conditionalRightPublicShift(Signal[] x, int s, Signal sign) throws Exception {
		assert(x!= null && s < x.length) : "rightshift: bad inputs";

		Signal res[] = new Signal[x.length];
		System.arraycopy(mux(Arrays.copyOfRange(x, 0, x.length-s), Arrays.copyOfRange(x, s, x.length), sign), 0, res, 0, x.length-s);
		System.arraycopy(mux(Arrays.copyOfRange(x, x.length-s, x.length), zeros(s), sign), 0, res, x.length-s, s);
		return res;
	}


	//tested
	public Signal[] leftPrivateShift(Signal[] x, Signal[] lengthToShift) throws Exception {
		Signal[] res = Arrays.copyOf(x, x.length);

		for(int i = 0; ((1<<i) < x.length) && i < lengthToShift.length; ++i)
			res = conditionalLeftPublicShift(res, (1<<i), lengthToShift[i]);
		Signal clear = SIGNAL_ZERO;
		for(int i = 0; i < lengthToShift.length; ++i) {
			if((1<<i) >= x.length)
				clear = or(clear, lengthToShift[i]);
		}

		return mux(res, zeros(x.length), clear);
	}

	//tested
	public Signal[] rightPrivateShift(Signal[] x, Signal[] lengthToShift) throws Exception {
		Signal[] res = Arrays.copyOf(x, x.length);

		for(int i = 0; ((1<<i) < x.length) && i < lengthToShift.length; ++i)
			res = conditionalRightPublicShift(res, (1<<i), lengthToShift[i]);
		Signal clear = SIGNAL_ZERO;
		for(int i = 0; i < lengthToShift.length; ++i) {
			if((1<<i) >= x.length)
				clear = or(clear, lengthToShift[i]);
		}

		return mux(res, zeros(x.length), clear);
	}
	
	Signal compare(Signal x, Signal y, Signal cin) throws Exception {
		Signal t1 = xor(x, cin);
		Signal t2 = xor(y, cin);
		t1 = and(t1, t2);
		return xor(x, t1);
	}

	public Signal compare(Signal[] x, Signal[] y) throws Exception {
		assert(x != null && y != null && x.length == y.length) : "compare: bad inputs.";

		Signal t = new Signal(false);
		for (int i = 0; i < x.length; i++) {
			t = compare(x[i], y[i], t);
		}

		return t;
	}

	protected Signal eq(Signal x, Signal y) {
		assert(x != null && y!= null) : "CircuitLib.eq: bad inputs";

		return not(xor(x, y));
	}

	//tested
	public Signal eq(Signal[] x, Signal[] y) throws Exception {
		assert(x != null && y != null && x.length == y.length) : "CircuitLib.eq[]: bad inputs.";

		Signal res = new Signal(true);
		for (int i = 0; i < x.length; i++) {
			Signal t = eq(x[i], y[i]);
			res = env.and(res, t);
		}

		return res;
	}
	
	public Signal[] twosComplement(Signal[] x) throws Exception {
		Signal reachOne = SIGNAL_ZERO;
		Signal[] result = new Signal[x.length];
		for(int i = 0; i < x.length; ++i) {
			result[i] = xor(x[i], reachOne);
			reachOne = or(reachOne, x[i]);
		}
		return result;
	}

}
