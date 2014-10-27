package compiledlib.priority_queue;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.IWritable;
public class PriorityQueueNode<T extends IWritable<T,Boolean>> implements IWritable<PriorityQueueNode<T>, Boolean> {
	public NodeId left;
	public KeyValue<T> keyvalue;
	public NodeId right;

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;
	private T factoryT;
	private int m;

	public PriorityQueueNode(CompEnv<Boolean> env, IntegerLib<Boolean> lib, int m, T factoryT) throws Exception {
		this.env = env;
		this.lib = lib;
		this.m = m;
		this.factoryT = factoryT;
		this.left = new NodeId(env, lib, m);
		this.keyvalue = new KeyValue<T>(env, lib, m, factoryT);
		this.right = new NodeId(env, lib, m);
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
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		PriorityQueueNode<T> ret = new PriorityQueueNode<T>(env, lib, m, factoryT);
		Boolean[] tmp;
		int now = 0;
		ret.left = new NodeId(env, lib, m);
		tmp = new Boolean[this.left.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.left = ret.left.newObj(tmp);
		ret.keyvalue = new KeyValue<T>(env, lib, m, factoryT);
		tmp = new Boolean[this.keyvalue.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.keyvalue = ret.keyvalue.newObj(tmp);
		ret.right = new NodeId(env, lib, m);
		tmp = new Boolean[this.right.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.right = ret.right.newObj(tmp);
		return ret;
}

}
