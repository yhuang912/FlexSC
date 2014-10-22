package compiledlib.priority_queue;
import java.security.SecureRandom;
import oram.SecureArray;
import oram.CircuitOram;
import flexsc.Mode;
import flexsc.Party;
import flexsc.CompEnv;
import java.util.BitSet;
import circuits.arithmetic.IntegerLib;
import util.Utils;
import gc.regular.GCEva;
import gc.regular.GCGen;
import gc.GCSignal;
import java.util.Arrays;
import java.util.Random;
import flexsc.IWritable;
import flexsc.Comparator;
import java.lang.reflect.Array;
public class KeyValue<T extends IWritable<T,GCSignal>> implements IWritable<KeyValue<T>, GCSignal> {
	public T value;
	public GCSignal[] key;

	private CompEnv<GCSignal> env;
	private IntegerLib<GCSignal> lib;
	private T factoryT;
	private int m;

	public KeyValue(CompEnv<GCSignal> env, IntegerLib<GCSignal> lib, int m, T factoryT) throws Exception {
		this.env = env;
		this.lib = lib;
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

	public GCSignal[] getBits() {
		GCSignal[] ret = new GCSignal[this.numBits()];
		GCSignal[] tmp_b;
		GCSignal tmp;
		int now = 0;
		tmp_b = this.value.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = key;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public KeyValue<T> newObj(GCSignal[] data) throws Exception {
		if(data == null) {
			data = new GCSignal[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		KeyValue<T> ret = new KeyValue<T>(env, lib, m, factoryT);
		GCSignal[] tmp;
		int now = 0;
		tmp = new GCSignal[this.factoryT.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.value = ret.factoryT.newObj(tmp);
		ret.key = new GCSignal[32];
		System.arraycopy(data, now, ret.key, 0, 32);
		now += 32;
		return ret;
}

}
