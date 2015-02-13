package compiledlib.avltree;
import java.security.SecureRandom;

import oram.CircuitOram;
import oram.SecureArray;
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
abstract public class FUNC_0_INTsecure_RECORDBoolArray_RECORDBoolArray {
	CompEnv<Boolean> env;
	IntegerLib<Boolean> intLib;
	FloatLib<Boolean> floatLib;

	public FUNC_0_INTsecure_RECORDBoolArray_RECORDBoolArray(CompEnv<Boolean> env) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<Boolean>(env);
		this.floatLib = new FloatLib<Boolean>(env, 24, 8);
	}

	public abstract Boolean[] calc(BoolArray x0, BoolArray x1) throws Exception;
}
