package pm;

import java.io.InputStream;
import java.io.OutputStream;

import test.Utils;
import flexsc.BooleanCompEnv;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;

/*
 * The computational environment for performance measurement. 
 */
public class PMCompEnv extends BooleanCompEnv {
	public static class Statistics {
		public int andGate = 0;
		public int xorGate = 0;
		public int notGate = 0;
		public int OTs = 0;
		public int NumEncAlice = 0;
		public int NumEncBob = 0;
		public long bandwidth = 0;
		public void flush() {
			bandwidth = 0;
			andGate = 0;
			xorGate = 0;
			notGate = 0;
			OTs = 0;
			NumEncAlice = 0;
			NumEncBob = 0;
		}
		public void add(Statistics s2) {
			andGate += s2.andGate;
			xorGate += s2.xorGate;
			notGate += s2.notGate;
			OTs += s2.OTs;
			NumEncAlice += s2.NumEncAlice;
			NumEncBob+=s2.NumEncBob;
			bandwidth +=s2.bandwidth;
		}
		public void finalize() {
			NumEncAlice = andGate*4+OTs*2;
			NumEncBob= andGate*1+OTs*1;
		}
		
		public Statistics newInstance() {
			Statistics s = new Statistics();
			s.andGate = andGate;
			s.xorGate = xorGate;
			s.notGate = notGate;
			s.OTs = OTs;
			s.NumEncAlice = NumEncAlice;
			s.NumEncBob = NumEncBob;
			s.bandwidth = bandwidth;
			return s;
		}
	}
	
	public Statistics statistic;
	Boolean t = true;
	Boolean f = false;

	public PMCompEnv(InputStream is, OutputStream os, Party p) {
		super(is, os, p, Mode.COUNT);
		this.p = p;
		t = true;
		f = false;
		statistic = new Statistics();
	}
	
	@Override
	public Boolean inputOfAlice(boolean in) throws Exception {
		++statistic.OTs;
		statistic.bandwidth+=10;
		return f;
	}

	@Override
	public Boolean inputOfBob(boolean in) throws Exception {
		return f;
	}

	@Override
	public boolean outputToAlice(Boolean out) throws Exception {
		statistic.bandwidth+=10;
		return false;
	}

	@Override
	public Boolean and(Boolean a, Boolean b) throws Exception {
		++statistic.andGate;
		statistic.bandwidth += 3*10;
		return f;
	}

	@Override
	public Boolean xor(Boolean a, Boolean b) {
		++statistic.xorGate;
		return f;
	}

	@Override
	public Boolean not(Boolean a) {
		++statistic.notGate;
		return f;
	}

	@Override
	public Boolean ONE() {
		return t;
	}

	@Override
	public Boolean ZERO() {
		return f;
	}

	@Override
	public boolean[] outputToAlice(Boolean[] out) throws Exception {
		statistic.bandwidth+=10*out.length;
		return Utils.tobooleanArray(out);
	}

	@Override
	public Boolean[] inputOfAlice(boolean[] in) throws Exception {
		statistic.OTs += in.length;
		statistic.bandwidth+=10*2*(80+in.length);
		return Utils.toBooleanArray(in);	
	}

	@Override
	public Boolean[] inputOfBob(boolean[] in) throws Exception {
		return Utils.toBooleanArray(in);
	}

//	@Override
//	public Representation<Boolean> inputOfBobFloatPoint(double d, int widthV, int widthP)
//			throws Exception {
//		FloatFormat f = new FloatFormat(d, 23, 9);
//		Representation<Boolean> result = 
//				new Representation<Boolean>(f.s, Utils.toBooleanArray(f.p), Utils.toBooleanArray(f.v), f.z);
//		return result;
//	}
//
//	@Override
//	public Representation<Boolean> inputOfAliceFloatPoint(double d, int widthV, int widthP)
//			throws Exception {
//		FloatFormat f = new FloatFormat(d, 23, 9);
//		Representation<Boolean> result = 
//				new Representation<Boolean>(f.s, Utils.toBooleanArray(f.p), Utils.toBooleanArray(f.v), f.z);
//		return result;
//	}
//
//	@Override
//	public double outputToAliceFloatPoint(Representation<Boolean> f) throws Exception {
//		FloatFormat d = new FloatFormat(Utils.tobooleanArray(f.v), Utils.tobooleanArray(f.p), f.s, f.z);
//		return d.toDouble();
//	}
//
//	@Override
//	public Boolean[] inputOfBobFixedPoint(double d, int width, int offset)
//			throws Exception {
//		return inputOfBob(Utils.fromFixPoint(d,width,offset));
//	}
//
//	@Override
//	public Boolean[] inputOfAliceFixedPoint(double d, int width, int offset)
//			throws Exception {
//		return inputOfBob(Utils.fromFixPoint(d,width,offset));
//	}
//
//	@Override
//	public double outputToAliceFixedPoint(Boolean[] f, int offset) throws Exception {
//		boolean[] res = outputToAlice(f);
//		return  Utils.toFixPoint(res, res.length, offset);
//	}

	@Override
	public CompEnv<Boolean> getNewInstance(InputStream in, OutputStream os) {
		return new PMCompEnv(in, os, this.getParty());
	}
}