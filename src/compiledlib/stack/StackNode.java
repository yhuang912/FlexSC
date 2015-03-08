package compiledlib.stack;
import java.security.SecureRandom;

import oram.CircuitOram;
import oram.SecureArray;
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
public class StackNode<T extends IWritable<T,GCSignal>> implements IWritable<StackNode<T>, GCSignal> {
	public GCSignal[] next;
	public T data;

	public CompEnv<GCSignal> env;
	public IntegerLib<GCSignal> intLib;
	public FloatLib<GCSignal> floatLib;
	private T factoryT;
	private int m;

	public StackNode(CompEnv<GCSignal> env, int m, T factoryT) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<GCSignal>(env);
		this.floatLib = new FloatLib<GCSignal>(env, 24, 8);
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

	public GCSignal[] getBits() {
		GCSignal[] ret = new GCSignal[this.numBits()];
		GCSignal[] tmp_b;
		GCSignal tmp;
		int now = 0;
		tmp_b = next;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = this.data.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public StackNode<T> newObj(GCSignal[] data) throws Exception {
		if(data == null) {
			data = new GCSignal[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		StackNode<T> ret = new StackNode<T>(env, m, factoryT);
		GCSignal[] tmp;
		int now = 0;
		ret.next = new GCSignal[m];
		System.arraycopy(data, now, ret.next, 0, m);
		now += m;
		tmp = new GCSignal[this.factoryT.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.data = ret.factoryT.newObj(tmp);
		return ret;
}

}
