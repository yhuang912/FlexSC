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
public class Int implements IWritable<Int, Boolean> {
	public Boolean[] val;

	public CompEnv<Boolean> env;
	public IntegerLib<Boolean> intLib;
	private int m;

	public Int(CompEnv<Boolean> env, IntegerLib<Boolean> intLib, int m) throws Exception {
		this.env = env;
		this.intLib = intLib;
		this.m = m;
		this.val = env.inputOfAlice(Utils.fromInt(0, m));
	}

	public int numBits() {
		return (0)+(m);
	}
	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp_b;
		Boolean tmp;
		int now = 0;
		tmp_b = val;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public Int newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		Int ret = new Int(env, intLib, m);
		Boolean[] tmp;
		int now = 0;
		ret.val = new Boolean[m];
		System.arraycopy(data, now, ret.val, 0, m);
		now += m;
		return ret;
}

}
