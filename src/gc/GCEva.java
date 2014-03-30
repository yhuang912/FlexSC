package gc;

import objects.Float.Represention;

import java.io.*;

import circuits.FloatFormat;
import flexsc.CompEnv;
import ot.*;

public class GCEva implements CompEnv<Signal> {
	InputStream is;
	OutputStream os;
	OTReceiver rcv;
	Garbler gb;
	public int nonFreeGate = 0;
	long gid = 0;

	public GCEva(InputStream is, OutputStream os) throws Exception {
		this.is = is;
		this.os = os;

		rcv = new NPOTReceiver(is, os);
//		rcv = new FakeOTReceiver(is, os);
		gb = new Garbler();
	}

	public Signal inputOfGen(boolean in) throws Exception {
		return Signal.receive(is);
	}

	public Signal inputOfEva(boolean in) throws Exception {
		return rcv.receive(in);
	}

	public Signal[] inputOfEva(boolean[] x) throws Exception {
		Signal[] result = new Signal[x.length];
		for(int i = 0; i < x.length; ++i)
			result[i] = inputOfEva(x[i]);
		return result;
	}

	public Signal[] inputOfGen(boolean[] x) throws Exception {
		Signal[] result = new Signal[x.length];
		for(int i = 0; i < x.length; ++i)
			result[i] = inputOfGen(false);
		return result;
	}
	
	public Represention inputOfEva(double d, int widthV, int widthP) throws Exception {
		FloatFormat f = new FloatFormat(d, widthV, widthP);
		Signal signalS = inputOfEva(f.s);
		Signal signalZ = inputOfEva(f.z);
		Signal[] v = inputOfEva(f.v);
		Signal[] p = inputOfEva(f.p);
		return new Represention(signalS, p, v, signalZ);
	}
	
	public Represention inputOfGen(int widthV, int widthP) throws Exception {
		FloatFormat f = new FloatFormat(0, widthV, widthP);
		Signal signalS = inputOfGen(f.s);
		Signal signalZ = inputOfGen(f.z);
		Signal[] v = inputOfGen(f.v);
		Signal[] p = inputOfGen(f.p);
		
		return new Represention(signalS, p, v, signalZ);
	}
	
	public boolean outputToGen(Signal out) throws Exception {
		if (!out.isPublic())
			out.send(os);
		return false;
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

	
	// public boolean transOutputToEva(Label out) throws Exception {
	//
	// }

	private Signal[][] gtt = new Signal[2][2];

	private void receiveGTT() {
		try {
			gtt[0][0] = Signal.ZERO;
			gtt[0][1] = Signal.receive(is);
			gtt[1][0] = Signal.receive(is);
			gtt[1][1] = Signal.receive(is);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public Signal and(Signal a, Signal b) {
		++nonFreeGate;
		if (a.isPublic() && b.isPublic())
			return new Signal(a.v && b.v);
		else if (a.isPublic())
			return a.v ? b : new Signal(false);
		else if (b.isPublic())
			return b.v ? a : new Signal(false);
		else {
			receiveGTT();

			int i0 = a.getLSB() ? 1 : 0;
			int i1 = b.getLSB() ? 1 : 0;

			Signal out = gb.dec(a, b, gid, gtt[i0][i1]);
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
		else {
			return new Signal(a);
		}
	}
}