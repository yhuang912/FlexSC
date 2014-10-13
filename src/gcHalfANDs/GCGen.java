package gcHalfANDs;

import java.security.*;
import java.io.*;
import java.math.BigInteger;

import circuits.FloatFormat;
import flexsc.Flag;
import objects.Float.Representation;
import ot.*;
import test.Utils;

public class GCGen extends GCCompEnv {

	public final GCSignal R;
	SecureRandom rnd = new SecureRandom();

	InputStream is;
	OutputStream os;
	OTSender snd;
	Garbler gb;

	long gid = 0;

	public long ands = 0;
	public GCGen(InputStream is, OutputStream os) throws Exception {
		this.is = is;
		this.os = os;

		R = GCSignal.freshLabel(rnd);
		R.setLSB();

		snd = new OTExtSender(80, is, os);
//		snd = new NPOTSender(80, is, os);
//		snd = new FakeOTSender(80, is, os);
		gb = new Garbler();
	}

	private GCSignal[] genPair() {
		GCSignal[] label = new GCSignal[2];
		label[0] = GCSignal.freshLabel(rnd);
		label[1] = R.xor(label[0]);
		return label;
	}

	public GCSignal inputOfAlice(boolean in) throws Exception {
		GCSignal[] label = genPair();
		long t = System.currentTimeMillis();
		label[in ? 1 : 0].send(os);
		Flag.OTTotalTime += (System.currentTimeMillis()-t);
		return label[0];
	}
	
	GCSignal sig = new GCSignal(true);

	public GCSignal inputOfBob(boolean in) throws Exception {
		GCSignal[] label = genPair();
		long t = System.currentTimeMillis();
		snd.send(label);
		Flag.OTTotalTime += (System.currentTimeMillis()-t);
		return label[0];
	}
	
	public GCSignal[] inputOfAlice(boolean[] x) throws Exception {
		GCSignal[] result = new GCSignal[x.length];
		for(int i = 0; i < x.length; ++i)
			result[i] = inputOfAlice(x[i]);
		os.flush();
		return result;
	}

	public GCSignal[] inputOfBob(boolean[] x) throws Exception {
		GCSignal[][] pair = new GCSignal[x.length][2];
		for(int i = 0; i < x.length; ++i)
			pair[i] = genPair();
		long t = System.currentTimeMillis();
		snd.send(pair);
		Flag.OTTotalTime += (System.currentTimeMillis()-t);
		GCSignal[] result = new GCSignal[x.length];
		for(int i = 0; i < x.length; ++i)
			result[i] = pair[i][0];

		return result;

	}

	
	public Representation<GCSignal> inputOfAliceFloatPoint(double d, int widthV, int widthP) throws Exception {
		FloatFormat f = new FloatFormat(d, widthV, widthP);
		GCSignal signalS = inputOfAlice(f.s);
		GCSignal signalZ = inputOfAlice(f.z);
		GCSignal[] v = inputOfAlice(f.v);
		GCSignal[] p = inputOfAlice(f.p);
		
		return new Representation<GCSignal>(signalS, p, v, signalZ);
	}
	

//	public Representation<GCSignal> inputOfGen(FloatFormat f, int widthV, int widthP) throws Exception {
//		GCSignal signalS = inputOfGen(f.s);
//		GCSignal signalZ = inputOfGen(f.z);
//		GCSignal[] v = inputOfGen(f.v);
//		GCSignal[] p = inputOfGen(f.p);
//		
//		return new Representation<GCSignal>(signalS, p, v, signalZ);
//	}	

	public Representation<GCSignal> inputOfGen(FloatFormat f, int widthV, int widthP) throws Exception {
		GCSignal signalS = inputOfAlice(f.s);
		GCSignal signalZ = inputOfAlice(f.z);
		GCSignal[] v = inputOfAlice(f.v);
		GCSignal[] p = inputOfAlice(f.p);
		
		return new Representation<GCSignal>(signalS, p, v, signalZ);
	}	

	
	public Representation<GCSignal> inputOfBobFloatPoint(double d, int widthV, int widthP) throws Exception {
		FloatFormat f = new FloatFormat(0, widthV, widthP);
		GCSignal signalS = inputOfBob(false);
		GCSignal signalZ = inputOfBob(false);
		GCSignal[] v = inputOfBob(f.v);
		GCSignal[] p = inputOfBob(f.p);
		
		return new Representation<GCSignal>(signalS, p, v, signalZ);
	}

	public GCSignal[] inputOfAliceFixedPoint(double a, int width, int offset) throws Exception {
		GCSignal[] result = inputOfAlice(Utils.fromFixPoint(a,width,offset));
		return result;
	}
	
	public GCSignal[] inputOfBobFixedPoint(double a, int width, int offset) throws Exception {
		return inputOfBob(new boolean[width]);
	}
	
	public boolean outputToAlice(GCSignal out) throws Exception {
		os.flush();

		if (out.isPublic())
			return out.v;
		
		GCSignal lb = GCSignal.receive(is);
		if (lb.equals(out))
			return false;
		else if (lb.equals(R.xor(out)))
			return true;

		return false;
//		throw new Exception("bad label at final output.");
	}
	
	public double outputToAliceFixedPoint(GCSignal[] f, int offset) throws Exception{
		boolean[] res = outputToAlice(f);
		return  Utils.toFixPoint(res, res.length, offset);
	}
	
	public boolean[] outputToAlice(GCSignal[] out) throws Exception {
		boolean [] result = new boolean[out.length];
		for(int i = 0; i < result.length; ++i) {
			result[i] = outputToAlice(out[i]);
		}
		return result;
	}

	public double outputToAliceFloatPoint(Representation<GCSignal> gcf) throws Exception {
		boolean s = outputToAlice(gcf.s);
		boolean z = outputToAlice(gcf.z);
		boolean[] v = outputToAlice(gcf.v);
		boolean[] p = outputToAlice(gcf.p);
		return new FloatFormat(v, p, s, z).toDouble();
	}
	// public boolean transOutputToEva(BitSet out) throws Exception {
	//
	// }

//	private GCSignal[][] gtt = new GCSignal[2][2];
	private GCSignal labelL[] = new GCSignal[2];
	private GCSignal labelR[] = new GCSignal[2];

	private GCSignal TG, WG, TE, WE;
	
	private GCSignal garble(GCSignal a, GCSignal b) {
		labelL[0] = a;
		labelL[1] = R.xor(labelL[0]);
		labelR[0] = b;
		labelR[1] = R.xor(labelR[0]);

		int cL = a.getLSB() ? 1 : 0;
		int cR = b.getLSB() ? 1 : 0;

		// first half gate
		TG = gb.hash(labelL[0], gid, false).xor(gb.hash(labelL[1], gid, false)).xor((cR == 1) ? R : GCSignal.ZERO);
		WG = gb.hash(labelL[0], gid, false).xor((cL == 1) ? TG : GCSignal.ZERO);
		
		// second half gate
		TE = gb.hash(labelR[0], gid, true).xor(gb.hash(labelR[1], gid, true)).xor(labelL[0]);
		WE = gb.hash(labelR[0], gid, true).xor((cR == 1) ? (TE.xor(labelL[0])) : GCSignal.ZERO);
		
		// send the encrypted gate
		try {
			long t = System.currentTimeMillis();
			TG.send(os);
			TE.send(os);
			Flag.GargleIOTime += (System.currentTimeMillis()-t);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// combine halves
		return WG.xor(WE);
	}

//	private void sendGTT() {
//		try {
//			long t = System.currentTimeMillis();
//			gtt[0][1].send(os);
//			gtt[1][0].send(os);
//			gtt[1][1].send(os);
//			Flag.GargleIOTime += (System.currentTimeMillis()-t);
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(1);
//		}
//	}
	
	public GCSignal and(GCSignal a, GCSignal b) {
		++ands;
		long t = System.currentTimeMillis();
		GCSignal res;
		if (a.isPublic() && b.isPublic())
			res = new GCSignal(a.v && b.v);
		else if (a.isPublic())
			res = a.v ? b : new GCSignal(false);
		else if (b.isPublic())
			res = b.v ? a : new GCSignal(false);
		else {
			GCSignal ret = garble(a, b);
			
//			sendGTT();
			gid++;
			res = ret;
		}
		Flag.GarbleTime += (System.currentTimeMillis()-t);
		return res;
	}

	// public BitSet or(BitSet a, BitSet b) {
	// BitSet zero = new BitSet();
	// if (a.equals(R) || b.equals(R))
	// return R;
	// else
	// return zero;
	// }
	//
	GCSignal f = GCSignal.freshLabel(new SecureRandom());
	public GCSignal xor(GCSignal a, GCSignal b) {
		if (a.isPublic() && b.isPublic())
			return new GCSignal(a.v ^ b.v);
		else if (a.isPublic())
			return a.v ? not(b) : new GCSignal(b);
		else if (b.isPublic())
			return b.v ? not(a) : new GCSignal(a);
		else {
			return a.xor(b);
		}
	}

	public GCSignal not(GCSignal a) {
		if (a.isPublic())
			return new GCSignal(!a.v);
		else 
			return R.xor(a);
	}

}
