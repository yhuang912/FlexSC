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
public class NoClass implements IWritable<NoClass, GCSignal> {

	public static CompEnv<GCSignal> env;
	public static IntegerLib<GCSignal> lib;

	public NoClass(CompEnv<GCSignal> env, IntegerLib<GCSignal> lib) throws Exception {
		this.env = env;
		this.lib = lib;
	}

	public int numBits() {
		return 0;
	}
	public GCSignal[] getBits() {
		GCSignal[] ret = new GCSignal[this.numBits()];
		GCSignal[] tmp_b;
		GCSignal tmp;
		int now = 0;
		return ret;
}

	public NoClass newObj(GCSignal[] data) throws Exception {
		if(data == null) {
			data = new GCSignal[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		NoClass ret = new NoClass(env, lib);
		GCSignal[] tmp;
		int now = 0;
		return ret;
}

}
