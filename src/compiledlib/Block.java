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
public class Block<t__T, T extends IWritable<T,t__T>> implements IWritable<Block<t__T, T>, t__T> {
	public T data;
	public t__T[] pos;
	public t__T[] id;
	public t__T isDummy;

	public CompEnv<t__T> env;
	public IntegerLib<t__T> intLib;
	public FloatLib<t__T> floatLib;
	private T factoryT;
	private int n;

	public Block(CompEnv<t__T> env, int n, T factoryT) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<t__T>(env);
		this.floatLib = new FloatLib<t__T>(env, 24, 8);
		this.n = n;
		this.factoryT = factoryT;
		this.data = factoryT.newObj(null);
		this.pos = env.inputOfAlice(Utils.fromInt(0, n));
		this.id = env.inputOfAlice(Utils.fromInt(0, n));
		this.isDummy = env.inputOfAlice(false);
	}

	public int numBits() {
		int sum = 0;
		sum += factoryT.numBits();
		sum += pos.length;
		sum += id.length;
		sum += isDummy.length;
		return sum;
	}

	public t__T[] getBits() {
		t__T[] ret = env.newTArray(this.numBits());
		t__T[] tmp_b;
		t__T tmp;
		int now = 0;
		tmp_b = this.data.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = pos;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = id;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp = isDummy;
		ret[now] = tmp;
		now ++;
		return ret;
}

	public Block<t__T, T> newObj(t__T[] data) throws Exception {
		if(data == null) {
			data = env.newTArray(this.numBits());
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		Block<t__T, T> ret = new Block<t__T, T>(env, n, factoryT);
		t__T[] tmp;
		int now = 0;
		tmp = env.newTArray(this.factoryT.numBits());
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.data = ret.factoryT.newObj(tmp);
		ret.pos = env.newTArray(n);
		System.arraycopy(data, now, ret.pos, 0, n);
		now += n;
		ret.id = env.newTArray(n);
		System.arraycopy(data, now, ret.id, 0, n);
		now += n;
		ret.isDummy = data[now];
		now ++;
		return ret;
}

}
