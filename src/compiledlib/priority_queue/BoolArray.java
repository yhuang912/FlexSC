package compiledlib.priority_queue;
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
public class BoolArray implements IWritable<BoolArray, GCSignal> {
	public GCSignal[] data;

	private CompEnv<GCSignal> env;
	private IntegerLib<GCSignal> lib;

	public BoolArray(CompEnv<GCSignal> env, IntegerLib<GCSignal> lib) throws Exception {
		this.env = env;
		this.lib = lib;
		this.data = env.inputOfAlice(Utils.fromInt(0, 32));
	}

	public int numBits() {
		return (0)+(32);
	}
	public GCSignal[] getBits() {
		GCSignal[] ret = new GCSignal[this.numBits()];
		GCSignal[] tmp_b;
		GCSignal tmp;
		int now = 0;
		tmp_b = data;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public BoolArray newObj(GCSignal[] data) throws Exception {
		if(data == null) {
			data = new GCSignal[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		BoolArray ret = new BoolArray(env, lib);
		GCSignal[] tmp;
		int now = 0;
		ret.data = new GCSignal[32];
		System.arraycopy(data, now, ret.data, 0, 32);
		now += 32;
		return ret;
}

}
