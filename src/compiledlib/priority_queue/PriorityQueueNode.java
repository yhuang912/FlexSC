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
public class PriorityQueueNode<T extends IWritable<T,Boolean>> implements IWritable<PriorityQueueNode<T>, Boolean> {
	public NodeId left;
	public KeyValue<T> keyvalue;
	public NodeId right;

	public CompEnv<Boolean> env;
	public IntegerLib<Boolean> intLib;
	public FloatLib<Boolean> floatLib;
	private T factoryT;
	private int m;

	public PriorityQueueNode(CompEnv<Boolean> env, int m, T factoryT) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<Boolean>(env);
		this.floatLib = new FloatLib<Boolean>(env, 24, 8);
		this.m = m;
		this.factoryT = factoryT;
		this.left = new NodeId(env, m);
		this.keyvalue = new KeyValue<T>(env, m, factoryT);
		this.right = new NodeId(env, m);
	}

	public int numBits() {
		int sum = 0;
		sum += left.numBits();
		sum += keyvalue.numBits();
		sum += right.numBits();
		return sum;
	}

	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp_b;
		Boolean tmp;
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

	public PriorityQueueNode<T> newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		PriorityQueueNode<T> ret = new PriorityQueueNode<T>(env, m, factoryT);
		Boolean[] tmp;
		int now = 0;
		ret.left = new NodeId(env, m);
		tmp = new Boolean[this.left.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.left = ret.left.newObj(tmp);
		ret.keyvalue = new KeyValue<T>(env, m, factoryT);
		tmp = new Boolean[this.keyvalue.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.keyvalue = ret.keyvalue.newObj(tmp);
		ret.right = new NodeId(env, m);
		tmp = new Boolean[this.right.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.right = ret.right.newObj(tmp);
		return ret;
}

}
