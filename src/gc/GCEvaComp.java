package gc;

import java.io.IOException;

import network.Network;
import ot.FakeOTReceiver;
import ot.OTReceiver;
import flexsc.Flag;
import flexsc.Party;

public abstract class GCEvaComp extends GCCompEnv{

	OTReceiver rcv;

	protected long gid = 0;

	public GCEvaComp(Network w) {
		super(w, Party.Bob);

		if (Flag.FakeOT)
			rcv = new FakeOTReceiver(w);
	}

	public GCSignal inputOfAlice(boolean in) {
		Flag.sw.startOT();
		GCSignal signal = GCSignal.receive(w);
		Flag.sw.stopOT();
		return signal;
	}

	public GCSignal inputOfBob(boolean in) {
		Flag.sw.startOT();
		GCSignal signal = null;
		try {
			signal = rcv.receive(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Flag.sw.stopOT();
		return signal;
	}

	public GCSignal[] inputOfBob(boolean[] x) {
		Flag.sw.startOT();
		GCSignal[] signal = null;
		try {

			signal = rcv.receive(x);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Flag.sw.stopOT();
		return signal;
	}

	public GCSignal[] inputOfAlice(boolean[] x) {
		GCSignal[] result = new GCSignal[x.length];
		for (int i = 0; i < x.length; ++i)
			result[i] = GCSignal.receive(w);
		System.out.println("...evq");
		return result;
	}

	public boolean outputToAlice(GCSignal out) {
		if (!out.isPublic())
			out.send(w);
		return false;
	}

	public boolean outputToBob(GCSignal out) {
		if (out.isPublic())
			return out.v;

		GCSignal lb = GCSignal.receive(w);
		if (lb.equals(out))
			return false;
		// else if (lb.equals(R.xor(out)))
		else
			return true;
	}

	public boolean[] outputToAlice(GCSignal[] out) {
		boolean[] result = new boolean[out.length];
		for (int i = 0; i < result.length; ++i) {
			if (!out[i].isPublic())
				out[i].send(w);
		}
		
		w.flush();

		for (int i = 0; i < result.length; ++i)
			result[i] = false;
		return result;
	}

	public boolean[] outputToBob(GCSignal[] out) {
		boolean[] result = new boolean[out.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = outputToBob(out[i]);
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
}
