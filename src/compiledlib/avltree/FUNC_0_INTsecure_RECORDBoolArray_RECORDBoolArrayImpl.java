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
public class FUNC_0_INTsecure_RECORDBoolArray_RECORDBoolArrayImpl extends FUNC_0_INTsecure_RECORDBoolArray_RECORDBoolArray {
	public FUNC_0_INTsecure_RECORDBoolArray_RECORDBoolArrayImpl(CompEnv<Boolean> env) throws Exception {
		super(env);
	}

	public Boolean[] calc(BoolArray x, BoolArray y) throws Exception {
		Boolean[] r = env.inputOfAlice(Utils.fromInt(0, 2));
		int f_tmp_521 = 0;
		int __tmp0 = f_tmp_521;
		int f_tmp_522 = 1;
		int __tmp1 = f_tmp_522;
		int f_tmp_523 = __tmp0 - __tmp1;
		int __tmp2 = f_tmp_523;
		r = env.inputOfAlice(Utils.fromInt(__tmp2, 2));
		Boolean[] f_tmp_524 = x.data;
		Boolean[] __tmp3 = f_tmp_524;
		Boolean[] f_tmp_525 = y.data;
		Boolean[] __tmp4 = f_tmp_525;
		Boolean f_tmp_526 = intLib.eq(__tmp3, __tmp4);
		Boolean __tmp5 = f_tmp_526;
		int f_tmp_527 = 0;
		int __tmp6 = f_tmp_527;
		Boolean[] f_tmp_529 = env.inputOfAlice(Utils.fromInt(__tmp6, 2));
		Boolean[] f_tmp_528 = intLib.mux(r, f_tmp_529,__tmp5);
		Boolean[] __tmp7 = f_tmp_528;
		r = __tmp7;
		Boolean f_tmp_530 = intLib.not(__tmp5);
		Boolean __tmp8 = f_tmp_530;
		Boolean[] f_tmp_531 = x.data;
		Boolean[] __tmp9 = f_tmp_531;
		Boolean[] f_tmp_532 = y.data;
		Boolean[] __tmp10 = f_tmp_532;
		Boolean f_tmp_533 = intLib.not(intLib.leq(__tmp9, __tmp10));
		Boolean __tmp11 = f_tmp_533;
		Boolean f_tmp_534 = intLib.and(__tmp8,__tmp11);
		Boolean __tmp12 = f_tmp_534;
		int f_tmp_535 = 1;
		int __tmp13 = f_tmp_535;
		Boolean[] f_tmp_537 = env.inputOfAlice(Utils.fromInt(__tmp13, 2));
		Boolean[] f_tmp_536 = intLib.mux(r, f_tmp_537,__tmp12);
		Boolean[] __tmp14 = f_tmp_536;
		r = __tmp14;
		Boolean f_tmp_538 = intLib.not(__tmp11);
		Boolean __tmp15 = f_tmp_538;
		Boolean f_tmp_539 = intLib.and(__tmp8,__tmp15);
		Boolean __tmp16 = f_tmp_539;
		return r;
	}
}
