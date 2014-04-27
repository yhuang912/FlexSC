package flexsc;

public interface CompEnv<T> {
	T inputOfGen(boolean in) throws Exception;
	T inputOfEva(boolean in) throws Exception;
	boolean outputToGen(T out) throws Exception;
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
	T newT(boolean v);
}