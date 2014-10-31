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
abstract public class FUNC_1_INTsecure_T1_T1<K extends IWritable<K,Boolean>> {
	CompEnv<Boolean> env;
	IntegerLib<Boolean> intLib;
	FloatLib<Boolean> floatLib;
	K factoryK;

	public FUNC_1_INTsecure_T1_T1(CompEnv<Boolean> env, K factoryK) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<Boolean>(env);
		this.floatLib = new FloatLib<Boolean>(env, 24, 8);
		this.factoryK = factoryK;
	}

	public abstract Boolean[] calc(K x0, K x1) throws Exception;
}
