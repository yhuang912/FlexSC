package circuits;

import flexsc.CompEnv;
import gc.Signal;

public class CircuitLib {
	protected CompEnv<Signal> env;
	public final static Signal SIGNAL_ZERO = new Signal(false);
	public final static Signal SIGNAL_ONE = new Signal(true);

	public CircuitLib(CompEnv<Signal> e) {
		env = e;
	}

	public static Signal[] toSignals(int a, int width) {
		Signal[] result = new Signal[width];
		for (int i = 0; i < width; ++i) {
			if ((a & 1) == 1)
				result[i] = SIGNAL_ONE;
			else
				result[i] = SIGNAL_ZERO;
			a >>= 1;
		}
		return result;
	}

	// Defaults to 32 bit constants.
	public static Signal[] toSignals(int value) {
		return toSignals(value, 32);
	}

	public Signal[] zeros(int length) {
		Signal[] result = new Signal[length];
		for (int i = 0; i < length; ++i) {
			result[i] = SIGNAL_ZERO;
		}
		return result;
	}

	public Signal[] ones(int length) {
		Signal[] result = new Signal[length];
		for (int i = 0; i < length; ++i) {
			result[i] = SIGNAL_ONE;
		}
		return result;
	}

	/*
	 * Basic logical operations on Signal and Signal[]
	 */
	public Signal and(Signal x, Signal y) throws Exception {
		assert (x != null && y != null) : "CircuitLib.and: bad inputs";

		return env.and(x, y);
	}

	public Signal[] and(Signal[] x, Signal[] y) throws Exception {
		assert (x != null && y != null && x.length == y.length) : "CircuitLib.and[]: bad inputs";

		Signal[] result = new Signal[x.length];
		for (int i = 0; i < x.length; ++i) {
			result[i] = and(x[i], y[i]);
		}
		return result;
	}

	public Signal xor(Signal x, Signal y) {
		assert (x != null && y != null) : "CircuitLib.xor: bad inputs";

		return env.xor(x, y);
	}

	public Signal[] xor(Signal[] x, Signal[] y) {
		assert (x != null && y != null && x.length == y.length) : "CircuitLib.xor[]: bad inputs";

		Signal[] result = new Signal[x.length];
		for (int i = 0; i < x.length; ++i) {
			result[i] = xor(x[i], y[i]);
		}
		return result;
	}

	public Signal not(Signal x) {
		assert (x != null) : "CircuitLib.not: bad input";

		return env.xor(x, SIGNAL_ONE);
	}

	// tested
	public Signal[] not(Signal[] x) {
		assert (x != null) : "CircuitLib.not[]: bad input";

		Signal[] result = new Signal[x.length];
		for (int i = 0; i < x.length; ++i) {
			result[i] = not(x[i]);
		}
		return result;
	}

	public Signal or(Signal x, Signal y) throws Exception {
		assert (x != null && y != null) : "CircuitLib.or: bad inputs";

		return xor(xor(x, y), and(x, y)); // http://stackoverflow.com/a/2443029
	}

	public Signal[] or(Signal[] x, Signal[] y) throws Exception {
		assert (x != null && y != null && x.length == y.length) : "CircuitLib.or[]: bad inputs";

		Signal[] result = new Signal[x.length];
		for (int i = 0; i < x.length; ++i) {
			result[i] = or(x[i], y[i]);
		}
		return result;
	}

	/*
	 * Output x when c == 0; Otherwise output y.
	 */
	public Signal mux(Signal x, Signal y, Signal c) throws Exception {
		assert (x != null && y != null && c != null) : "CircuitLib.mux: bad inputs";
		Signal t = xor(x, y);
		t = and(t, c);
		Signal ret = xor(t, x);
		return ret;
	}

	public Signal[] mux(Signal[] x, Signal[] y, Signal c) throws Exception {
		assert (x != null && y != null && x.length == y.length) : "CircuitLib.mux[]: bad inputs";

		Signal[] ret = new Signal[x.length];
		for (int i = 0; i < x.length; i++)
			ret[i] = mux(x[i], y[i], c);

		return ret;
	}
}