package compiledlib.priority_queue;
import java.security.SecureRandom;
import oram.SecureArray;
import oram.CircuitOram;
import flexsc.Mode;
import flexsc.Party;
import flexsc.CompEnv;
import java.util.BitSet;
import circuits.arithmetic.IntegerLib;
import circuits.arithmetic.FloatLib;
import util.Utils;
import gc.regular.GCEva;
import gc.regular.GCGen;
import gc.GCSignal;
import java.util.Arrays;
import java.util.Random;
import flexsc.IWritable;
import flexsc.Comparator;
import java.lang.reflect.Array;
public class NodeId implements IWritable<NodeId, GCSignal> {
	public GCSignal[] pos;
	public GCSignal[] id;

	public CompEnv<GCSignal> env;
	public IntegerLib<GCSignal> intLib;
	private int m;

	public NodeId(CompEnv<GCSignal> env, IntegerLib<GCSignal> intLib, int m) throws Exception {
		this.env = env;
		this.intLib = intLib;
		this.m = m;
		this.pos = intLib.randBools(m);
		this.id = env.inputOfAlice(Utils.fromInt(0, m));
	}

	public int numBits() {
		return ((0)+(m))+(m);
	}
	public GCSignal[] getBits() {
		GCSignal[] ret = new GCSignal[this.numBits()];
		GCSignal[] tmp_b;
		GCSignal tmp;
		int now = 0;
		tmp_b = pos;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = id;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public NodeId newObj(GCSignal[] data) throws Exception {
		if(data == null) {
			data = new GCSignal[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		NodeId ret = new NodeId(env, intLib, m);
		GCSignal[] tmp;
		int now = 0;
		ret.pos = new GCSignal[m];
		System.arraycopy(data, now, ret.pos, 0, m);
		now += m;
		ret.id = new GCSignal[m];
		System.arraycopy(data, now, ret.id, 0, m);
		now += m;
		return ret;
}

}
