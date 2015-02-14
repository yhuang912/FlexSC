package gc;

import network.Network;
import flexsc.CompEnv;
import flexsc.Mode;
import flexsc.Party;

public abstract class GCCompEnv extends CompEnv<GCSignal> {
	public GCCompEnv(Network w, Party p) {
		super(w, p, Mode.OPT);
	}

	public GCSignal ONE() {
		return new GCSignal(true);
	}
	
	public GCSignal ZERO() {
		return new GCSignal(false);
	}
	
	public GCSignal[] newTArray(int len) {
		return new GCSignal[len];
	}
	
	public GCSignal[][] newTArray(int d1, int d2) {
		return new GCSignal[d1][d2];
	}
	
	public GCSignal[][][] newTArray(int d1, int d2, int d3) {
		return new GCSignal[d1][d2][d3];
	}
	
	public GCSignal newT(boolean v) {
		return new GCSignal(v);
	}
}
