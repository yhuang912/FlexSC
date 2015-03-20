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
public class PriorityQueueNode<T extends IWritable<T,GCSignal>> implements IWritable<PriorityQueueNode<T>, GCSignal> {
	public NodeId left;
	public KeyValue<T> keyvalue;
	public NodeId right;

	public CompEnv<GCSignal> env;
	public IntegerLib<GCSignal> intLib;
	private T factoryT;
	private int m;

	public PriorityQueueNode(CompEnv<GCSignal> env, IntegerLib<GCSignal> intLib, int m, T factoryT) throws Exception {
		this.env = env;
		this.intLib = intLib;
		this.m = m;
		this.factoryT = factoryT;
		this.left = new NodeId(env, intLib, m);
		this.keyvalue = new KeyValue<T>(env, intLib, m, factoryT);
		this.right = new NodeId(env, intLib, m);
	}

	public int numBits() {
		int sum = 0;
		sum += left.numBits();
		sum += keyvalue.numBits();
		sum += right.numBits();
		return sum;
	}

	public GCSignal[] getBits() {
		GCSignal[] ret = new GCSignal[this.numBits()];
		GCSignal[] tmp_b;
		GCSignal tmp;
		int now = 0;
		tmp_b = this.left.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = this.keyvalue.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = this.right.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public PriorityQueueNode<T> newObj(GCSignal[] data) throws Exception {
		if(data == null) {
			data = new GCSignal[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		PriorityQueueNode<T> ret = new PriorityQueueNode<T>(env, intLib, m, factoryT);
		GCSignal[] tmp;
		int now = 0;
		ret.left = new NodeId(env, intLib, m);
		tmp = new GCSignal[this.left.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.left = ret.left.newObj(tmp);
		ret.keyvalue = new KeyValue<T>(env, intLib, m, factoryT);
		tmp = new GCSignal[this.keyvalue.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.keyvalue = ret.keyvalue.newObj(tmp);
		ret.right = new NodeId(env, intLib, m);
		tmp = new GCSignal[this.right.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.right = ret.right.newObj(tmp);
		return ret;
}

}
