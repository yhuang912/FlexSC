package gc;

import flexsc.CompEnv;

public abstract class GCCompEnv implements CompEnv<GCSignal> {
	public GCSignal ONE() {
		return new GCSignal(true);
	}
	
	public GCSignal ZERO() {
		return new GCSignal(false);
	}
	
	public GCSignal[] newTArray(int len) {
		return new GCSignal[len];
	}
	
	public GCSignal newT(boolean v) {
		return new GCSignal(v);
	}
}
