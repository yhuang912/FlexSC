package compiledlib.tmp;
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
public class StackNode<t__T, T extends IWritable<T,t__T>> implements IWritable<StackNode<t__T, T>, t__T> {
	public Pointer<t__T> next;
	public T data;

	public CompEnv<t__T> env;
	public IntegerLib<t__T> intLib;
	public FloatLib<t__T> floatLib;
	private T factoryT;
	private int m;

	public StackNode(CompEnv<t__T> env, int m, T factoryT) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<t__T>(env);
		this.floatLib = new FloatLib<t__T>(env, 24, 8);
		this.m = m;
		this.factoryT = factoryT;
		this.next = new Pointer<t__T>(env, m);
		this.data = factoryT.newObj(null);
	}

	public int numBits() {
		int sum = 0;
		sum += next.numBits();
		sum += factoryT.numBits();
		return sum;
	}

	public t__T[] getBits() {
		t__T[] ret = env.newTArray(this.numBits());
		t__T[] tmp_b;
		t__T tmp;
		int now = 0;
		tmp_b = this.next.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = this.data.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public StackNode<t__T, T> newObj(t__T[] data) throws Exception {
		if(data == null) {
			data = env.newTArray(this.numBits());
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		StackNode<t__T, T> ret = new StackNode<t__T, T>(env, m, factoryT);
		t__T[] tmp;
		int now = 0;
		ret.next = new Pointer<t__T>(env, m);
		tmp = env.newTArray(this.next.numBits());
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.next = ret.next.newObj(tmp);
		tmp = env.newTArray(this.factoryT.numBits());
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.data = ret.factoryT.newObj(tmp);
		return ret;
}

}
