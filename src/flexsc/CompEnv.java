package flexsc;

import gc.GCEva;
import gc.GCGen;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import cv.CVCompEnv;
import circuits.FloatFormat;
import objects.Float.Representation;
import pm.PMCompEnv;
import test.Utils;

public abstract class CompEnv<T> {
	public static CompEnv getEnv(Mode mode, Party p, InputStream is, OutputStream os) throws Exception{
		if(mode == Mode.REAL)
			if(p == Party.Bob)
				return new GCEva(is, os);
			else
				return new GCGen(is, os);
		else if(mode == Mode.VERIFY)
			return new CVCompEnv(is,os, p);
		else if(mode == Mode.COUNT)
			return new PMCompEnv(is,os, p);
		else return null;
	}
	
	public InputStream is;
	public OutputStream os;
	public Party p;
	public CompEnv(InputStream is, OutputStream os) {
		this.is = is;
		this.os = os;
	}
	
	public abstract T inputOfAlice(boolean in) throws Exception;
	public 	abstract T inputOfBob(boolean in) throws Exception;
	public abstract boolean outputToAlice(T out) throws Exception;
	
	public abstract T[] inputOfAlice(boolean[] in) throws Exception;
	public abstract T[] inputOfBob(boolean[] in) throws Exception;
	public abstract boolean[] outputToAlice(T[] out) throws Exception;
	
	public abstract T and(T a, T b) throws Exception;
	public abstract T xor(T a, T b);
	public abstract T not(T a);
	
	public abstract T ONE();
	public abstract T ZERO();
	
	public abstract T[] newTArray(int len);
	public abstract T[][] newTArray(int d1, int d2);
	public abstract T[][][] newTArray(int d1, int d2, int d3);
	public abstract T newT(boolean v);
	
	abstract public CompEnv<T> getNewInstance(InputStream in, OutputStream os) throws Exception;
	public Party getParty(){
		return p;
	}
	
	public void flush() throws Exception {
		os.flush();
	}
	
	public Representation<T> inputOfBobFloatPoint(double d, int widthV, int widthP)
			throws Exception {
		FloatFormat f = new FloatFormat(d, widthV, widthP);
		boolean[] data = new boolean[2+f.v.length+f.p.length];
		System.arraycopy(f.p, 0, data, 0, f.p.length);
		System.arraycopy(f.v, 0, data, f.p.length, f.v.length);
		data[data.length-2] = f.s;
		data[data.length-1] = f.z;
		T[] scData = inputOfBob(data);
		return new Representation<T>(scData[data.length-2], 
				Arrays.copyOf(scData, f.p.length),
				Arrays.copyOfRange(scData, f.p.length, f.p.length+f.v.length),
				scData[data.length-1]);
	}

	public Representation<T> inputOfAliceFloatPoint(double d, int widthV, int widthP)
			throws Exception {
		FloatFormat f = new FloatFormat(d, widthV, widthP);
		boolean[] data = new boolean[2+f.v.length+f.p.length];
		System.arraycopy(f.p, 0, data, 0, f.p.length);
		System.arraycopy(f.v, 0, data, f.p.length, f.v.length);
		data[data.length-2] = f.s;
		data[data.length-1] = f.z;
		T[] scData = inputOfAlice(data);
		return new Representation<T>(scData[data.length-2], 
				Arrays.copyOf(scData, f.p.length),
				Arrays.copyOfRange(scData, f.p.length, f.p.length+f.v.length),
				scData[data.length-1]);
	}

	public double outputToAliceFloatPoint(Representation<T> re) throws Exception {
		boolean s = outputToAlice(re.s);
		boolean z = outputToAlice(re.z);
		boolean[] v = outputToAlice(re.v);
		boolean[] p = outputToAlice(re.p);
		return new FloatFormat(v, p, s, z).toDouble();
	}


	public T[] inputOfBobFixedPoint(double d, int width, int offset)
			throws Exception {
		return inputOfBob(Utils.fromFixPoint(d,width,offset));
	}

	public T[] inputOfAliceFixedPoint(double d, int width, int offset)
			throws Exception {
		return inputOfAlice(Utils.fromFixPoint(d,width,offset));
	}

	public double outputToAliceFixedPoint(T[] f, int offset) throws Exception {
		boolean[] res = outputToAlice(f);
		return  Utils.toFixPoint(res, res.length, offset);
	}
//	abstract public Representation<T> inputOfBobFloatPoint(double d, int widthV, int widthP) throws Exception;
//	abstract public Representation<T> inputOfAliceFloatPoint(double d, int widthV, int widthP) throws Exception;
//	abstract public double outputToAliceFloatPoint(Representation<T> gcf) throws Exception;
	
//	abstract public T[] inputOfBobFixedPoint(double a, int width, int offset) throws Exception;
//	abstract public T[] inputOfAliceFixedPoint(double d, int width, int offset) throws Exception;
//	abstract public double outputToAliceFixedPoint(T[] f, int offset) throws Exception;

}