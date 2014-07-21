package circuits;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import flexsc.CompEnv;
import flexsc.Party;
import gc.GCSignal;

public class CircuitLib<T> {
	protected CompEnv<T> env;
//	public final static Signal SIGNAL_ZERO = new Signal(false);
//	public final static Signal SIGNAL_ONE = new Signal(true);
	public final T SIGNAL_ZERO;
	public final T SIGNAL_ONE;

	public CircuitLib(CompEnv<T> e) {
		env = e;
		SIGNAL_ZERO = e.ZERO();
		SIGNAL_ONE = e.ONE();
	}

	public T[] toSignals(int a, int width) {
		T[] result = env.newTArray(width);
		for (int i = 0; i < width; ++i) {
			if ((a & 1) == 1)
				result[i] = SIGNAL_ONE;
			else
				result[i] = SIGNAL_ZERO;
			a >>= 1;
		}
		return result;
	}

	public T[] randBools(Random rng, int length) throws Exception {
		boolean[] res = new boolean[length];
		for(int i = 0; i < length; ++i)
			res[i] = rng.nextBoolean();
		T[] alice = env.inputOfAlice(res); 
		T[] bob = env.inputOfBob(res);
		return xor(alice, bob);
	}

	public boolean[] getBooleans(T[] x) throws Exception {
		return env.outputToAlice(x);
	}
	
	public boolean[] syncBooleans(boolean[] pos) throws IOException {
		if(env.getParty() == Party.Alice){
			//send pos to bob
			env.os.write(new byte[]{(byte) pos.length});
			byte[] tmp = new byte[pos.length];
			for(int i = 0; i < pos.length; ++i)
				tmp[i] = (byte) (pos[i] ? 1 : 0);
			env.os.write(tmp);
			env.os.flush();
		}
		else {
			byte[] l = new byte[1];
			env.is.read(l);
			byte tmp[] = new byte[l[0]];
			env.is.read(tmp);
			pos = new boolean[l[0]];
			for(int k = 0; k < tmp.length; ++k) {
				pos[k] = ((tmp[k] - 1) == 0);
			}
		}
		return pos;
	}
	
	// Defaults to 32 bit constants.
	public T[] toSignals(int value) {
		return toSignals(value, 32);
	}

	/*
	 * If GCSignal is being passed to toSignals (happens because of subtle issues 
	 * in the RAMSCCompiler), then don't do anything just return that value. 
	 * I know this is super hacky, so I'll try to fix the internal issues with the 
	 * compiler itself, but this is probably a quick fix for now.
	*/
	public GCSignal[] toSignals(GCSignal[] value) {
		return value;
	}
	
	public T[] zeros(int length) {
		T[] result = env.newTArray(length);
		for (int i = 0; i < length; ++i) {
			result[i] = SIGNAL_ZERO;
		}
		return result;
	}

	public T[] ones(int length) {
		T[] result = env.newTArray(length);
		for (int i = 0; i < length; ++i) {
			result[i] = SIGNAL_ONE;
		}
		return result;
	}

	/*
	 * Basic logical operations on Signal and Signal[]
	 */
	public T and(T x, T y) throws Exception {
		assert (x != null && y != null) : "CircuitLib.and: bad inputs";

		return env.and(x, y);
	}

	public T[] and(T[] x, T[] y) throws Exception {
		assert (x != null && y != null && x.length == y.length) : "CircuitLib.and[]: bad inputs";

		T[] result = env.newTArray(x.length);
		for (int i = 0; i < x.length; ++i) {
			result[i] = and(x[i], y[i]);
		}
		return result;
	}

	public T xor(T x, T y) {
		assert (x != null && y != null) : "CircuitLib.xor: bad inputs";

		return env.xor(x, y);
	}

	public T[] xor(T[] x, T[] y) {
		assert (x != null && y != null && x.length == y.length) : "CircuitLib.xor[]: bad inputs";

		T[] result = env.newTArray(x.length);
		for (int i = 0; i < x.length; ++i) {
			result[i] = xor(x[i], y[i]);
		}
		return result;
	}

	public T not(T x) {
		assert (x != null) : "CircuitLib.not: bad input";

		return env.xor(x, SIGNAL_ONE);
	}

	// tested
	public T[] not(T[] x) {
		assert (x != null) : "CircuitLib.not[]: bad input";

		T[] result = env.newTArray(x.length);
		for (int i = 0; i < x.length; ++i) {
			result[i] = not(x[i]);
		}
		return result;
	}

	public T or(T x, T y) throws Exception {
		assert (x != null && y != null) : "CircuitLib.or: bad inputs";

		return xor(xor(x, y), and(x, y)); // http://stackoverflow.com/a/2443029
	}

	public T[] or(T[] x, T[] y) throws Exception {
		assert (x != null && y != null && x.length == y.length) : "CircuitLib.or[]: bad inputs";

		T[] result = env.newTArray(x.length);
		for (int i = 0; i < x.length; ++i) {
			result[i] = or(x[i], y[i]);
		}
		return result;
	}

	/*
	 * Output x when c == 0; Otherwise output y.
	 */
	public T mux(T x, T y, T c) throws Exception {
		assert (x != null && y != null && c != null) : "CircuitLib.mux: bad inputs";
		T t = xor(x, y);
		t = and(t, c);
		T ret = xor(t, x);
		return ret;
	}

	public T[] mux(T[] x, T[] y, T c) throws Exception {
		assert (x != null && y != null && x.length == y.length) : "CircuitLib.mux[]: bad inputs";

		T[] ret = env.newTArray(x.length);
		for (int i = 0; i < x.length; i++)
			ret[i] = mux(x[i], y[i], c);

		return ret;
	}
	
	public T[] padSignal(T[] a, int length) {
		T[] res = zeros(length);
		for(int i = 0; i < a.length && i < length; ++i)
			res[i] = a[i];
		return res;
	}
	
	public T[] copy(T[] x) {
		return Arrays.copyOf(x, x.length);
	}
}