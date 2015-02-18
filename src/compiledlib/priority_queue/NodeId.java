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
public class NodeId<t__T> implements IWritable<NodeId<t__T>, t__T> {
	public t__T[] pos;
	public t__T[] id;

	public CompEnv<t__T> env;
	public IntegerLib<t__T> intLib;
	public FloatLib<t__T> floatLib;
	private int m;

	public NodeId(CompEnv<t__T> env, int m) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<t__T>(env);
		this.floatLib = new FloatLib<t__T>(env, 24, 8);
		this.m = m;
		this.pos = intLib.randBools(m);
		this.id = env.inputOfAlice(Utils.fromInt(0, m));
	}

	public int numBits() {
		return ((0)+(m))+(m);
	}
	public t__T[] getBits() {
		t__T[] ret = env.newTArray(this.numBits());
		t__T[] tmp_b;
		t__T tmp;
		int now = 0;
		tmp_b = pos;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = id;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public NodeId<t__T> newObj(t__T[] data) throws Exception {
		if(data == null) {
			data = env.newTArray(this.numBits());
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		NodeId<t__T> ret = new NodeId<t__T>(env, m);
		t__T[] tmp;
		int now = 0;
		ret.pos = env.newTArray(m);
		System.arraycopy(data, now, ret.pos, 0, m);
		now += m;
		ret.id = env.newTArray(m);
		System.arraycopy(data, now, ret.id, 0, m);
		now += m;
		return ret;
}

}
