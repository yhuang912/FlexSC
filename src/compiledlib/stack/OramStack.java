package compiledlib.stack;
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
public class OramStack<T extends IWritable<T,GCSignal>> {
	public GCSignal[] size;
	public SecureArray<GCSignal> data;

	public CompEnv<GCSignal> env;
	public IntegerLib<GCSignal> intLib;
	public FloatLib<GCSignal> floatLib;
	private T factoryT;

	public OramStack(CompEnv<GCSignal> env, T factoryT, SecureArray<GCSignal> data) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<GCSignal>(env);
		this.floatLib = new FloatLib<GCSignal>(env, 24, 8);
		this.factoryT = factoryT;
		this.size = env.inputOfAlice(Utils.fromInt(0, 32));
		this.data = data;
	}

	public T stack_op(T operand, GCSignal op) throws Exception {
		T ret = factoryT.newObj(null);
		boolean f_tmp_0 = true;
		boolean __tmp0 = f_tmp_0;
		GCSignal f_tmp_2 = env.inputOfAlice(__tmp0);
		GCSignal f_tmp_1 = intLib.eq(op, f_tmp_2);
		GCSignal __tmp1 = f_tmp_1;
		SecureArray<GCSignal> f_tmp_3 = this.data;
		SecureArray<GCSignal> __tmp2 = f_tmp_3;
		GCSignal[] f_tmp_4 = this.size;
		GCSignal[] __tmp3 = f_tmp_4;
		T f_tmp_5 = factoryT.newObj(__tmp2.read(__tmp3));
		T __tmp4 = f_tmp_5;
		T f_tmp_6 = this.factoryT.newObj(intLib.mux(ret.getBits(), __tmp4.getBits(),__tmp1));
		T __tmp5 = f_tmp_6;
		ret = __tmp5;
		GCSignal[] f_tmp_7 = this.size;
		GCSignal[] __tmp6 = f_tmp_7;
		int f_tmp_8 = 1;
		int __tmp7 = f_tmp_8;
		GCSignal[] f_tmp_10 = env.inputOfAlice(Utils.fromInt(__tmp7, 32));
		GCSignal[] f_tmp_9 = intLib.sub(__tmp6,f_tmp_10);
		GCSignal[] __tmp8 = f_tmp_9;
		GCSignal[] f_tmp_11 = this.size;
		GCSignal[] __tmp9 = f_tmp_11;
		GCSignal[] f_tmp_12 = intLib.mux(__tmp9, __tmp8,__tmp1);
		GCSignal[] __tmp10 = f_tmp_12;
		this.size = __tmp10;
		GCSignal f_tmp_13 = intLib.not(__tmp1);
		GCSignal __tmp11 = f_tmp_13;
		GCSignal[] f_tmp_14 = this.size;
		GCSignal[] __tmp12 = f_tmp_14;
		int f_tmp_15 = 1;
		int __tmp13 = f_tmp_15;
		GCSignal[] f_tmp_17 = env.inputOfAlice(Utils.fromInt(__tmp13, 32));
		GCSignal[] f_tmp_16 = intLib.add(__tmp12,f_tmp_17);
		GCSignal[] __tmp14 = f_tmp_16;
		GCSignal[] f_tmp_18 = this.size;
		GCSignal[] __tmp15 = f_tmp_18;
		GCSignal[] f_tmp_19 = intLib.mux(__tmp15, __tmp14,__tmp11);
		GCSignal[] __tmp16 = f_tmp_19;
		this.size = __tmp16;
		SecureArray<GCSignal> f_tmp_20 = this.data;
		SecureArray<GCSignal> __tmp17 = f_tmp_20;
		GCSignal[] f_tmp_21 = this.size;
		GCSignal[] __tmp18 = f_tmp_21;
		T f_tmp_22 = factoryT.newObj(__tmp17.read(__tmp18));
		T __tmp19 = f_tmp_22;
		T f_tmp_23 = this.factoryT.newObj(intLib.mux(__tmp19.getBits(), operand.getBits(),__tmp11));
		T __tmp20 = f_tmp_23;
		__tmp17.write(__tmp18,__tmp20.getBits());
		return ret;
	}
}
