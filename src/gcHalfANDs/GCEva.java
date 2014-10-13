package gcHalfANDs;

import objects.Float.Representation;

import java.io.*;
import java.security.SecureRandom;

import circuits.FloatFormat;
import flexsc.Flag;
import ot.*;
import test.Utils;

public class GCEva extends GCCompEnv {
	InputStream is;
	OutputStream os;
	OTReceiver rcv;
	Garbler gb;
	public int nonFreeGate = 0;
	long gid = 0;

	public GCEva(InputStream is, OutputStream os) throws Exception {
		this.is = is;
		this.os = os;

		rcv = new OTExtReceiver(is, os);
//		rcv = new NPOTReceiver(is, os);
//		rcv = new FakeOTReceiver(is, os);
		gb = new Garbler();
		gtt[0][0] = GCSignal.ZERO;
	}

	GCSignal sig = new GCSignal(true);
	public GCSignal inputOfAlice(boolean in) throws Exception {
		long t = System.currentTimeMillis();
		GCSignal signal = GCSignal.receive(is);
		Flag.OTTotalTime += (System.currentTimeMillis()-t);
		return signal;
	}

	public GCSignal inputOfBob(boolean in) throws Exception {
		long t = System.currentTimeMillis();
		GCSignal signal = rcv.receive(in);
		Flag.OTTotalTime += (System.currentTimeMillis()-t);
		return signal; 
	}

	public GCSignal[] inputOfBob(boolean[] x) throws Exception {
		long t = System.currentTimeMillis();
		GCSignal[] signal = rcv.receive(x);
		Flag.OTTotalTime += (System.currentTimeMillis()-t);
		return signal;
	}

	public GCSignal[] inputOfAlice(boolean[] x) throws Exception {
		GCSignal[] result = new GCSignal[x.length];
		for(int i = 0; i < x.length; ++i)
			result[i] = inputOfAlice(false);
		return result;
	}

	public Representation<GCSignal> inputOfBobFloatPoint(double d, int widthV, int widthP) throws Exception {
		FloatFormat f = new FloatFormat(d, widthV, widthP);
		GCSignal signalS = inputOfBob(f.s);
		GCSignal signalZ = inputOfBob(f.z);
		GCSignal[] v = inputOfBob(f.v);
		GCSignal[] p = inputOfBob(f.p);
		return new Representation<GCSignal>(signalS, p, v, signalZ);
	}

	public Representation<GCSignal> inputOfAliceFloatPoint(double d, int widthV, int widthP) throws Exception {
		FloatFormat f = new FloatFormat(0, widthV, widthP);
		GCSignal signalS = inputOfAlice(f.s);
		GCSignal signalZ = inputOfAlice(f.z);
		GCSignal[] v = inputOfAlice(f.v);
		GCSignal[] p = inputOfAlice(f.p);

		return new Representation<GCSignal>(signalS, p, v, signalZ);
	}

	public Representation<GCSignal> inputOfEva(FloatFormat f, int widthV, int widthP) throws Exception {
		GCSignal signalS = inputOfBob(f.s);
		GCSignal signalZ = inputOfBob(f.z);
		GCSignal[] v = inputOfBob(f.v);
		GCSignal[] p = inputOfBob(f.p);
		return new Representation<GCSignal>(signalS, p, v, signalZ);
	}

	public GCSignal[] inputOfBobFixedPoint(double a, int width, int offset) throws Exception {
		GCSignal[] result = inputOfBob(Utils.fromFixPoint(a,width,offset));
		return result;
	}


	public GCSignal[] inputOfAliceFixedPoint(double d, int width, int offset) throws Exception {
		return inputOfAlice(new boolean[width]);
	}

	public double outputToAliceFixedPoint(GCSignal[] f, int offset) throws Exception{
		boolean[] res = outputToAlice(f);
		return  Utils.toFixPoint(res, res.length, offset);
	}


	public GCSignal[] inputOfGenFixPoint(int width, int offset) throws Exception {
		return inputOfAlice(new boolean[width]);
	}

	public boolean outputToAlice(GCSignal out) throws Exception {
		if (!out.isPublic())
			out.send(os);
		return false;
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


	// public boolean transOutputToEva(Label out) throws Exception {
	//
	// }

	private GCSignal[][] gtt = new GCSignal[2][2];

	SecureRandom rng = new SecureRandom();
//	private void receiveGTT() {
//		try {
//			long t = System.currentTimeMillis();
//			gtt[0][1] = GCSignal.receive(is);
//			gtt[1][0] = GCSignal.receive(is);
//			gtt[1][1] = GCSignal.receive(is);
//			Flag.GargleIOTime += (System.currentTimeMillis()-t);
//
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(1);
//		}
//	}

	public GCSignal and(GCSignal a, GCSignal b) {
		long t = System.currentTimeMillis();
		GCSignal res;
		++nonFreeGate;
		if (a.isPublic() && b.isPublic())
			res =  new GCSignal(a.v && b.v);
		else if (a.isPublic())
			res =  a.v ? b : new GCSignal(false);
		else if (b.isPublic())
			res = b.v ? a : new GCSignal(false);
		else {
//			receiveGTT();

			int i0 = a.getLSB() ? 1 : 0;
			int i1 = b.getLSB() ? 1 : 0;

			GCSignal TG = GCSignal.ZERO, WG, TE = GCSignal.ZERO, WE;
			try {
				TG = GCSignal.receive(is);
				TE = GCSignal.receive(is);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

			WG = gb.hash(a, gid, false).xor((i0 == 1) ? TG : GCSignal.ZERO);
			WE = gb.hash(b, gid, true).xor((i1 == 1) ? (TE.xor(a)) : GCSignal.ZERO);
			
			GCSignal out = WG.xor(WE);
			
			gid++;
			res =  out;
		}
		Flag.GarbleTime += (System.currentTimeMillis()-t);
		return res;
	}

	// public Label or(Label a, Label b) {
	// Label zero = new Label();
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
		else {
			return new GCSignal(a);
		}
	}
}