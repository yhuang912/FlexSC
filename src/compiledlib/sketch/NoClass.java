package compiledlib.sketch;
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
public class NoClass implements IWritable<NoClass, Boolean> {

	public static CompEnv<Boolean> env;
	public static IntegerLib<Boolean> intLib;
	public static FloatLib<Boolean> floatLib;

	public NoClass(CompEnv<Boolean> env, IntegerLib<Boolean> intLib, FloatLib<Boolean> floatLib) throws Exception {
		this.env = env;
		this.intLib = intLib;
		this.floatLib = floatLib;
	}

	public int numBits() {
		return 0;
	}
	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp_b;
		Boolean tmp;
		int now = 0;
		return ret;
}

	public NoClass newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		NoClass ret = new NoClass(env, intLib, floatLib);
		Boolean[] tmp;
		int now = 0;
		return ret;
}

}
