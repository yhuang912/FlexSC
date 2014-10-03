package circuits.arithmetic;

import java.util.Arrays;

import circuits.ArithmeticLib;
import circuits.IntegerLib;
import test.Utils;
import flexsc.CompEnv;

//http://x86asm.net/articles/fixed-point-arithmetic-and-tricks/
public class FixedPointLib<T> implements ArithmeticLib<T>{

	CompEnv<T> env;
	IntegerLib<T> lib;
	int offset;
	int width;
	public FixedPointLib(CompEnv<T> e, int width, int offset) {
		this.env = e;
		lib = new IntegerLib<>(e);
		this.offset = offset;
		this.width = width;
	}
	
	public T[] add(T[] x, T[] y) throws Exception {
		return lib.add(x, y);
	}
	
	public T[] sub(T[] x, T[] y) throws Exception{
		return lib.sub(x, y);
	}
	
	public T[] multiply(T[] x, T[] y) throws Exception{
		T[] res = lib.multiplyFull(x, y);
		return Arrays.copyOf(lib.rightPublicShift(res, offset), width);
	}
	
	public T[] div(T[] x, T[] y) throws Exception {
		T[] padX = lib.padSignedSignal(x, x.length + offset);
		return lib.div(lib.leftPublicShift(padX, offset), y);
	}
	
	public T[] publicValue(double d) {
		boolean[] a = Utils.fromFixPoint(d, width, offset);
		T[] res = env.newTArray(width);
		for(int i = 0; i < width; ++i)
			res[i] = a[i]? lib.SIGNAL_ONE: lib.SIGNAL_ZERO;
		return res;
	}

	@Override
	public T leq(T[] a, T[] b) throws Exception {
		return lib.leq(a, b);
	}

	@Override
	public T eq(T[] a, T[] b) throws Exception {
		return lib.eq(a, b);
	}

	@Override
	public T[] sqrt(T[] a) throws Exception {
		
		return null;
	}
}