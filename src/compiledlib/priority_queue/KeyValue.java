package compiledlib.priority_queue;
import util.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.IWritable;
class KeyValue<T extends IWritable<T,Boolean>> implements IWritable<KeyValue<T>, Boolean> {
	T value;
	Boolean[] key;

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;
	private T factoryT;

	public KeyValue(CompEnv<Boolean> env, IntegerLib<Boolean> lib, T factoryT) throws Exception {
		this.env = env;
		this.lib = lib;
		this.factoryT = factoryT;
		this.value = factoryT.newObj(null);
		this.key = env.inputOfAlice(Utils.fromInt(0, 32));
	}

	public int numBits() {
		int sum = 0;
		sum += factoryT.numBits();
		sum += key.length;
		return sum;
	}

	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp;
		int now = 0;
		tmp = this.value.getBits();
		System.arraycopy(tmp, 0, ret, now, tmp.length);
		now += tmp.length;
		tmp = key;
		System.arraycopy(tmp, 0, ret, now, tmp.length);
		now += tmp.length;
		return ret;
}

	public KeyValue<T> newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		KeyValue<T> ret = new KeyValue<T>(env, lib, factoryT);
		Boolean[] tmp;
		int now = 0;
		tmp = new Boolean[this.factoryT.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.value = ret.factoryT.newObj(tmp);
		ret.key = new Boolean[32];
		System.arraycopy(data, now, ret.key, 0, 32);
		now += 32;
		return ret;
}

}
