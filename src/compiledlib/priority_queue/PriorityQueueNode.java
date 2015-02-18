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
public class PriorityQueueNode<t__T, T extends IWritable<T,t__T>> implements IWritable<PriorityQueueNode<t__T, T>, t__T> {
	public NodeId<t__T> left;
	public KeyValue<t__T, T> keyvalue;
	public NodeId<t__T> right;

	public CompEnv<t__T> env;
	public IntegerLib<t__T> intLib;
	public FloatLib<t__T> floatLib;
	private T factoryT;
	private int m;

	public PriorityQueueNode(CompEnv<t__T> env, int m, T factoryT) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<t__T>(env);
		this.floatLib = new FloatLib<t__T>(env, 24, 8);
		this.m = m;
		this.factoryT = factoryT;
		this.left = new NodeId<t__T>(env, m);
		this.keyvalue = new KeyValue<t__T, T>(env, m, factoryT);
		this.right = new NodeId<t__T>(env, m);
	}

	public int numBits() {
		int sum = 0;
		sum += left.numBits();
		sum += keyvalue.numBits();
		sum += right.numBits();
		return sum;
	}

	public t__T[] getBits() {
		t__T[] ret = env.newTArray(this.numBits());
		t__T[] tmp_b;
		t__T tmp;
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

	public PriorityQueueNode<t__T, T> newObj(t__T[] data) throws Exception {
		if(data == null) {
			data = env.newTArray(this.numBits());
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		PriorityQueueNode<t__T, T> ret = new PriorityQueueNode<t__T, T>(env, m, factoryT);
		t__T[] tmp;
		int now = 0;
		ret.left = new NodeId<t__T>(env, m);
		tmp = env.newTArray(this.left.numBits());
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.left = ret.left.newObj(tmp);
		ret.keyvalue = new KeyValue<t__T, T>(env, m, factoryT);
		tmp = env.newTArray(this.keyvalue.numBits());
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.keyvalue = ret.keyvalue.newObj(tmp);
		ret.right = new NodeId<t__T>(env, m);
		tmp = env.newTArray(this.right.numBits());
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.right = ret.right.newObj(tmp);
		return ret;
}

}
