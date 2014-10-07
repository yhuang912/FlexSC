package circuits;

import java.io.IOException;

import circuits.arithmetic.FixedPointLib;
import circuits.arithmetic.FloatLib;
import flexsc.CompEnv;
import gc.BadLabelException;

public interface ArithmeticLib<T> {
	CompEnv<T> getEnv();

	T[] inputOfAlice(double d) throws IOException;

	T[] inputOfBob(double d) throws IOException;

	double outputToAlice(T[] a) throws IOException, BadLabelException;

	T[] add(T[] x, T[] y);

	T[] multiply(T[] x, T[] y);

	T[] div(T[] x, T[] y);

	T[] sub(T[] x, T[] y);

	T[] publicValue(double v);

	T leq(T[] a, T[] b);

	T eq(T[] a, T[] b);

	T[] sqrt(T[] a);

	T[] toSecureInt(T[] a, IntegerLib<T> lib);

	T[] toSecureFloat(T[] a, FloatLib<T> lib);

	T[] toSecureFixPoint(T[] a, FixedPointLib<T> lib);
}