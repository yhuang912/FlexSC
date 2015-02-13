package compiledlib.sketch;
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
public class count_min_sketch {
	public Boolean[][][] hash_seed;
	public SecureArray<Boolean>[] sketch;

	public CompEnv<Boolean> env;
	public IntegerLib<Boolean> intLib;
	public FloatLib<Boolean> floatLib;

	public count_min_sketch(CompEnv<Boolean> env, IntegerLib<Boolean> intLib, FloatLib<Boolean> floatLib, Boolean[][][] hash_seed, SecureArray<Boolean>[] sketch) throws Exception {
		this.env = env;
		this.intLib = intLib;
		this.floatLib = floatLib;
		this.hash_seed = hash_seed;
		this.sketch = sketch;
	}

	public void init() throws Exception {
		int i = 0;
		int j = 0;
		int f_tmp_0 = 0;
		int __tmp0 = f_tmp_0;
		i = __tmp0;
		int f_tmp_1 = 5;
		int __tmp1 = f_tmp_1;
		boolean f_tmp_2 = i < __tmp1;
		boolean __tmp2 = f_tmp_2;
		while(__tmp2) {
			int f_tmp_3 = 0;
			int __tmp3 = f_tmp_3;
			j = __tmp3;
			int f_tmp_4 = 2;
			int __tmp4 = f_tmp_4;
			boolean f_tmp_5 = j < __tmp4;
			boolean __tmp5 = f_tmp_5;
			while(__tmp5) {
				int f_tmp_6 = 64;
				int __tmp6 = f_tmp_6;
				Boolean[] f_tmp_7 = intLib.randBools(__tmp6);
				Boolean[] __tmp7 = f_tmp_7;
				Boolean[][][] f_tmp_8 = this.hash_seed;
				Boolean[][][] __tmp8 = f_tmp_8;
				Boolean[][] f_tmp_9 = __tmp8[i];
				Boolean[][] __tmp9 = f_tmp_9;
				__tmp9[j]=__tmp7;
				int f_tmp_10 = 1;
				int __tmp10 = f_tmp_10;
				int f_tmp_11 = j + __tmp10;
				int __tmp11 = f_tmp_11;
				j = __tmp11;
				int f_tmp_12 = 2;
				__tmp4 = f_tmp_12;
				boolean f_tmp_13 = j < __tmp4;
				__tmp5 = f_tmp_13;
			}
			int f_tmp_14 = 1;
			int __tmp12 = f_tmp_14;
			int f_tmp_15 = i + __tmp12;
			int __tmp13 = f_tmp_15;
			i = __tmp13;
			int f_tmp_16 = 5;
			__tmp1 = f_tmp_16;
			boolean f_tmp_17 = i < __tmp1;
			__tmp2 = f_tmp_17;
		}

	}
	public Boolean[] fast_mod(Boolean[] v) throws Exception {
		int f_tmp_18 = 31;
		int __tmp14 = f_tmp_18;
		Boolean[] f_tmp_19 = intLib.rightPublicShift(v, __tmp14);
		Boolean[] __tmp15 = f_tmp_19;
		Boolean[] f_tmp_20 = intLib.add(__tmp15,v);
		Boolean[] __tmp16 = f_tmp_20;
		int f_tmp_21 = 2147483647;
		int __tmp17 = f_tmp_21;
		Boolean[] f_tmp_23 = env.inputOfAlice(Utils.fromInt(__tmp17, 64));
		Boolean[] f_tmp_22 = intLib.and(__tmp16,f_tmp_23);
		Boolean[] __tmp18 = f_tmp_22;
		return __tmp18;
	}
	public Boolean[] hash(int row_number, Boolean[] element) throws Exception {
		Boolean[] h = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[][][] f_tmp_24 = this.hash_seed;
		Boolean[][][] __tmp19 = f_tmp_24;
		Boolean[][] f_tmp_25 = __tmp19[row_number];
		Boolean[][] __tmp20 = f_tmp_25;
		int f_tmp_26 = 0;
		int __tmp21 = f_tmp_26;
		Boolean[] f_tmp_27 = __tmp20[__tmp21];
		Boolean[] __tmp22 = f_tmp_27;
		Boolean[] f_tmp_28 = intLib.multiply(__tmp22,element);
		Boolean[] __tmp23 = f_tmp_28;
		Boolean[][][] f_tmp_29 = this.hash_seed;
		Boolean[][][] __tmp24 = f_tmp_29;
		Boolean[][] f_tmp_30 = __tmp24[row_number];
		Boolean[][] __tmp25 = f_tmp_30;
		int f_tmp_31 = 1;
		int __tmp26 = f_tmp_31;
		Boolean[] f_tmp_32 = __tmp25[__tmp26];
		Boolean[] __tmp27 = f_tmp_32;
		Boolean[] f_tmp_33 = intLib.add(__tmp23,__tmp27);
		Boolean[] __tmp28 = f_tmp_33;
		h = __tmp28;
		Boolean[] f_tmp_34 = this.fast_mod(h);
		Boolean[] __tmp29 = f_tmp_34;
		int f_tmp_35 = 100;
		int __tmp30 = f_tmp_35;
		Boolean[] f_tmp_37 = env.inputOfAlice(Utils.fromInt(__tmp30, 64));
		Boolean[] f_tmp_36 = intLib.mod(__tmp29,f_tmp_37);
		Boolean[] __tmp31 = f_tmp_36;
		return __tmp31;
	}
	public void insert(Boolean[] element, Boolean[] frequency) throws Exception {
		int i = 0;
		Boolean[] pos = env.inputOfAlice(Utils.fromInt(0, 64));
		int f_tmp_38 = 0;
		int __tmp32 = f_tmp_38;
		i = __tmp32;
		int f_tmp_39 = 5;
		int __tmp33 = f_tmp_39;
		boolean f_tmp_40 = i < __tmp33;
		boolean __tmp34 = f_tmp_40;
		while(__tmp34) {
			Boolean[] f_tmp_41 = this.hash(i, element);
			Boolean[] __tmp35 = f_tmp_41;
			pos = __tmp35;
			SecureArray<Boolean>[] f_tmp_42 = this.sketch;
			SecureArray<Boolean>[] __tmp36 = f_tmp_42;
			SecureArray<Boolean> f_tmp_43 = __tmp36[i];
			SecureArray<Boolean> __tmp37 = f_tmp_43;
			Boolean[] f_tmp_44 = __tmp37.read(pos);
			Boolean[] __tmp38 = f_tmp_44;
			Boolean[] f_tmp_45 = intLib.add(__tmp38,frequency);
			Boolean[] __tmp39 = f_tmp_45;
			SecureArray<Boolean>[] f_tmp_46 = this.sketch;
			SecureArray<Boolean>[] __tmp40 = f_tmp_46;
			SecureArray<Boolean> f_tmp_47 = __tmp40[i];
			SecureArray<Boolean> __tmp41 = f_tmp_47;
			__tmp41.write(pos,__tmp39);
			int f_tmp_48 = 1;
			int __tmp42 = f_tmp_48;
			int f_tmp_49 = i + __tmp42;
			int __tmp43 = f_tmp_49;
			i = __tmp43;
			int f_tmp_50 = 5;
			__tmp33 = f_tmp_50;
			boolean f_tmp_51 = i < __tmp33;
			__tmp34 = f_tmp_51;
		}

	}
	public Boolean[] query(Boolean[] element) throws Exception {
		Boolean[] minimum = env.inputOfAlice(Utils.fromInt(0, 64));
		int i = 0;
		Boolean[] pos = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[] s = env.inputOfAlice(Utils.fromInt(0, 64));
		int f_tmp_52 = 1;
		int __tmp44 = f_tmp_52;
		int f_tmp_53 = 31;
		int __tmp45 = f_tmp_53;
		int f_tmp_54 = __tmp44 << __tmp45;
		int __tmp46 = f_tmp_54;
		minimum = env.inputOfAlice(Utils.fromInt(__tmp46, 64));
		int f_tmp_55 = 0;
		int __tmp47 = f_tmp_55;
		i = __tmp47;
		int f_tmp_56 = 5;
		int __tmp48 = f_tmp_56;
		boolean f_tmp_57 = i < __tmp48;
		boolean __tmp49 = f_tmp_57;
		while(__tmp49) {
			Boolean[] f_tmp_58 = this.hash(i, element);
			Boolean[] __tmp50 = f_tmp_58;
			pos = __tmp50;
			SecureArray<Boolean>[] f_tmp_59 = this.sketch;
			SecureArray<Boolean>[] __tmp51 = f_tmp_59;
			SecureArray<Boolean> f_tmp_60 = __tmp51[i];
			SecureArray<Boolean> __tmp52 = f_tmp_60;
			Boolean[] f_tmp_61 = __tmp52.read(pos);
			Boolean[] __tmp53 = f_tmp_61;
			s = __tmp53;
			Boolean f_tmp_62 = intLib.not(intLib.geq(s, minimum));
			Boolean __tmp54 = f_tmp_62;
			Boolean[] f_tmp_63 = intLib.mux(minimum, s,__tmp54);
			Boolean[] __tmp55 = f_tmp_63;
			minimum = __tmp55;
			Boolean f_tmp_64 = intLib.not(__tmp54);
			Boolean __tmp56 = f_tmp_64;
			int f_tmp_65 = 1;
			int __tmp57 = f_tmp_65;
			int f_tmp_66 = i + __tmp57;
			int __tmp58 = f_tmp_66;
			i = __tmp58;
			int f_tmp_67 = 5;
			__tmp48 = f_tmp_67;
			boolean f_tmp_68 = i < __tmp48;
			__tmp49 = f_tmp_68;
		}
		return minimum;
	}
}
