package compiledlib.queue;
import java.security.SecureRandom;

import oram.CircuitOram;
import oram.SecureArray;
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
public class QueueNode<T extends IWritable<T,GCSignal>> implements IWritable<QueueNode<T>, GCSignal> {
	public GCSignal[] next;
	public T data;

	private CompEnv<GCSignal> env;
	private IntegerLib<GCSignal> lib;
	private T factoryT;
	private int m;

	public QueueNode(CompEnv<GCSignal> env, IntegerLib<GCSignal> lib, int m, T factoryT) throws Exception {
		this.env = env;
		this.lib = lib;
		this.m = m;
		this.factoryT = factoryT;
		this.next = env.inputOfAlice(Utils.fromInt(0, m));
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

	public QueueNode<T> newObj(GCSignal[] data) throws Exception {
		if(data == null) {
			data = new GCSignal[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		QueueNode<T> ret = new QueueNode<T>(env, lib, m, factoryT);
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
