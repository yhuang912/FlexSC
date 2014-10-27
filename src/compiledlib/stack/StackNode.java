package compiledlib.stack;
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
public class StackNode<T extends IWritable<T,Boolean>> implements IWritable<StackNode<T>, Boolean> {
	public Boolean[] next;
	public T data;

	public CompEnv<Boolean> env;
	public IntegerLib<Boolean> intLib;
	private T factoryT;
	private int m;

	public StackNode(CompEnv<Boolean> env, IntegerLib<Boolean> intLib, int m, T factoryT) throws Exception {
		this.env = env;
		this.intLib = intLib;
		this.m = m;
		this.factoryT = factoryT;
		this.next = intLib.randBools(m);
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
		Boolean[] tmp_b;
		Boolean tmp;
		int now = 0;
		tmp_b = next;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = this.data.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public StackNode<T> newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		StackNode<T> ret = new StackNode<T>(env, intLib, m, factoryT);
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
