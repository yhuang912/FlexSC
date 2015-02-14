package gc;

import java.io.IOException;

import network.Network;
import ot.FakeOTSender;
import ot.OTExtSender;
import ot.OTPreprocessSender;
import ot.OTSender;
import flexsc.CompEnv;
import flexsc.Flag;
import flexsc.Mode;
import flexsc.Party;

public abstract class GCGenComp extends GCCompEnv{

	static public GCSignal R = null;
	static {
		R = GCSignal.freshLabel(CompEnv.rnd);
		R.setLSB();
	}

	OTSender snd;
	protected long gid = 0;

	public GCGenComp(Network w) {
		super(w, Party.Alice);

		if (Flag.FakeOT)
			snd = new FakeOTSender(80, w);
//		else if(Flag.PreProcessOT)
//			snd = new OTPreprocessSender(80, this);
//		else
//			snd = new OTExtSender(80, this);
	}

	public static GCSignal[] genPairForLabel() {
		GCSignal[] label = new GCSignal[2];
		if(Flag.mode != Mode.OFFLINE || !Flag.offline)
			label[0] = GCSignal.freshLabel(rnd);
		if(Flag.mode == Mode.OFFLINE) {
			if(Flag.offline) {
				label[0] = GCSignal.receive(gc.offline.GCGen.fin);
			}
			else 
				label[0].send(gc.offline.GCGen.fout);
		}
		label[1] = R.xor(label[0]);
		return label;
	}
	
	public static GCSignal[] genPair() {
		GCSignal[] label = new GCSignal[2];
		label[0] = GCSignal.freshLabel(rnd);
		label[1] = R.xor(label[0]);
		return label;
	}

	public GCSignal inputOfAlice(boolean in) {
		Flag.sw.startOT();
		GCSignal[] label = genPairForLabel();
		Flag.sw.startOTIO();
		label[in ? 1 : 0].send(w);
		flush();
		Flag.sw.stopOTIO();
		Flag.sw.stopOT();
		return label[0];
	}

	public GCSignal inputOfBob(boolean in) {
		Flag.sw.startOT();
		GCSignal[] label = genPairForLabel();
		try {
			snd.send(label);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Flag.sw.stopOT();
		return label[0];
	}

	public GCSignal[] inputOfAlice(boolean[] x) {
		Flag.sw.startOT();
		GCSignal[][] pairs = new GCSignal[x.length][2];
		GCSignal[] result = new GCSignal[x.length];
		for (int i = 0; i < x.length; ++i) {
			pairs[i] = genPairForLabel();
			result[i] = pairs[i][0];
		}
		Flag.sw.startOTIO();
		for (int i = 0; i < x.length; ++i)
			pairs[i][x[i] ? 1 : 0].send(w);
		flush();
		Flag.sw.stopOTIO();
		Flag.sw.stopOT();
		return result;
	}

	public GCSignal[] inputOfBob(boolean[] x) {
		Flag.sw.startOT();
		GCSignal[][] pair = new GCSignal[x.length][2];
		for (int i = 0; i < x.length; ++i)
			pair[i] = genPairForLabel();
		try {
			snd.send(pair);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GCSignal[] result = new GCSignal[x.length];
		for (int i = 0; i < x.length; ++i)
			result[i] = pair[i][0];
		Flag.sw.stopOT();
		return result;
	}

	protected boolean gatesRemain = false;

	public boolean outputToAlice(GCSignal out) {
		if (gatesRemain) {
			gatesRemain = false;
			flush();
		}
		if (out.isPublic())
			return out.v;

		GCSignal lb = GCSignal.receive(w);
		if (lb.equals(out))
			return false;
		else if (lb.equals(R.xor(out)))
			return true;

		try {
			throw new Exception("bad label at final output.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean outputToBob(GCSignal out) {
		if (!out.isPublic())
			out.send(w);
		return false;
	}

	public boolean[] outputToBob(GCSignal[] out) {
		boolean[] result = new boolean[out.length];

		for (int i = 0; i < result.length; ++i) {
			if (!out[i].isPublic())
				out[i].send(w);
		}
		flush();

		for (int i = 0; i < result.length; ++i)
			result[i] = false;
		return result;
	}

	public boolean[] outputToAlice(GCSignal[] out) {
		boolean[] result = new boolean[out.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = outputToAlice(out[i]);
		}
		return result;
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
}
