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
public class KeyValue<T extends IWritable<T,Boolean>> implements IWritable<KeyValue<T>, Boolean> {
	public T value;
	public Boolean[] key;

	public CompEnv<Boolean> env;
	public IntegerLib<Boolean> intLib;
	private T factoryT;
	private int m;

	public KeyValue(CompEnv<Boolean> env, IntegerLib<Boolean> intLib, int m, T factoryT) throws Exception {
		this.env = env;
		this.intLib = intLib;
		this.m = m;
		this.factoryT = factoryT;
		this.value = factoryT.newObj(null);
		this.key = env.inputOfAlice(Utils.fromInt(0, 16));
	}

	public int numBits() {
		int sum = 0;
		sum += factoryT.numBits();
		sum += key.length;
		return sum;
	}

	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp_b;
		Boolean tmp;
		int now = 0;
		tmp_b = this.value.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = key;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public KeyValue<T> newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		KeyValue<T> ret = new KeyValue<T>(env, intLib, m, factoryT);
		Boolean[] tmp;
		int now = 0;
		tmp = new Boolean[this.factoryT.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.value = ret.factoryT.newObj(tmp);
		ret.key = new Boolean[16];
		System.arraycopy(data, now, ret.key, 0, 16);
		now += 16;
		return ret;
}

}
