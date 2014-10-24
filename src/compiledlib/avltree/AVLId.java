package compiledlib.avltree;
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
public class AVLId implements IWritable<AVLId, Boolean> {
	public Boolean[] pos;
	public Boolean[] id;

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;
	private int m;

	public AVLId(CompEnv<Boolean> env, IntegerLib<Boolean> lib, int m) throws Exception {
		this.env = env;
		this.lib = lib;
		this.m = m;
		this.pos = env.inputOfAlice(Utils.fromInt(0, m));
		this.id = env.inputOfAlice(Utils.fromInt(0, m));
	}

	public int numBits() {
		return ((0)+(m))+(m);
	}
	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp_b;
		Boolean tmp;
		int now = 0;
		tmp_b = pos;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		tmp_b = id;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public AVLId newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		AVLId ret = new AVLId(env, lib, m);
		Boolean[] tmp;
		int now = 0;
		ret.pos = new Boolean[m];
		System.arraycopy(data, now, ret.pos, 0, m);
		now += m;
		ret.id = new Boolean[m];
		System.arraycopy(data, now, ret.id, 0, m);
		now += m;
		return ret;
}

}
