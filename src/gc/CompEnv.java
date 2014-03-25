package gc;

public interface CompEnv <T> {
//	static int serverPort = 54321;
	
	T inputOfGen(boolean in) throws Exception;
	T inputOfEva(boolean in) throws Exception;
	boolean outputToGen(T out) throws Exception;
//	boolean transOutputToEva(T out) throws Exception;
	
//	T not(T a);
//	T or(T a, T b) throws Exception;
	T and(T a, T b) throws Exception;
	T xor(T a, T b);
	T not(T a);
	
//	T[] add(T[] a, T[] b);
//	T[] mux(T[] a, T[] b, T c);
}