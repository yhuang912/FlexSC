package compiledlib.stack;
import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.IWritable;
public class StackNode<T extends IWritable<T,Boolean>> implements IWritable<StackNode<T>, Boolean> {
	public Boolean[] next;
	public T data;

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;
	private T factoryT;
	private int m;

	public StackNode(CompEnv<Boolean> env, IntegerLib<Boolean> lib, int m, T factoryT) throws Exception {
		this.env = env;
		this.lib = lib;
		this.m = m;
		this.factoryT = factoryT;
		this.next = env.inputOfAlice(Utils.fromInt(0, m));
		this.data = factoryT.newObj(null);
	}

	public int numBits() {
		int sum = 0;
		sum += next.length;
		sum += factoryT.numBits();
		return sum;
	}

	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp;
		int now = 0;
		tmp = next;
		System.arraycopy(tmp, 0, ret, now, tmp.length);
		now += tmp.length;
		tmp = this.data.getBits();
		System.arraycopy(tmp, 0, ret, now, tmp.length);
		now += tmp.length;
		return ret;
}

	public StackNode<T> newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		StackNode<T> ret = new StackNode<T>(env, lib, m, factoryT);
		Boolean[] tmp;
		int now = 0;
		ret.next = new Boolean[m];
		System.arraycopy(data, now, ret.next, 0, m);
		now += m;
		tmp = new Boolean[this.factoryT.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.data = ret.factoryT.newObj(tmp);
		return ret;
}

}
