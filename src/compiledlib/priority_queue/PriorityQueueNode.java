package compiledlib.priority_queue;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.IWritable;
class PriorityQueueNode<T extends IWritable<T,Boolean>> implements IWritable<PriorityQueueNode<T>, Boolean> {
	NodeId left;
	KeyValue<T> keyvalue;
	NodeId right;

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;
	private T factoryT;

	public PriorityQueueNode(CompEnv<Boolean> env, IntegerLib<Boolean> lib, T factoryT) throws Exception {
		this.env = env;
		this.lib = lib;
		this.factoryT = factoryT;
		this.left = new NodeId(env, lib);
		this.keyvalue = new KeyValue<T>(env, lib, factoryT);
		this.right = new NodeId(env, lib);
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
		Boolean[] tmp;
		int now = 0;
		tmp = this.left.getBits();
		System.arraycopy(tmp, 0, ret, now, tmp.length);
		now += tmp.length;
		tmp = this.keyvalue.getBits();
		System.arraycopy(tmp, 0, ret, now, tmp.length);
		now += tmp.length;
		tmp = this.right.getBits();
		System.arraycopy(tmp, 0, ret, now, tmp.length);
		now += tmp.length;
		return ret;
}

	public PriorityQueueNode<T> newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		PriorityQueueNode<T> ret = new PriorityQueueNode<T>(env, lib, factoryT);
		Boolean[] tmp;
		int now = 0;
		ret.left = new NodeId(env, lib);
		tmp = new Boolean[this.left.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.left = ret.left.newObj(tmp);
		ret.keyvalue = new KeyValue<T>(env, lib, factoryT);
		tmp = new Boolean[this.keyvalue.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.keyvalue = ret.keyvalue.newObj(tmp);
		ret.right = new NodeId(env, lib);
		tmp = new Boolean[this.right.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.right = ret.right.newObj(tmp);
		return ret;
}

}
