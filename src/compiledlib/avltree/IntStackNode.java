package compiledlib.avltree;
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
public class IntStackNode implements IWritable<IntStackNode, Boolean> {
	public Boolean[] next;
	public Boolean[] data;

	public CompEnv<Boolean> env;
	public IntegerLib<Boolean> intLib;
	public FloatLib<Boolean> floatLib;
	private int m;

	public IntStackNode(CompEnv<Boolean> env, int m) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<Boolean>(env);
		this.floatLib = new FloatLib<Boolean>(env, 24, 8);
		this.m = m;
		this.next = intLib.randBools(m);
		this.data = env.inputOfAlice(Utils.fromInt(0, m));
	}

	public int numBits() {
		return ((0)+(m))+(m);
	}
	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp_b;
		Boolean tmp;
		int now = 0;
		tmp_b = next;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = data;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public IntStackNode newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		IntStackNode ret = new IntStackNode(env, m);
		Boolean[] tmp;
		int now = 0;
		ret.next = new Boolean[m];
		System.arraycopy(data, now, ret.next, 0, m);
		now += m;
		ret.data = new Boolean[m];
		System.arraycopy(data, now, ret.data, 0, m);
		now += m;
		return ret;
}

}