package gc;

import java.security.*;
import java.io.*;

import circuits.FloatFormat;
import flexsc.CompEnv;
import objects.Float.Represention;
import ot.*;

public class GCGen implements CompEnv<Signal> {

	public final Signal R;
	SecureRandom rnd = new SecureRandom();

	InputStream is;
	OutputStream os;
	OTSender snd;
	Garbler gb;

	long gid = 0;

	public GCGen(InputStream is, OutputStream os) throws Exception {
		this.is = is;
		this.os = os;

		R = Signal.freshLabel(rnd);
		R.setLSB();

		snd = new NPOTSender(80, is, os);
//		snd = new FakeOTSender(80, is, os);
		gb = new Garbler();
	}

	private Signal[] genPair() {
		Signal[] label = new Signal[2];
		label[0] = Signal.freshLabel(rnd);
		label[1] = R.xor(label[0]);
		return label;
	}

	public Signal inputOfGen(boolean in) throws Exception {
		Signal[] label = genPair();
		label[in ? 1 : 0].send(os);
		return label[0];
	}

	public Signal inputOfEva(boolean in) throws Exception {
		Signal[] label = genPair();
		snd.send(label);
		return in ? label[1] : label[0];
	}
	
	public Signal[] inputOfGen(boolean[] x) throws Exception {
		Signal[] result = new Signal[x.length];
		for(int i = 0; i < x.length; ++i)
			result[i] = inputOfGen(x[i]);
		return result;
	}

	public Signal[] inputOfEva(boolean[] x) throws Exception {
		Signal[] result = new Signal[x.length];
		for(int i = 0; i < x.length; ++i)
			result[i] = inputOfEva(false);
		return result;
	}
	
	public Represention inputOfGen(double d, int widthV, int widthP) throws Exception {
		FloatFormat f = new FloatFormat(d, widthV, widthP);
		Signal signalS = inputOfGen(f.s);
		Signal signalZ = inputOfGen(f.z);
		Signal[] v = inputOfGen(f.v);
		Signal[] p = inputOfGen(f.p);
		
		return new Represention(signalS, p, v, signalZ);
	}
	
	public Represention inputOfEva(int widthV, int widthP) throws Exception {
		FloatFormat f = new FloatFormat(0, widthV, widthP);
		Signal signalS = inputOfEva(false);
		Signal signalZ = inputOfEva(false);
		Signal[] v = inputOfEva(f.v);
		Signal[] p = inputOfEva(f.p);
		
		return new Represention(signalS, p, v, signalZ);
	}

	public boolean outputToGen(Signal out) throws Exception {
		if (out.isPublic())
			return out.v;
		
		Signal lb = Signal.receive(is);
		if (lb.equals(out))
			return false;
		else if (lb.equals(R.xor(out)))
			return true;

		throw new Exception("bad label at final output.");
	}
	
	public boolean[] outputToGen(Signal[] out) throws Exception {
		boolean [] result = new boolean[out.length];
		for(int i = 0; i < result.length; ++i) {
			result[i] = outputToGen(out[i]);
		}
		return result;
	}

	public double outputToGen(Represention gcf) throws Exception {
		boolean s = outputToGen(gcf.s);
		boolean z = outputToGen(gcf.z);
		boolean[] v = outputToGen(gcf.v);
		boolean[] p = outputToGen(gcf.p);
		return new FloatFormat(v, p, s, z).toDouble();
	}
	// public boolean transOutputToEva(BitSet out) throws Exception {
	//
	// }

	private Signal[][] gtt = new Signal[2][2];
	private Signal labelL[] = new Signal[2];
	private Signal labelR[] = new Signal[2];

	private Signal garble(Signal a, Signal b) {
		labelL[0] = a;
		labelL[1] = R.xor(labelL[0]);
		labelR[0] = b;
		labelR[1] = R.xor(labelR[0]);

		int cL = a.getLSB() ? 1 : 0;
		int cR = b.getLSB() ? 1 : 0;

		Signal[] lb = new Signal[2];
		lb[cL & cR] = gb.enc(labelL[cL], labelR[cR], gid, Signal.ZERO);
		lb[1 - (cL & cR)] = R.xor(lb[cL & cR]);

		gtt[0 ^ cL][0 ^ cR] = lb[0];
		gtt[0 ^ cL][1 ^ cR] = lb[0];
		gtt[1 ^ cL][0 ^ cR] = lb[0];
		gtt[1 ^ cL][1 ^ cR] = lb[1];

		if (cL != 0 || cR != 0)
			gtt[0 ^ cL][0 ^ cR] = gb.enc(labelL[0], labelR[0], gid,
					gtt[0 ^ cL][0 ^ cR]);
		if (cL != 0 || cR != 1)
			gtt[0 ^ cL][1 ^ cR] = gb.enc(labelL[0], labelR[1], gid,
					gtt[0 ^ cL][1 ^ cR]);
		if (cL != 1 || cR != 0)
			gtt[1 ^ cL][0 ^ cR] = gb.enc(labelL[1], labelR[0], gid,
					gtt[1 ^ cL][0 ^ cR]);
		if (cL != 1 || cR != 1)
			gtt[1 ^ cL][1 ^ cR] = gb.enc(labelL[1], labelR[1], gid,
					gtt[1 ^ cL][1 ^ cR]);

		// assert(gb.enc(labelL[cL], labelR[cR], gid,
		// gtt[0][0]).equals(Label.ZERO)) : "Garbling problem.";
		return lb[0];
	}

	private void sendGTT() {
		try {
			gtt[0][1].send(os);
			gtt[1][0].send(os);
			gtt[1][1].send(os);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public Signal and(Signal a, Signal b) {
		if (a.isPublic() && b.isPublic())
			return new Signal(a.v && b.v);
		else if (a.isPublic())
			return a.v ? b : new Signal(false);
		else if (b.isPublic())
			return b.v ? a : new Signal(false);
		else {
			Signal ret = garble(a, b);
			sendGTT();
			gid++;
			return ret;
		}
	}

	// public BitSet or(BitSet a, BitSet b) {
	// BitSet zero = new BitSet();
	// if (a.equals(R) || b.equals(R))
	// return R;
	// else
	// return zero;
	// }
	//
	public Signal xor(Signal a, Signal b) {
		if (a.isPublic() && b.isPublic())
			return new Signal(a.v ^ b.v);
		else if (a.isPublic())
			return a.v ? not(b) : new Signal(b);
		else if (b.isPublic())
			return b.v ? not(a) : new Signal(a);
		else {
			return a.xor(b);
		}
	}

	public Signal not(Signal a) {
		if (a.isPublic())
			return new Signal(!a.v);
		else 
			return R.xor(a);
	}
}
