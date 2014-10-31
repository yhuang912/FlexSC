package compiledlib.avltree;
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
public class AVLNode<K extends IWritable<K,Boolean>, V extends IWritable<V,Boolean>> implements IWritable<AVLNode<K, V>, Boolean> {
	public AVLId left;
	public Boolean[] rDepth;
	public AVLId right;
	public V value;
	public K key;
	public Boolean[] lDepth;

	public CompEnv<Boolean> env;
	public IntegerLib<Boolean> intLib;
	public FloatLib<Boolean> floatLib;
	private K factoryK;
	private V factoryV;
	private int m;

	public AVLNode(CompEnv<Boolean> env, int m, K factoryK, V factoryV) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<Boolean>(env);
		this.floatLib = new FloatLib<Boolean>(env, 24, 8);
		this.m = m;
		this.factoryK = factoryK;
		this.factoryV = factoryV;
		this.left = new AVLId(env, m);
		this.rDepth = env.inputOfAlice(Utils.fromInt(0, (m)+(1)));
		this.right = new AVLId(env, m);
		this.value = factoryV.newObj(null);
		this.key = factoryK.newObj(null);
		this.lDepth = env.inputOfAlice(Utils.fromInt(0, (m)+(1)));
	}

	public int numBits() {
		int sum = 0;
		sum += left.numBits();
		sum += rDepth.length;
		sum += right.numBits();
		sum += factoryV.numBits();
		sum += factoryK.numBits();
		sum += lDepth.length;
		return sum;
	}

	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp_b;
		Boolean tmp;
		int now = 0;
		tmp_b = this.left.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = rDepth;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = this.right.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = this.value.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = this.key.getBits();
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = lDepth;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public AVLNode<K, V> newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		AVLNode<K, V> ret = new AVLNode<K, V>(env, m, factoryK, factoryV);
		Boolean[] tmp;
		int now = 0;
		ret.left = new AVLId(env, m);
		tmp = new Boolean[this.left.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.left = ret.left.newObj(tmp);
		ret.rDepth = new Boolean[(m)+(1)];
		System.arraycopy(data, now, ret.rDepth, 0, (m)+(1));
		now += (m)+(1);
		ret.right = new AVLId(env, m);
		tmp = new Boolean[this.right.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.right = ret.right.newObj(tmp);
		tmp = new Boolean[this.factoryV.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.value = ret.factoryV.newObj(tmp);
		tmp = new Boolean[this.factoryK.numBits()];
		System.arraycopy(data, now, tmp, 0, tmp.length);
		now += tmp.length;
		ret.key = ret.factoryK.newObj(tmp);
		ret.lDepth = new Boolean[(m)+(1)];
		System.arraycopy(data, now, ret.lDepth, 0, (m)+(1));
		now += (m)+(1);
		return ret;
}

}
