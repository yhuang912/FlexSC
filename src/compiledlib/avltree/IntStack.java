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
public class IntStack {
	public CircuitOram<Boolean> poram;
	public Boolean[] size;
	public Boolean[] root;

	public CompEnv<Boolean> env;
	public IntegerLib<Boolean> intLib;
	public FloatLib<Boolean> floatLib;
	private int m;

	public IntStack(CompEnv<Boolean> env, int m, CircuitOram<Boolean> poram) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<Boolean>(env);
		this.floatLib = new FloatLib<Boolean>(env, 24, 8);
		this.m = m;
		this.poram = poram;
		this.size = env.inputOfAlice(Utils.fromInt(0, m));
		this.root = intLib.randBools(m);
	}

	public Boolean[] stack_op(Boolean[] operand, Boolean op, Boolean dum) throws Exception {
		Boolean[] ret = env.inputOfAlice(Utils.fromInt(0, m));
		IntStackNode r = new IntStackNode(env, m);
		IntStackNode node = new IntStackNode(env, m);
		boolean f_tmp_0 = true;
		boolean __tmp17 = f_tmp_0;
		Boolean f_tmp_2 = env.inputOfAlice(__tmp17);
		Boolean f_tmp_1 = intLib.eq(op, f_tmp_2);
		Boolean __tmp18 = f_tmp_1;
		boolean f_tmp_3 = true;
		boolean __tmp19 = f_tmp_3;
		Boolean f_tmp_5 = env.inputOfAlice(__tmp19);
		Boolean f_tmp_4 = intLib.eq(dum, f_tmp_5);
		Boolean __tmp20 = f_tmp_4;
		Boolean f_tmp_6 = intLib.and(__tmp18,__tmp20);
		Boolean __tmp21 = f_tmp_6;
		Boolean[] f_tmp_7 = this.size;
		Boolean[] __tmp22 = f_tmp_7;
		Boolean[] f_tmp_8 = this.root;
		Boolean[] __tmp23 = f_tmp_8;
		CircuitOram<Boolean> f_tmp_9 = this.poram;
		CircuitOram<Boolean> __tmp24 = f_tmp_9;
		IntStackNode f_tmp_10 = new IntStackNode(env, m).newObj(__tmp24.conditionalReadAndRemove(__tmp22, __tmp23, __tmp21));
		IntStackNode __tmp25 = f_tmp_10;
		IntStackNode f_tmp_11 = new IntStackNode(env, m).newObj(intLib.mux(r.getBits(), __tmp25.getBits(),__tmp21));
		IntStackNode __tmp26 = f_tmp_11;
		r = __tmp26;
		Boolean[] f_tmp_12 = r.next;
		Boolean[] __tmp27 = f_tmp_12;
		Boolean[] f_tmp_13 = this.root;
		Boolean[] __tmp28 = f_tmp_13;
		Boolean[] f_tmp_14 = intLib.mux(__tmp28, __tmp27,__tmp21);
		Boolean[] __tmp29 = f_tmp_14;
		this.root = __tmp29;
		Boolean[] f_tmp_15 = this.size;
		Boolean[] __tmp30 = f_tmp_15;
		int f_tmp_16 = 1;
		int __tmp31 = f_tmp_16;
		Boolean[] f_tmp_18 = env.inputOfAlice(Utils.fromInt(__tmp31, m));
		Boolean[] f_tmp_17 = intLib.sub(__tmp30,f_tmp_18);
		Boolean[] __tmp32 = f_tmp_17;
		Boolean[] f_tmp_19 = this.size;
		Boolean[] __tmp33 = f_tmp_19;
		Boolean[] f_tmp_20 = intLib.mux(__tmp33, __tmp32,__tmp21);
		Boolean[] __tmp34 = f_tmp_20;
		this.size = __tmp34;
		Boolean[] f_tmp_21 = r.data;
		Boolean[] __tmp35 = f_tmp_21;
		Boolean[] f_tmp_22 = intLib.mux(ret, __tmp35,__tmp21);
		Boolean[] __tmp36 = f_tmp_22;
		ret = __tmp36;
		Boolean f_tmp_23 = intLib.not(__tmp21);
		Boolean __tmp37 = f_tmp_23;
		boolean f_tmp_24 = true;
		boolean __tmp38 = f_tmp_24;
		Boolean f_tmp_26 = env.inputOfAlice(__tmp38);
		Boolean f_tmp_25 = intLib.eq(dum, f_tmp_26);
		Boolean __tmp39 = f_tmp_25;
		Boolean f_tmp_27 = intLib.and(__tmp37,__tmp39);
		Boolean __tmp40 = f_tmp_27;
		Boolean[] f_tmp_28 = this.root;
		Boolean[] __tmp41 = f_tmp_28;
		IntStackNode f_tmp_29 = new IntStackNode(env, m);
		f_tmp_29.next = __tmp41;
		f_tmp_29.data = operand;
		IntStackNode __tmp42 = f_tmp_29;
		IntStackNode f_tmp_30 = new IntStackNode(env, m).newObj(intLib.mux(node.getBits(), __tmp42.getBits(),__tmp40));
		IntStackNode __tmp43 = f_tmp_30;
		node = __tmp43;
		Boolean[] f_tmp_31 = intLib.randBools(m);
		Boolean[] __tmp44 = f_tmp_31;
		Boolean[] f_tmp_32 = this.root;
		Boolean[] __tmp45 = f_tmp_32;
		Boolean[] f_tmp_33 = intLib.mux(__tmp45, __tmp44,__tmp40);
		Boolean[] __tmp46 = f_tmp_33;
		this.root = __tmp46;
		Boolean[] f_tmp_34 = this.size;
		Boolean[] __tmp47 = f_tmp_34;
		int f_tmp_35 = 1;
		int __tmp48 = f_tmp_35;
		Boolean[] f_tmp_37 = env.inputOfAlice(Utils.fromInt(__tmp48, m));
		Boolean[] f_tmp_36 = intLib.add(__tmp47,f_tmp_37);
		Boolean[] __tmp49 = f_tmp_36;
		Boolean[] f_tmp_38 = this.size;
		Boolean[] __tmp50 = f_tmp_38;
		Boolean[] f_tmp_39 = intLib.mux(__tmp50, __tmp49,__tmp40);
		Boolean[] __tmp51 = f_tmp_39;
		this.size = __tmp51;
		Boolean[] f_tmp_40 = this.size;
		Boolean[] __tmp52 = f_tmp_40;
		Boolean[] f_tmp_41 = this.root;
		Boolean[] __tmp53 = f_tmp_41;
		CircuitOram<Boolean> f_tmp_42 = this.poram;
		CircuitOram<Boolean> __tmp54 = f_tmp_42;
		__tmp54.conditionalPutBack(__tmp52, __tmp53, node.getBits(), __tmp40);
		Boolean f_tmp_44 = intLib.not(__tmp39);
		Boolean __tmp56 = f_tmp_44;
		Boolean f_tmp_45 = intLib.and(__tmp37,__tmp56);
		Boolean __tmp57 = f_tmp_45;
		return ret;
	}
}
