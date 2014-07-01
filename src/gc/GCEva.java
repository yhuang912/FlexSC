package gc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import objects.Float.Representation;
import ot.FakeOTReceiver;
import ot.OTExtReceiver;
import ot.OTReceiver;
import test.Utils;
import circuits.FloatFormat;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Party;

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

		if(Flag.FakeOT)
			rcv = new FakeOTReceiver(is, os);
		else
			rcv = new OTExtReceiver(is, os);
		
		gb = new Garbler();
		gtt[0][0] = GCSignal.ZERO;
	}
	
	public GCEva(InputStream is, OutputStream os, boolean NoOT) throws Exception {
		this.is = is;
		this.os = os;

		rcv = null;
		gb = new Garbler();
		gtt[0][0] = GCSignal.ZERO;
	}


	public GCSignal inputOfAlice(boolean in) throws Exception {
		Flag.sw.startOT();
		GCSignal signal = GCSignal.receive(is);
		Flag.sw.stopOT();
		return signal;
	}

	public GCSignal inputOfBob(boolean in) throws Exception {
		Flag.sw.startOT();
		GCSignal signal = rcv.receive(in);
		Flag.sw.stopOT();
		return signal; 
	}

	public GCSignal[] inputOfBob(boolean[] x) throws Exception {
		Flag.sw.startOT();
		GCSignal[] signal = rcv.receive(x);
		Flag.sw.stopOT();
		return signal;
	}

	public GCSignal[] inputOfAlice(boolean[] x) throws Exception {
		Flag.sw.startOT();
		GCSignal[] result = new GCSignal[x.length];
		for(int i = 0; i < x.length; ++i)
			result[i] = GCSignal.receive(is);
		Flag.sw.stopOT();
		return result;
	}


	public boolean outputToAlice(GCSignal out) throws Exception {
		if (!out.isPublic())
			out.send(os);
		return false;
	}

	public boolean[] outputToAlice(GCSignal[] out) throws Exception {
		boolean [] result = new boolean[out.length];
		
		for(int i = 0; i < result.length; ++i) {
			if (!out[i].isPublic())
				out[i].send(os);
		}
		os.flush();
		
		for(int i = 0; i < result.length; ++i)
			result[i] = false;
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

	private void receiveGTT() {
		try {
			Flag.sw.startGCIO();
			gtt[0][1] = GCSignal.receive(is);
			gtt[1][0] = GCSignal.receive(is);
			gtt[1][1] = GCSignal.receive(is);
			Flag.sw.stopGCIO();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public GCSignal and(GCSignal a, GCSignal b) throws IllegalBlockSizeException, BadPaddingException {
		Flag.sw.startGC();

		GCSignal res;
		if (a.isPublic() && b.isPublic())
			res =  new GCSignal(a.v && b.v);
		else if (a.isPublic())
			res =  a.v ? b : new GCSignal(false);
		else if (b.isPublic())
			res = b.v ? a : new GCSignal(false);
		else {
			receiveGTT();

			int i0 = a.getLSB() ? 1 : 0;
			int i1 = b.getLSB() ? 1 : 0;

			res = gb.dec(a, b, gid, gtt[i0][i1]);
			gid++;
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
		else
			return a.xor(b);
	}

	public GCSignal not(GCSignal a) {
		if (a.isPublic())
			return new GCSignal(!a.v);
		else {
			return new GCSignal(a);
		}
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

	@Override
	public CompEnv<GCSignal> getNewInstance(InputStream in, OutputStream os) throws Exception {
		return new GCEva(in, os, true);
	}


	@Override
	public Party getParty() { 
		return Party.Bob;
	}
	
	@Override
	public void flush() throws IOException {
		os.flush();		
	}
}