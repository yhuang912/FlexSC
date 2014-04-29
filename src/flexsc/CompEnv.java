package flexsc;

import objects.Float.Representation;

public interface CompEnv<T> {
	T inputOfGen(boolean in) throws Exception;
	T inputOfEva(boolean in) throws Exception;
	T[] inputOfGen(boolean[] in) throws Exception;
	T[] inputOfEva(boolean[] in) throws Exception;
	boolean outputToGen(T out) throws Exception;
	boolean[] outputToGen(T[] out) throws Exception;
	
	public Representation<T> inputOfEva(double d, int widthV, int widthP) throws Exception;
	public Representation<T> inputOfGen(double d, int widthV, int widthP) throws Exception;
	public double outputToGen(Representation<T> gcf) throws Exception;
	
	public T[] inputOfEvaFixPoint(double a, int width, int offset) throws Exception;
	public T[] inputOfGenFixPoint(double d, int width, int offset) throws Exception;
	public double outputToGen(T[] f, int offset) throws Exception;

	
//	boolean transOutputToEva(T out) throws Exception;
	
//	T or(T a, T b) throws Exception;
	T and(T a, T b) throws Exception;
	T xor(T a, T b);
	T not(T a);
	
//	T[] add(T[] a, T[] b);
//	T[] mux(T[] a, T[] b, T c);
	
	T ONE();
	T ZERO();
	
	T[] newTArray(int len);
	T[][] newTArray(int d1, int d2);
	T[][][] newTArray(int d1, int d2, int d3);
	T newT(boolean v);
}