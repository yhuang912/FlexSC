package flexsc;

public interface CompEnv<T> {
	T inputOfAlice(boolean in) throws Exception;
	T inputOfBob(boolean in) throws Exception;
	T[] inputOfAlice(boolean[] in) throws Exception;
	T[] inputOfBob(boolean[] in) throws Exception;
	boolean outputToAlice(T out) throws Exception;
	boolean[] outputToAlice(T[] out) throws Exception;
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