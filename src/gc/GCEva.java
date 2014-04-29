package gc;

import objects.Float.Representation;

import java.io.*;

import circuits.FloatFormat;
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
	}

	public GCSignal inputOfAlice(boolean in) throws Exception {
		return GCSignal.receive(is);
	}

	public GCSignal inputOfBob(boolean in) throws Exception {
		return rcv.receive(in);
	}

	public GCSignal[] inputOfBob(boolean[] x) throws Exception {
		GCSignal[] result = new GCSignal[x.length];
		for(int i = 0; i < x.length; ++i)
			result[i] = inputOfBob(x[i]);
		return result;
	}

	public GCSignal[] inputOfAlice(boolean[] x) throws Exception {
		GCSignal[] result = new GCSignal[x.length];
		for(int i = 0; i < x.length; ++i)
			result[i] = inputOfAlice(false);
		return result;
	}
	
	public Representation<GCSignal> inputOfEva(double d, int widthV, int widthP) throws Exception {
		FloatFormat f = new FloatFormat(d, widthV, widthP);
		GCSignal signalS = inputOfBob(f.s);
		GCSignal signalZ = inputOfBob(f.z);
		GCSignal[] v = inputOfBob(f.v);
		GCSignal[] p = inputOfBob(f.p);
		return new Representation<GCSignal>(signalS, p, v, signalZ);
	}
	
	public Representation<GCSignal> inputOfGen(int widthV, int widthP) throws Exception {
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
	public GCSignal[] inputOfEvaFixPoint(double a, int width, int offset) throws Exception {
		GCSignal[] result = inputOfBob(Utils.fromFixPoint(a,width,offset));
		return result;
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

	public double outputToGen(Representation<GCSignal> gcf) throws Exception {
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

	private void receiveGTT() {
		try {
			gtt[0][0] = GCSignal.ZERO;
			gtt[0][1] = GCSignal.receive(is);
			gtt[1][0] = GCSignal.receive(is);
			gtt[1][1] = GCSignal.receive(is);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public GCSignal and(GCSignal a, GCSignal b) {
		++nonFreeGate;
		if (a.isPublic() && b.isPublic())
			return new GCSignal(a.v && b.v);
		else if (a.isPublic())
			return a.v ? b : new GCSignal(false);
		else if (b.isPublic())
			return b.v ? a : new GCSignal(false);
		else {
			receiveGTT();

			int i0 = a.getLSB() ? 1 : 0;
			int i1 = b.getLSB() ? 1 : 0;

			GCSignal out = gb.dec(a, b, gid, gtt[i0][i1]);
			gid++;
			return out;
		}
	}

	// public Label or(Label a, Label b) {
	// Label zero = new Label();
	// if (a.equals(R) || b.equals(R))
	// return R;
	// else
	// return zero;
	// }
	//
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