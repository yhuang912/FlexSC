package circuits;

import java.util.Arrays;

import test.Utils;
import flexsc.CompEnv;
import gc.Signal;

public class FixedPointLib extends IntegerLib {

	public FixedPointLib(CompEnv<Signal> e) {
		super(e);
		// TODO Auto-generated constructor stub
	}
	
	public Signal[] add(Signal[] x, Signal[] y, int offset) throws Exception {
		return add(x, y);
	}
	
	public Signal[] sub(Signal[] x, Signal[] y, int offset) throws Exception{
		return sub(x, y);
	}
	
	public Signal[] multiply(Signal[] x, Signal[] y, int offset) throws Exception{
		Signal[] res = unSignedMultiply(x, y);
		return Arrays.copyOfRange(res, offset, offset+x.length);
	}
	
	public Signal[] divide(Signal[] x, Signal[] y, int offset) throws Exception {
		int newLength = x.length*2;
		Signal[] padX = padSignal(x, newLength);
		padX = leftPublicShift(padX, newLength-x.length);
		Signal[] res = divide(padX, padSignal(y, newLength));
		Signal[] shiftRes = rightPublicShift(res, newLength-x.length-offset);
		return padSignal(shiftRes, x.length);
		
	}
	
	public Signal[] publicFixPoint(double d, int width, int offset) {
		boolean[] a = Utils.fromFixPoint(d, width, offset);
		Signal[] res = new Signal[width];
		for(int i = 0; i < width; ++i)
			res[i] = a[i]? SIGNAL_ONE: SIGNAL_ZERO;
		return res;
	}

}
