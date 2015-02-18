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
public class KeyValue<t__T, T extends IWritable<T,t__T>> implements IWritable<KeyValue<t__T, T>, t__T> {
	public T value;
	public t__T[] key;

	public CompEnv<t__T> env;
	public IntegerLib<t__T> intLib;
	public FloatLib<t__T> floatLib;
	private T factoryT;
	private int m;

	public KeyValue(CompEnv<t__T> env, int m, T factoryT) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<t__T>(env);
		this.floatLib = new FloatLib<t__T>(env, 24, 8);
		this.m = m;
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

	public t__T[] getBits() {
		t__T[] ret = env.newTArray(this.numBits());
		t__T[] tmp_b;
		t__T tmp;
		int now = 0;
		tmp_b = this.value.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = key;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public KeyValue<t__T, T> newObj(t__T[] data) throws Exception {
		if(data == null) {
			data = env.newTArray(this.numBits());
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		KeyValue<t__T, T> ret = new KeyValue<t__T, T>(env, m, factoryT);
		t__T[] tmp;
		int now = 0;
		tmp = env.newTArray(this.factoryT.numBits());
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.value = ret.factoryT.newObj(tmp);
		ret.key = env.newTArray(32);
		System.arraycopy(data, now, ret.key, 0, 32);
		now += 32;
		return ret;
}

}
