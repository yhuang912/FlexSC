package compiledlib.sketch;
import java.security.SecureRandom;

import oram.SecureArray;
import oram.CircuitOram;
import flexsc.Mode;
import flexsc.Party;
import flexsc.CompEnv;

import java.util.BitSet;

import circuits.arithmetic.FloatLib;
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
public class ams_sketch {
	public Boolean[][][] hash_seed;
	public SecureArray<Boolean>[] sketch;

	public CompEnv<Boolean> env;
	public IntegerLib<Boolean> intLib;
	public FloatLib<Boolean> floatLib;

	public ams_sketch(CompEnv<Boolean> env, IntegerLib<Boolean> intLib, FloatLib<Boolean> floatLib, Boolean[][][] hash_seed, SecureArray<Boolean>[] sketch) throws Exception {
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
			int f_tmp_4 = 6;
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
				int f_tmp_12 = 6;
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
	public Boolean[] hash2(int row_number, Boolean[] element) throws Exception {
		Boolean[] h = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[][][] f_tmp_24 = this.hash_seed;
		Boolean[][][] __tmp19 = f_tmp_24;
		int f_tmp_25 = 0;
		int __tmp20 = f_tmp_25;
		Boolean[][] f_tmp_26 = __tmp19[__tmp20];
		Boolean[][] __tmp21 = f_tmp_26;
		int f_tmp_27 = 0;
		int __tmp22 = f_tmp_27;
		Boolean[] f_tmp_28 = __tmp21[__tmp22];
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
		int f_tmp_35 = 5000;
		int __tmp30 = f_tmp_35;
		Boolean[] f_tmp_37 = env.inputOfAlice(Utils.fromInt(__tmp30, 64));
		Boolean[] f_tmp_36 = intLib.mod(__tmp29,f_tmp_37);
		Boolean[] __tmp31 = f_tmp_36;
		return __tmp31;
	}
	public Boolean[] hash4(int row_number, Boolean[] element) throws Exception {
		Boolean[] h = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[][][] f_tmp_38 = this.hash_seed;
		Boolean[][][] __tmp32 = f_tmp_38;
		Boolean[][] f_tmp_39 = __tmp32[row_number];
		Boolean[][] __tmp33 = f_tmp_39;
		int f_tmp_40 = 2;
		int __tmp34 = f_tmp_40;
		Boolean[] f_tmp_41 = __tmp33[__tmp34];
		Boolean[] __tmp35 = f_tmp_41;
		h = __tmp35;
		Boolean[] f_tmp_42 = intLib.multiply(h,element);
		Boolean[] __tmp36 = f_tmp_42;
		Boolean[][][] f_tmp_43 = this.hash_seed;
		Boolean[][][] __tmp37 = f_tmp_43;
		Boolean[][] f_tmp_44 = __tmp37[row_number];
		Boolean[][] __tmp38 = f_tmp_44;
		int f_tmp_45 = 3;
		int __tmp39 = f_tmp_45;
		Boolean[] f_tmp_46 = __tmp38[__tmp39];
		Boolean[] __tmp40 = f_tmp_46;
		Boolean[] f_tmp_47 = intLib.add(__tmp36,__tmp40);
		Boolean[] __tmp41 = f_tmp_47;
		Boolean[] f_tmp_48 = this.fast_mod(__tmp41);
		Boolean[] __tmp42 = f_tmp_48;
		h = __tmp42;
		Boolean[] f_tmp_49 = intLib.multiply(h,element);
		Boolean[] __tmp43 = f_tmp_49;
		Boolean[][][] f_tmp_50 = this.hash_seed;
		Boolean[][][] __tmp44 = f_tmp_50;
		Boolean[][] f_tmp_51 = __tmp44[row_number];
		Boolean[][] __tmp45 = f_tmp_51;
		int f_tmp_52 = 4;
		int __tmp46 = f_tmp_52;
		Boolean[] f_tmp_53 = __tmp45[__tmp46];
		Boolean[] __tmp47 = f_tmp_53;
		Boolean[] f_tmp_54 = intLib.add(__tmp43,__tmp47);
		Boolean[] __tmp48 = f_tmp_54;
		Boolean[] f_tmp_55 = this.fast_mod(__tmp48);
		Boolean[] __tmp49 = f_tmp_55;
		h = __tmp49;
		Boolean[] f_tmp_56 = intLib.multiply(h,element);
		Boolean[] __tmp50 = f_tmp_56;
		Boolean[][][] f_tmp_57 = this.hash_seed;
		Boolean[][][] __tmp51 = f_tmp_57;
		Boolean[][] f_tmp_58 = __tmp51[row_number];
		Boolean[][] __tmp52 = f_tmp_58;
		int f_tmp_59 = 5;
		int __tmp53 = f_tmp_59;
		Boolean[] f_tmp_60 = __tmp52[__tmp53];
		Boolean[] __tmp54 = f_tmp_60;
		Boolean[] f_tmp_61 = intLib.add(__tmp50,__tmp54);
		Boolean[] __tmp55 = f_tmp_61;
		Boolean[] f_tmp_62 = this.fast_mod(__tmp55);
		Boolean[] __tmp56 = f_tmp_62;
		h = __tmp56;
		int f_tmp_63 = 1;
		int __tmp57 = f_tmp_63;
		Boolean[] f_tmp_65 = env.inputOfAlice(Utils.fromInt(__tmp57, 64));
		Boolean[] f_tmp_64 = intLib.and(h,f_tmp_65);
		Boolean[] __tmp58 = f_tmp_64;
		return __tmp58;
	}
	public void insert(Boolean[] element, Boolean[] frequency) throws Exception {
		int i = 0;
		Boolean[] pos = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[] h4 = env.inputOfAlice(Utils.fromInt(0, 64));
		int f_tmp_66 = 0;
		int __tmp59 = f_tmp_66;
		i = __tmp59;
		int f_tmp_67 = 5;
		int __tmp60 = f_tmp_67;
		boolean f_tmp_68 = i < __tmp60;
		boolean __tmp61 = f_tmp_68;
		while(__tmp61) {
			Boolean[] f_tmp_69 = this.hash2(i, element);
			Boolean[] __tmp62 = f_tmp_69;
			pos = __tmp62;
			Boolean[] f_tmp_70 = this.hash4(i, element);
			Boolean[] __tmp63 = f_tmp_70;
			h4 = __tmp63;
			int f_tmp_71 = 0;
			int __tmp64 = f_tmp_71;
			Boolean[] f_tmp_73 = env.inputOfAlice(Utils.fromInt(__tmp64, 64));
			Boolean f_tmp_72 = intLib.eq(h4, f_tmp_73);
			Boolean __tmp65 = f_tmp_72;
			SecureArray<Boolean>[] f_tmp_74 = this.sketch;
			SecureArray<Boolean>[] __tmp66 = f_tmp_74;
			SecureArray<Boolean> f_tmp_75 = __tmp66[i];
			SecureArray<Boolean> __tmp67 = f_tmp_75;
			Boolean[] f_tmp_76 = __tmp67.read(pos);
			Boolean[] __tmp68 = f_tmp_76;
			Boolean[] f_tmp_77 = intLib.add(__tmp68,frequency);
			Boolean[] __tmp69 = f_tmp_77;
			SecureArray<Boolean>[] f_tmp_78 = this.sketch;
			SecureArray<Boolean>[] __tmp70 = f_tmp_78;
			SecureArray<Boolean> f_tmp_79 = __tmp70[i];
			SecureArray<Boolean> __tmp71 = f_tmp_79;
			Boolean[] f_tmp_80 = __tmp71.read(pos);
			Boolean[] __tmp72 = f_tmp_80;
			Boolean[] f_tmp_81 = intLib.mux(__tmp72, __tmp69,__tmp65);
			Boolean[] __tmp73 = f_tmp_81;
			__tmp71.write(pos,__tmp73);
			Boolean f_tmp_82 = intLib.not(__tmp65);
			Boolean __tmp74 = f_tmp_82;
			SecureArray<Boolean>[] f_tmp_83 = this.sketch;
			SecureArray<Boolean>[] __tmp75 = f_tmp_83;
			SecureArray<Boolean> f_tmp_84 = __tmp75[i];
			SecureArray<Boolean> __tmp76 = f_tmp_84;
			Boolean[] f_tmp_85 = __tmp76.read(pos);
			Boolean[] __tmp77 = f_tmp_85;
			Boolean[] f_tmp_86 = intLib.sub(__tmp77,frequency);
			Boolean[] __tmp78 = f_tmp_86;
			SecureArray<Boolean>[] f_tmp_87 = this.sketch;
			SecureArray<Boolean>[] __tmp79 = f_tmp_87;
			SecureArray<Boolean> f_tmp_88 = __tmp79[i];
			SecureArray<Boolean> __tmp80 = f_tmp_88;
			Boolean[] f_tmp_89 = __tmp80.read(pos);
			Boolean[] __tmp81 = f_tmp_89;
			Boolean[] f_tmp_90 = intLib.mux(__tmp81, __tmp78,__tmp74);
			Boolean[] __tmp82 = f_tmp_90;
			__tmp80.write(pos,__tmp82);
			int f_tmp_91 = 1;
			int __tmp83 = f_tmp_91;
			int f_tmp_92 = i + __tmp83;
			int __tmp84 = f_tmp_92;
			i = __tmp84;
			int f_tmp_93 = 5;
			__tmp60 = f_tmp_93;
			boolean f_tmp_94 = i < __tmp60;
			__tmp61 = f_tmp_94;
		}

	}
	public Boolean[] query(Boolean[] element) throws Exception {
		Boolean[][] res = new Boolean[5][];
		for(int _j_=0; _j_<5; ++_j_) {
			res[_j_] = env.inputOfAlice(Utils.fromInt(0, 64));
		}
		int i = 0;
		int j = 0;
		int f_tmp_95 = 0;
		int __tmp85 = f_tmp_95;
		i = __tmp85;
		int f_tmp_96 = 5;
		int __tmp86 = f_tmp_96;
		boolean f_tmp_97 = i < __tmp86;
		boolean __tmp87 = f_tmp_97;
		while(__tmp87) {
			int f_tmp_98 = 0;
			int __tmp88 = f_tmp_98;
			Boolean[] f_tmp_99 = env.inputOfAlice(Utils.fromInt(__tmp88, 64));
			res[i]=f_tmp_99;
			int f_tmp_100 = 0;
			int __tmp89 = f_tmp_100;
			j = __tmp89;
			int f_tmp_101 = 5000;
			int __tmp90 = f_tmp_101;
			boolean f_tmp_102 = j < __tmp90;
			boolean __tmp91 = f_tmp_102;
			while(__tmp91) {
				Boolean[] f_tmp_103 = res[i];
				Boolean[] __tmp92 = f_tmp_103;
				SecureArray<Boolean>[] f_tmp_104 = this.sketch;
				SecureArray<Boolean>[] __tmp93 = f_tmp_104;
				SecureArray<Boolean> f_tmp_105 = __tmp93[i];
				SecureArray<Boolean> __tmp94 = f_tmp_105;
				Boolean[] f_tmp_107 = env.inputOfAlice(Utils.fromInt(j, 64));
				Boolean[] f_tmp_106 = __tmp94.read(f_tmp_107);
				Boolean[] __tmp95 = f_tmp_106;
				SecureArray<Boolean>[] f_tmp_108 = this.sketch;
				SecureArray<Boolean>[] __tmp96 = f_tmp_108;
				SecureArray<Boolean> f_tmp_109 = __tmp96[i];
				SecureArray<Boolean> __tmp97 = f_tmp_109;
				Boolean[] f_tmp_111 = env.inputOfAlice(Utils.fromInt(j, 64));
				Boolean[] f_tmp_110 = __tmp97.read(f_tmp_111);
				Boolean[] __tmp98 = f_tmp_110;
				Boolean[] f_tmp_112 = intLib.multiply(__tmp95,__tmp98);
				Boolean[] __tmp99 = f_tmp_112;
				Boolean[] f_tmp_113 = intLib.add(__tmp92,__tmp99);
				Boolean[] __tmp100 = f_tmp_113;
				res[i]=__tmp100;
				int f_tmp_114 = 1;
				int __tmp101 = f_tmp_114;
				int f_tmp_115 = j + __tmp101;
				int __tmp102 = f_tmp_115;
				j = __tmp102;
				int f_tmp_116 = 5000;
				__tmp90 = f_tmp_116;
				boolean f_tmp_117 = j < __tmp90;
				__tmp91 = f_tmp_117;
			}
			int f_tmp_118 = 1;
			int __tmp103 = f_tmp_118;
			int f_tmp_119 = i + __tmp103;
			int __tmp104 = f_tmp_119;
			i = __tmp104;
			int f_tmp_120 = 5;
			__tmp86 = f_tmp_120;
			boolean f_tmp_121 = i < __tmp86;
			__tmp87 = f_tmp_121;
		}
		int f_tmp_122 = 0;
		int __tmp105 = f_tmp_122;
		i = __tmp105;
		int f_tmp_123 = 5;
		int __tmp106 = f_tmp_123;
		boolean f_tmp_124 = i < __tmp106;
		boolean __tmp107 = f_tmp_124;
		while(__tmp107) {
			int f_tmp_125 = 0;
			int __tmp108 = f_tmp_125;
			j = __tmp108;
			int f_tmp_126 = 5;
			int __tmp109 = f_tmp_126;
			boolean f_tmp_127 = j < __tmp109;
			boolean __tmp110 = f_tmp_127;
			while(__tmp110) {
				Boolean[] f_tmp_128 = res[i];
				Boolean[] __tmp111 = f_tmp_128;
				Boolean[] f_tmp_129 = res[j];
				Boolean[] __tmp112 = f_tmp_129;
				Boolean f_tmp_130 = intLib.not(intLib.geq(__tmp111, __tmp112));
				Boolean __tmp113 = f_tmp_130;
				Boolean[] f_tmp_131 = res[i];
				Boolean[] __tmp114 = f_tmp_131;
				Boolean[] f_tmp_132 = res[j];
				Boolean[] __tmp115 = f_tmp_132;
				Boolean[] f_tmp_133 = intLib.xor(__tmp114,__tmp115);
				Boolean[] __tmp116 = f_tmp_133;
				Boolean[] f_tmp_134 = res[i];
				Boolean[] __tmp117 = f_tmp_134;
				Boolean[] f_tmp_135 = intLib.mux(__tmp117, __tmp116,__tmp113);
				Boolean[] __tmp118 = f_tmp_135;
				res[i]=__tmp118;
				Boolean[] f_tmp_136 = res[i];
				Boolean[] __tmp119 = f_tmp_136;
				Boolean[] f_tmp_137 = res[j];
				Boolean[] __tmp120 = f_tmp_137;
				Boolean[] f_tmp_138 = intLib.xor(__tmp119,__tmp120);
				Boolean[] __tmp121 = f_tmp_138;
				Boolean[] f_tmp_139 = res[j];
				Boolean[] __tmp122 = f_tmp_139;
				Boolean[] f_tmp_140 = intLib.mux(__tmp122, __tmp121,__tmp113);
				Boolean[] __tmp123 = f_tmp_140;
				res[j]=__tmp123;
				Boolean[] f_tmp_141 = res[i];
				Boolean[] __tmp124 = f_tmp_141;
				Boolean[] f_tmp_142 = res[j];
				Boolean[] __tmp125 = f_tmp_142;
				Boolean[] f_tmp_143 = intLib.xor(__tmp124,__tmp125);
				Boolean[] __tmp126 = f_tmp_143;
				Boolean[] f_tmp_144 = res[i];
				Boolean[] __tmp127 = f_tmp_144;
				Boolean[] f_tmp_145 = intLib.mux(__tmp127, __tmp126,__tmp113);
				Boolean[] __tmp128 = f_tmp_145;
				res[i]=__tmp128;
				Boolean f_tmp_146 = intLib.not(__tmp113);
				Boolean __tmp129 = f_tmp_146;
				int f_tmp_147 = 1;
				int __tmp130 = f_tmp_147;
				int f_tmp_148 = j + __tmp130;
				int __tmp131 = f_tmp_148;
				j = __tmp131;
				int f_tmp_149 = 5;
				__tmp109 = f_tmp_149;
				boolean f_tmp_150 = j < __tmp109;
				__tmp110 = f_tmp_150;
			}
			int f_tmp_151 = 1;
			int __tmp132 = f_tmp_151;
			int f_tmp_152 = i + __tmp132;
			int __tmp133 = f_tmp_152;
			i = __tmp133;
			int f_tmp_153 = 5;
			__tmp106 = f_tmp_153;
			boolean f_tmp_154 = i < __tmp106;
			__tmp107 = f_tmp_154;
		}
		int f_tmp_155 = 5;
		int __tmp134 = f_tmp_155;
		int f_tmp_156 = 2;
		int __tmp135 = f_tmp_156;
		int f_tmp_157 = __tmp134 / __tmp135;
		int __tmp136 = f_tmp_157;
		Boolean[] f_tmp_158 = res[__tmp136];
		Boolean[] __tmp137 = f_tmp_158;
		return __tmp137;
	}
}
