package compiledlib.priority_queue;
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
public class BoolArray implements IWritable<BoolArray, GCSignal> {
	public GCSignal[] data;

	public CompEnv<GCSignal> env;
	public IntegerLib<GCSignal> intLib;

	public BoolArray(CompEnv<GCSignal> env, IntegerLib<GCSignal> intLib) throws Exception {
		this.env = env;
		this.intLib = intLib;
		this.data = env.inputOfAlice(Utils.fromInt(0, 16));
	}

	public int numBits() {
		return (0)+(16);
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
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		BoolArray ret = new BoolArray(env, intLib);
		GCSignal[] tmp;
		int now = 0;
		ret.data = new GCSignal[16];
		System.arraycopy(data, now, ret.data, 0, 16);
		now += 16;
		return ret;
}

}
