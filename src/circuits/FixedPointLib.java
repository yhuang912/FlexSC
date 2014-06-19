package circuits;

import java.util.Arrays;

import test.Utils;
import flexsc.CompEnv;

public class FixedPointLib<T> extends IntegerLib<T> {

	public FixedPointLib(CompEnv<T> e) {
		super(e);
	}
	
	public T[] add(T[] x, T[] y, int offset) throws Exception {
		return add(x, y);
	}
	
	public T[] sub(T[] x, T[] y, int offset) throws Exception{
		return sub(x, y);
	}
	
	public T[] multiply(T[] x, T[] y, int offset) throws Exception{
		T[] res = unSignedMultiply(x, y);
		return Arrays.copyOfRange(res, offset, offset+x.length);
	}
	
	public T[] divide(T[] x, T[] y, int offset) throws Exception {
		int newLength = x.length*2;
		T[] padX = padSignal(x, newLength);
		padX = leftPublicShift(padX, newLength-x.length);
		T[] res = divide(padX, padSignal(y, newLength));
		T[] shiftRes = rightPublicShift(res, newLength-x.length-offset);
		return padSignal(shiftRes, x.length);
		
	}
	
	public T[] publicFixPoint(double d, int width, int offset) {
		boolean[] a = Utils.fromFixPoint(d, width, offset);
		T[] res = env.newTArray(width);
		for(int i = 0; i < width; ++i)
			res[i] = a[i]? SIGNAL_ONE: SIGNAL_ZERO;
		return res;
	}

}
