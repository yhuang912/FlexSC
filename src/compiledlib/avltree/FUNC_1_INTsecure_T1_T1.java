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
abstract public class FUNC_1_INTsecure_T1_T1<K extends IWritable<K,Boolean>> {
	CompEnv<Boolean> env;
	IntegerLib<Boolean> lib;
	K factoryK;

	public FUNC_1_INTsecure_T1_T1(CompEnv<Boolean> env, IntegerLib<Boolean> lib, K factoryK) throws Exception {
		this.env = env;
		this.lib = lib;
		this.factoryK = factoryK;
	}

	public abstract Boolean[] calc(K x0, K x1) throws Exception;
}
