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
public class BoolArray implements IWritable<BoolArray, Boolean> {
	public Boolean[] data;

	public CompEnv<Boolean> env;
	public IntegerLib<Boolean> intLib;

	public BoolArray(CompEnv<Boolean> env, IntegerLib<Boolean> intLib) throws Exception {
		this.env = env;
		this.intLib = intLib;
		this.data = env.inputOfAlice(Utils.fromInt(0, 16));
	}

	public int numBits() {
		return (0)+(16);
	}
	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp_b;
		Boolean tmp;
		int now = 0;
		tmp_b = data;
		System.arraycopy(tmp_b, 0, ret, now, tmp_b.length);
		now += tmp_b.length;
		return ret;
}

	public BoolArray newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		BoolArray ret = new BoolArray(env, intLib);
		Boolean[] tmp;
		int now = 0;
		ret.data = new Boolean[16];
		System.arraycopy(data, now, ret.data, 0, 16);
		now += 16;
		return ret;
}

}
