package gc;

import java.security.*;
import java.io.*;

import circuits.FloatFormat;
import flexsc.Flag;
import objects.Float.Representation;
import ot.*;
import rand.ISAACProvider;
import test.StopWatch;
import test.Utils;

public class GCGen extends GCCompEnv {

	public final GCSignal R;
//	SecureRandom rnd = new SecureRandom();
	static SecureRandom rnd;
	static{
	Security.addProvider(new ISAACProvider ());
	try {
		rnd = SecureRandom.getInstance ("ISAACRandom");
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

	InputStream is;
	OutputStream os;
	OTSender snd;
	Garbler gb;

	long gid = 0;
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
		Flag.sw.startOT();
		GCSignal[] label = genPair();
		label[in ? 1 : 0].send(os);
		os.flush();
		Flag.sw.stopOT();
		return label[0];
	}
	
	public GCSignal inputOfBob(boolean in) throws Exception {
		Flag.sw.startOT();
		GCSignal[] label = genPair();
		snd.send(label);
		Flag.sw.stopOT();
		return label[0];
	}
	
	public GCSignal[] inputOfAlice(boolean[] x) throws Exception {
		Flag.sw.startOT();
		GCSignal[][] pairs = new GCSignal[x.length][2];
		GCSignal[] result = new GCSignal[x.length];
		for(int i = 0; i < x.length; ++i){
			pairs[i] = genPair();
			result[i] = pairs[i][0];
		}
		for(int i = 0; i < x.length; ++i)
			pairs[i][x[i] ? 1 : 0].send(os);
		os.flush();
		Flag.sw.stopOT();
		return result;
	}

	public GCSignal[] inputOfBob(boolean[] x) throws Exception {
		Flag.sw.startOT();
		GCSignal[][] pair = new GCSignal[x.length][2];
		for(int i = 0; i < x.length; ++i)
			pair[i] = genPair();
		snd.send(pair);
		GCSignal[] result = new GCSignal[x.length];
		for(int i = 0; i < x.length; ++i)
			result[i] = pair[i][0];
		Flag.sw.stopOT();
		return result;

	}
	
	boolean gatesRemain = false;
	public boolean outputToAlice(GCSignal out) throws Exception {
		if(gatesRemain){
			gatesRemain = false;
			os.flush();
//			Flag.sw.ands += ands;
//			ands = 0;
		}

		if (out.isPublic())
			return out.v;
		
		GCSignal lb = GCSignal.receive(is);
		if (lb.equals(out))
			return false;
		else if (lb.equals(R.xor(out)))
			return true;

//		return false;
		throw new Exception("bad label at final output.");
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


	private GCSignal[][] gtt = new GCSignal[2][2];
	private GCSignal labelL[] = new GCSignal[2];
	private GCSignal labelR[] = new GCSignal[2];

	private GCSignal garble(GCSignal a, GCSignal b) {
		labelL[0] = a;
		labelL[1] = R.xor(labelL[0]);
		labelR[0] = b;
		labelR[1] = R.xor(labelR[0]);

		int cL = a.getLSB() ? 1 : 0;
		int cR = b.getLSB() ? 1 : 0;

		GCSignal[] lb = new GCSignal[2];
		lb[cL & cR] = gb.enc(labelL[cL], labelR[cR], gid, GCSignal.ZERO);
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
			Flag.sw.startGCIO();
			gtt[0][1].send(os);
			gtt[1][0].send(os);
			gtt[1][1].send(os);
			Flag.sw.stopGCIO();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public GCSignal and(GCSignal a, GCSignal b) {
		++Flag.sw.ands;

		Flag.sw.startGC();
		GCSignal res;
		if (a.isPublic() && b.isPublic())
			res = new GCSignal(a.v && b.v);
		else if (a.isPublic())
			res = a.v ? b : new GCSignal(false);
		else if (b.isPublic())
			res = b.v ? a : new GCSignal(false);
		else {

			GCSignal ret = garble(a, b);
			
			sendGTT();
			gid++;
			gatesRemain = true;
			res = ret;
		}
		Flag.sw.stopGC();
		return res;
	}

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


	
	public Representation<GCSignal> inputOfAliceFloatPoint(double d, int widthV, int widthP) throws Exception {
		FloatFormat f = new FloatFormat(d, widthV, widthP);
		GCSignal signalS = inputOfAlice(f.s);
		GCSignal signalZ = inputOfAlice(f.z);
		GCSignal[] v = inputOfAlice(f.v);
		GCSignal[] p = inputOfAlice(f.p);
		
		return new Representation<GCSignal>(signalS, p, v, signalZ);
	}

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
}
