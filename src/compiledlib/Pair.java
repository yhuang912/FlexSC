package compiledlib;
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
public class Pair<t__T, V1 extends IWritable<V1,t__T>, V2 extends IWritable<V2,t__T>> implements IWritable<Pair<t__T, V1, V2>, t__T> {
	public V1 first;
	public V2 second;

	public CompEnv<t__T> env;
	public IntegerLib<t__T> intLib;
	public FloatLib<t__T> floatLib;
	private V1 factoryV1;
	private V2 factoryV2;

	public Pair(CompEnv<t__T> env, V1 factoryV1, V2 factoryV2) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<t__T>(env);
		this.floatLib = new FloatLib<t__T>(env, 24, 8);
		this.factoryV1 = factoryV1;
		this.factoryV2 = factoryV2;
		this.first = factoryV1.newObj(null);
		this.second = factoryV2.newObj(null);
	}

	public int numBits() {
		int sum = 0;
		sum += factoryV1.numBits();
		sum += factoryV2.numBits();
		return sum;
	}

	public t__T[] getBits() {
		t__T[] ret = env.newTArray(this.numBits());
		t__T[] tmp_b;
		t__T tmp;
		int now = 0;
		tmp_b = this.first.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = this.second.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public Pair<t__T, V1, V2> newObj(t__T[] data) throws Exception {
		if(data == null) {
			data = env.newTArray(this.numBits());
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		Pair<t__T, V1, V2> ret = new Pair<t__T, V1, V2>(env, factoryV1, factoryV2);
		t__T[] tmp;
		int now = 0;
		tmp = env.newTArray(this.factoryV1.numBits());
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.first = ret.factoryV1.newObj(tmp);
		tmp = env.newTArray(this.factoryV2.numBits());
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.second = ret.factoryV2.newObj(tmp);
		return ret;
}

}
