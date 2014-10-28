package circuits.arithmetic;

import java.awt.peer.LightweightPeer;
import java.io.IOException;
import java.util.Arrays;

import test.Utils;
import circuits.ArithmeticLib;
import circuits.IntegerLib;
import flexsc.CompEnv;
import gc.BadLabelException;

//http://x86asm.net/articles/fixed-point-arithmetic-and-tricks/
public class FixedPointLib<T> implements ArithmeticLib<T> {

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

	public T[] inputOfAlice(double d) throws IOException {
		return env.inputOfAlice(Utils.fromFixPoint(d, width, offset));
	}

	public T[] inputOfBob(double d) throws IOException {
		return env.inputOfBob(Utils.fromFixPoint(d, width, offset));
	}

	public T[] add(T[] x, T[] y) {
		return lib.add(x, y);
	}

	public T[] sub(T[] x, T[] y) {
		return lib.sub(x, y);
	}

	//http://dsp.stackexchange.com/questions/7906/fixed-point-multiplication-with-negative-numbers
	public T[] multiply(T[] x, T[] y) {
		T[] res = lib.karatsubaMultiply(lib.absolute(x), lib.absolute(y));
		res = Arrays.copyOfRange(res, offset,offset+ width);
		return lib.addSign(res, lib.xor(x[x.length-1], y[y.length-1]));
	}
//	public T[] multiply(T[] x, T[] y) {
//		T[] res = lib.multiplyFull(x, y);
//		res = Arrays.copyOf(lib.rightPublicShift(res, offset), width);
////		try {
////			double d1;
////			double d = outputToAlice(x);
////			double dd = outputToAlice(y);
////			d1 = outputToAlice(res);
//		
////		if(Math.abs(d1-d*dd) > 10){
////			System.out.print(Arrays.toString(lib.getEnv().outputToAlice(res)));
////			System.out.println(d+" "+dd+" "+d1);
////		}
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		} catch (BadLabelException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		return res;
//	}

	public T[] div(T[] x, T[] y) {
		T[] padX = lib.padSignedSignal(x, x.length + offset);
		return Arrays.copyOf(lib.div(lib.leftPublicShift(padX, offset), y), width);
	}

	public T[] publicValue(double d) {
		boolean[] a = Utils.fromFixPoint(d, width, offset);
		T[] res = env.newTArray(width);
		for (int i = 0; i < width; ++i)
			res[i] = a[i] ? lib.SIGNAL_ONE : lib.SIGNAL_ZERO;
		return res;
	}

	@Override
	public T leq(T[] a, T[] b) {
		return lib.leq(a, b);
	}

	@Override
	public T eq(T[] a, T[] b) {
		return lib.eq(a, b);
	}

	@Override
	public T[] sqrt(T[] a) {

		return null;
	}
	
	@Override
	public CompEnv<T> getEnv() {
		return env;
	}

	@Override
	public T[] toSecureInt(T[] a, IntegerLib<T> lib) {
		T[] res = lib.env.newTArray(lib.width);
		T[] intPart = Arrays.copyOfRange(a, offset, a.length);
		if(res.length >= intPart.length)
			System.arraycopy(intPart, 0, res, 0, intPart.length);
		else 
			res = Arrays.copyOf(intPart, res.length);
		return res;
	}

	@Override
	public T[] toSecureFloat(T[] a, FloatLib<T> lib) {
		return null;
	}

	@Override
	public T[] toSecureFixPoint(T[] a, FixedPointLib<T> lib) {
		//later may support case between different libs;
		return a;
	}

	@Override
	public double outputToAlice(T[] a) throws IOException, BadLabelException {
		return Utils.toFixPoint(env.outputToAlice(a), offset);
	}
}