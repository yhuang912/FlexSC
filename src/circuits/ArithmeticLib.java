package circuits;

public interface ArithmeticLib<T> {
	T[] add(T[] x, T[] y) throws Exception;
	T[] multiply(T[] x, T[] y) throws Exception;
	T[] div(T[] x, T[] y) throws Exception;
	T[] sub(T[] x, T[] y) throws Exception;
	T[] publicValue(double v) throws Exception;
	T leq(T[] a, T[] b) throws Exception;
	T eq(T[] a, T[] b) throws Exception;
	T[] sqrt(T[] a) throws Exception;
}