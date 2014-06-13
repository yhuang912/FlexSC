package flexsc;

import java.io.InputStream;
import java.io.OutputStream;

import objects.Float.Representation;

public interface CompEnv<T> {
	T inputOfAlice(boolean in) throws Exception;
	T inputOfBob(boolean in) throws Exception;
	boolean outputToAlice(T out) throws Exception;
	
	T[] inputOfAlice(boolean[] in) throws Exception;
	T[] inputOfBob(boolean[] in) throws Exception;
	boolean[] outputToAlice(T[] out) throws Exception;
	
	public Representation<T> inputOfBobFloatPoint(double d, int widthV, int widthP) throws Exception;
	public Representation<T> inputOfAliceFloatPoint(double d, int widthV, int widthP) throws Exception;
	public double outputToAliceFloatPoint(Representation<T> gcf) throws Exception;
	
	public T[] inputOfBobFixedPoint(double a, int width, int offset) throws Exception;
	public T[] inputOfAliceFixedPoint(double d, int width, int offset) throws Exception;
	public double outputToAliceFixedPoint(T[] f, int offset) throws Exception;

	T and(T a, T b) throws Exception;
	T xor(T a, T b);
	T not(T a);
	
	T ONE();
	T ZERO();
	
	T[] newTArray(int len);
	T[][] newTArray(int d1, int d2);
	T[][][] newTArray(int d1, int d2, int d3);
	T newT(boolean v);
	
	public CompEnv<T> getNewInstance(InputStream in, OutputStream os) throws Exception;
	public Party getParty();
}