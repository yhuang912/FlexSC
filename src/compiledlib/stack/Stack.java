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
public class Stack<T extends IWritable<T,GCSignal>> {
	public GCSignal[] size;
	public GCSignal[] root;
	public CircuitOram<GCSignal> oram;

	public CompEnv<GCSignal> env;
	public IntegerLib<GCSignal> intLib;
	public FloatLib<GCSignal> floatLib;
	private T factoryT;
	private int m;

	public Stack(CompEnv<GCSignal> env, int m, T factoryT, CircuitOram<GCSignal> oram) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<GCSignal>(env);
		this.floatLib = new FloatLib<GCSignal>(env, 24, 8);
		this.m = m;
		this.factoryT = factoryT;
		this.size = env.inputOfAlice(Utils.fromInt(0, m));
		this.root = intLib.randBools(m);
		this.oram = oram;
	}

	public T stack_op(T operand, GCSignal op) throws Exception {
		T ret = factoryT.newObj(null);
		StackNode<T> r = new StackNode<T>(env, m, factoryT);
		StackNode<T> node = new StackNode<T>(env, m, factoryT);
		boolean f_tmp_0 = true;
		boolean __tmp0 = f_tmp_0;
		GCSignal f_tmp_2 = env.inputOfAlice(__tmp0);
		GCSignal f_tmp_1 = intLib.eq(op, f_tmp_2);
		GCSignal __tmp1 = f_tmp_1;
		GCSignal[] f_tmp_3 = this.size;
		GCSignal[] __tmp2 = f_tmp_3;
		GCSignal[] f_tmp_4 = this.root;
		GCSignal[] __tmp3 = f_tmp_4;
		CircuitOram<GCSignal> f_tmp_5 = this.oram;
		CircuitOram<GCSignal> __tmp4 = f_tmp_5;
		StackNode<T> f_tmp_6 = new StackNode<T>(env, m, factoryT).newObj(__tmp4.conditionalReadAndRemove(__tmp2, __tmp3, __tmp1));
		StackNode<T> __tmp5 = f_tmp_6;
		StackNode<T> f_tmp_7 = new StackNode<T>(env, m, factoryT).newObj(intLib.mux(r.getBits(), __tmp5.getBits(),__tmp1));
		StackNode<T> __tmp6 = f_tmp_7;
		r = __tmp6;
		GCSignal[] f_tmp_8 = r.next;
		GCSignal[] __tmp7 = f_tmp_8;
		GCSignal[] f_tmp_9 = this.root;
		GCSignal[] __tmp8 = f_tmp_9;
		GCSignal[] f_tmp_10 = intLib.mux(__tmp8, __tmp7,__tmp1);
		GCSignal[] __tmp9 = f_tmp_10;
		this.root = __tmp9;
		GCSignal[] f_tmp_11 = this.size;
		GCSignal[] __tmp10 = f_tmp_11;
		int f_tmp_12 = 1;
		int __tmp11 = f_tmp_12;
		GCSignal[] f_tmp_14 = env.inputOfAlice(Utils.fromInt(__tmp11, m));
		GCSignal[] f_tmp_13 = intLib.sub(__tmp10,f_tmp_14);
		GCSignal[] __tmp12 = f_tmp_13;
		GCSignal[] f_tmp_15 = this.size;
		GCSignal[] __tmp13 = f_tmp_15;
		GCSignal[] f_tmp_16 = intLib.mux(__tmp13, __tmp12,__tmp1);
		GCSignal[] __tmp14 = f_tmp_16;
		this.size = __tmp14;
		T f_tmp_17 = r.data;
		T __tmp15 = f_tmp_17;
		T f_tmp_18 = this.factoryT.newObj(intLib.mux(ret.getBits(), __tmp15.getBits(),__tmp1));
		T __tmp16 = f_tmp_18;
		ret = __tmp16;
		GCSignal f_tmp_19 = intLib.not(__tmp1);
		GCSignal __tmp17 = f_tmp_19;
		GCSignal[] f_tmp_20 = this.root;
		GCSignal[] __tmp18 = f_tmp_20;
		StackNode<T> f_tmp_21 = new StackNode<T>(env, m, factoryT);
		f_tmp_21.next = __tmp18;
		f_tmp_21.data = operand;
		StackNode<T> __tmp19 = f_tmp_21;
		StackNode<T> f_tmp_22 = new StackNode<T>(env, m, factoryT).newObj(intLib.mux(node.getBits(), __tmp19.getBits(),__tmp17));
		StackNode<T> __tmp20 = f_tmp_22;
		node = __tmp20;
		GCSignal[] f_tmp_23 = intLib.randBools(m);
		GCSignal[] __tmp21 = f_tmp_23;
		GCSignal[] f_tmp_24 = this.root;
		GCSignal[] __tmp22 = f_tmp_24;
		GCSignal[] f_tmp_25 = intLib.mux(__tmp22, __tmp21,__tmp17);
		GCSignal[] __tmp23 = f_tmp_25;
		this.root = __tmp23;
		GCSignal[] f_tmp_26 = this.size;
		GCSignal[] __tmp24 = f_tmp_26;
		int f_tmp_27 = 1;
		int __tmp25 = f_tmp_27;
		GCSignal[] f_tmp_29 = env.inputOfAlice(Utils.fromInt(__tmp25, m));
		GCSignal[] f_tmp_28 = intLib.add(__tmp24,f_tmp_29);
		GCSignal[] __tmp26 = f_tmp_28;
		GCSignal[] f_tmp_30 = this.size;
		GCSignal[] __tmp27 = f_tmp_30;
		GCSignal[] f_tmp_31 = intLib.mux(__tmp27, __tmp26,__tmp17);
		GCSignal[] __tmp28 = f_tmp_31;
		this.size = __tmp28;
		GCSignal[] f_tmp_32 = this.size;
		GCSignal[] __tmp29 = f_tmp_32;
		GCSignal[] f_tmp_33 = this.root;
		GCSignal[] __tmp30 = f_tmp_33;
		CircuitOram<GCSignal> f_tmp_34 = this.oram;
		CircuitOram<GCSignal> __tmp31 = f_tmp_34;
		__tmp31.conditionalPutBack(__tmp29, __tmp30, node.getBits(), __tmp17);
		return ret;
	}
}
