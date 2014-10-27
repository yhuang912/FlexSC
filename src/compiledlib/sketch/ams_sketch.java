package compiledlib.sketch;
import oram.SecureArray;
import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
public class ams_sketch {
	public SecureArray<Boolean>[] sketch;
	public Boolean[][][] hash_seed;

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;

	public ams_sketch(CompEnv<Boolean> env, IntegerLib<Boolean> lib, SecureArray<Boolean>[] sketch, Boolean[][][] hash_seed) throws Exception {
		this.env = env;
		this.lib = lib;
		this.sketch = sketch;
		this.hash_seed = hash_seed;
	}

	public void init() throws Exception {
		int i = 0;
		int j = 0;
		int f_tmp_0 = 0;
		int __tmp0 = f_tmp_0;
		i = __tmp0;
		int f_tmp_1 = 10;
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
				Boolean[] f_tmp_7 = lib.randBools(__tmp6);
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
			int f_tmp_16 = 10;
			__tmp1 = f_tmp_16;
			boolean f_tmp_17 = i < __tmp1;
			__tmp2 = f_tmp_17;
		}

	}
	public Boolean[] fast_mod(Boolean[] v) throws Exception {
		Boolean[] f_tmp_18 = lib.rightPublicShift(v, 31);
		Boolean[] __tmp14 = f_tmp_18;
		Boolean[] f_tmp_19 = lib.add(__tmp14,v);
		Boolean[] __tmp15 = f_tmp_19;
		int f_tmp_20 = 2147483647;
		int __tmp16 = f_tmp_20;
		Boolean[] f_tmp_22 = env.inputOfAlice(Utils.fromInt(__tmp16, 64));
		Boolean[] f_tmp_21 = lib.and(__tmp15,f_tmp_22);
		Boolean[] __tmp17 = f_tmp_21;
		return __tmp17;
	}
	public Boolean[] hash2(int row_number, Boolean[] element) throws Exception {
		Boolean[] h = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[][][] f_tmp_23 = this.hash_seed;
		Boolean[][][] __tmp18 = f_tmp_23;
		int f_tmp_24 = 0;
		int __tmp19 = f_tmp_24;
		Boolean[][] f_tmp_25 = __tmp18[__tmp19];
		Boolean[][] __tmp20 = f_tmp_25;
		int f_tmp_26 = 0;
		int __tmp21 = f_tmp_26;
		Boolean[] f_tmp_27 = __tmp20[__tmp21];
		Boolean[] __tmp22 = f_tmp_27;
		Boolean[][][] f_tmp_28 = this.hash_seed;
		Boolean[][][] __tmp23 = f_tmp_28;
		Boolean[][] f_tmp_29 = __tmp23[row_number];
		Boolean[][] __tmp24 = f_tmp_29;
		int f_tmp_30 = 1;
		int __tmp25 = f_tmp_30;
		Boolean[] f_tmp_31 = __tmp24[__tmp25];
		Boolean[] __tmp26 = f_tmp_31;
		Boolean[] f_tmp_32 = lib.add(__tmp22,__tmp26);
		Boolean[] __tmp27 = f_tmp_32;
		h = __tmp27;
		Boolean[] f_tmp_33 = this.fast_mod(h);
		Boolean[] __tmp28 = f_tmp_33;
		int f_tmp_34 = 1000;
		int __tmp29 = f_tmp_34;
		Boolean[] f_tmp_36 = env.inputOfAlice(Utils.fromInt(__tmp29, 64));
		Boolean[] f_tmp_35 = lib.mod(__tmp28,f_tmp_36);
		Boolean[] __tmp30 = f_tmp_35;
		return __tmp30;
	}
	public Boolean[] hash4(int row_number, Boolean[] element) throws Exception {
		Boolean[] h = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[][][] f_tmp_37 = this.hash_seed;
		Boolean[][][] __tmp31 = f_tmp_37;
		Boolean[][] f_tmp_38 = __tmp31[row_number];
		Boolean[][] __tmp32 = f_tmp_38;
		int f_tmp_39 = 2;
		int __tmp33 = f_tmp_39;
		Boolean[] f_tmp_40 = __tmp32[__tmp33];
		Boolean[] __tmp34 = f_tmp_40;
		h = __tmp34;
		Boolean[] f_tmp_41 = lib.multiply(h,element);
		Boolean[] __tmp35 = f_tmp_41;
		Boolean[][][] f_tmp_42 = this.hash_seed;
		Boolean[][][] __tmp36 = f_tmp_42;
		Boolean[][] f_tmp_43 = __tmp36[row_number];
		Boolean[][] __tmp37 = f_tmp_43;
		int f_tmp_44 = 3;
		int __tmp38 = f_tmp_44;
		Boolean[] f_tmp_45 = __tmp37[__tmp38];
		Boolean[] __tmp39 = f_tmp_45;
		Boolean[] f_tmp_46 = lib.add(__tmp35,__tmp39);
		Boolean[] __tmp40 = f_tmp_46;
		Boolean[] f_tmp_47 = this.fast_mod(__tmp40);
		Boolean[] __tmp41 = f_tmp_47;
		h = __tmp41;
		Boolean[] f_tmp_48 = lib.multiply(h,element);
		Boolean[] __tmp42 = f_tmp_48;
		Boolean[][][] f_tmp_49 = this.hash_seed;
		Boolean[][][] __tmp43 = f_tmp_49;
		Boolean[][] f_tmp_50 = __tmp43[row_number];
		Boolean[][] __tmp44 = f_tmp_50;
		int f_tmp_51 = 4;
		int __tmp45 = f_tmp_51;
		Boolean[] f_tmp_52 = __tmp44[__tmp45];
		Boolean[] __tmp46 = f_tmp_52;
		Boolean[] f_tmp_53 = lib.add(__tmp42,__tmp46);
		Boolean[] __tmp47 = f_tmp_53;
		Boolean[] f_tmp_54 = this.fast_mod(__tmp47);
		Boolean[] __tmp48 = f_tmp_54;
		h = __tmp48;
		Boolean[] f_tmp_55 = lib.multiply(h,element);
		Boolean[] __tmp49 = f_tmp_55;
		Boolean[][][] f_tmp_56 = this.hash_seed;
		Boolean[][][] __tmp50 = f_tmp_56;
		Boolean[][] f_tmp_57 = __tmp50[row_number];
		Boolean[][] __tmp51 = f_tmp_57;
		int f_tmp_58 = 5;
		int __tmp52 = f_tmp_58;
		Boolean[] f_tmp_59 = __tmp51[__tmp52];
		Boolean[] __tmp53 = f_tmp_59;
		Boolean[] f_tmp_60 = lib.add(__tmp49,__tmp53);
		Boolean[] __tmp54 = f_tmp_60;
		Boolean[] f_tmp_61 = this.fast_mod(__tmp54);
		Boolean[] __tmp55 = f_tmp_61;
		h = __tmp55;
		int f_tmp_62 = 1;
		int __tmp56 = f_tmp_62;
		Boolean[] f_tmp_64 = env.inputOfAlice(Utils.fromInt(__tmp56, 64));
		Boolean[] f_tmp_63 = lib.and(h,f_tmp_64);
		Boolean[] __tmp57 = f_tmp_63;
		return __tmp57;
	}
	public void insert(Boolean[] element, Boolean[] frequency) throws Exception {
		int i = 0;
		Boolean[] pos = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[] h4 = env.inputOfAlice(Utils.fromInt(0, 64));
		int f_tmp_65 = 0;
		int __tmp58 = f_tmp_65;
		i = __tmp58;
		int f_tmp_66 = 10;
		int __tmp59 = f_tmp_66;
		boolean f_tmp_67 = i < __tmp59;
		boolean __tmp60 = f_tmp_67;
		while(__tmp60) {
			Boolean[] f_tmp_68 = this.hash2(i, element);
			Boolean[] __tmp61 = f_tmp_68;
			pos = __tmp61;
			Boolean[] f_tmp_69 = this.hash4(i, element);
			Boolean[] __tmp62 = f_tmp_69;
			h4 = __tmp62;
			int f_tmp_70 = 0;
			int __tmp63 = f_tmp_70;
			Boolean[] f_tmp_72 = env.inputOfAlice(Utils.fromInt(__tmp63, 64));
			Boolean f_tmp_71 = lib.eq(h4, f_tmp_72);
			Boolean __tmp64 = f_tmp_71;
			SecureArray<Boolean>[] f_tmp_73 = this.sketch;
			SecureArray<Boolean>[] __tmp65 = f_tmp_73;
			SecureArray<Boolean> f_tmp_74 = __tmp65[i];
			SecureArray<Boolean> __tmp66 = f_tmp_74;
			Boolean[] f_tmp_75 = __tmp66.read(pos);
			Boolean[] __tmp67 = f_tmp_75;
			Boolean[] f_tmp_76 = lib.add(__tmp67,frequency);
			Boolean[] __tmp68 = f_tmp_76;
			SecureArray<Boolean>[] f_tmp_77 = this.sketch;
			SecureArray<Boolean>[] __tmp69 = f_tmp_77;
			SecureArray<Boolean> f_tmp_78 = __tmp69[i];
			SecureArray<Boolean> __tmp70 = f_tmp_78;
			Boolean[] f_tmp_79 = __tmp70.read(pos);
			Boolean[] __tmp71 = f_tmp_79;
			Boolean[] f_tmp_80 = lib.mux(__tmp71, __tmp68,__tmp64);
			Boolean[] __tmp72 = f_tmp_80;
			__tmp70.write(pos,__tmp72);
			Boolean f_tmp_81 = lib.not(__tmp64);
			Boolean __tmp73 = f_tmp_81;
			SecureArray<Boolean>[] f_tmp_82 = this.sketch;
			SecureArray<Boolean>[] __tmp74 = f_tmp_82;
			SecureArray<Boolean> f_tmp_83 = __tmp74[i];
			SecureArray<Boolean> __tmp75 = f_tmp_83;
			Boolean[] f_tmp_84 = __tmp75.read(pos);
			Boolean[] __tmp76 = f_tmp_84;
			Boolean[] f_tmp_85 = lib.sub(__tmp76,frequency);
			Boolean[] __tmp77 = f_tmp_85;
			SecureArray<Boolean>[] f_tmp_86 = this.sketch;
			SecureArray<Boolean>[] __tmp78 = f_tmp_86;
			SecureArray<Boolean> f_tmp_87 = __tmp78[i];
			SecureArray<Boolean> __tmp79 = f_tmp_87;
			Boolean[] f_tmp_88 = __tmp79.read(pos);
			Boolean[] __tmp80 = f_tmp_88;
			Boolean[] f_tmp_89 = lib.mux(__tmp80, __tmp77,__tmp73);
			Boolean[] __tmp81 = f_tmp_89;
			__tmp79.write(pos,__tmp81);
			int f_tmp_90 = 1;
			int __tmp82 = f_tmp_90;
			int f_tmp_91 = i + __tmp82;
			int __tmp83 = f_tmp_91;
			i = __tmp83;
			int f_tmp_92 = 10;
			__tmp59 = f_tmp_92;
			boolean f_tmp_93 = i < __tmp59;
			__tmp60 = f_tmp_93;
		}

	}
	public Boolean[] query(Boolean[] element) throws Exception {
		Boolean[][] res = new Boolean[10][];
		int i = 0;
		int j = 0;
		int f_tmp_94 = 0;
		int __tmp84 = f_tmp_94;
		i = __tmp84;
		int f_tmp_95 = 10;
		int __tmp85 = f_tmp_95;
		boolean f_tmp_96 = i < __tmp85;
		boolean __tmp86 = f_tmp_96;
		while(__tmp86) {
			int f_tmp_97 = 0;
			int __tmp87 = f_tmp_97;
			Boolean[] f_tmp_98 = env.inputOfAlice(Utils.fromInt(__tmp87, 64));
			res[i]=f_tmp_98;
			int f_tmp_99 = 0;
			int __tmp88 = f_tmp_99;
			j = __tmp88;
			int f_tmp_100 = 1000;
			int __tmp89 = f_tmp_100;
			boolean f_tmp_101 = j < __tmp89;
			boolean __tmp90 = f_tmp_101;
			while(__tmp90) {
				Boolean[] f_tmp_102 = res[i];
				Boolean[] __tmp91 = f_tmp_102;
				SecureArray<Boolean>[] f_tmp_103 = this.sketch;
				SecureArray<Boolean>[] __tmp92 = f_tmp_103;
				SecureArray<Boolean> f_tmp_104 = __tmp92[i];
				SecureArray<Boolean> __tmp93 = f_tmp_104;
				Boolean[] f_tmp_106 = env.inputOfAlice(Utils.fromInt(j, 64));
				Boolean[] f_tmp_105 = __tmp93.read(f_tmp_106);
				Boolean[] __tmp94 = f_tmp_105;
				SecureArray<Boolean>[] f_tmp_107 = this.sketch;
				SecureArray<Boolean>[] __tmp95 = f_tmp_107;
				SecureArray<Boolean> f_tmp_108 = __tmp95[i];
				SecureArray<Boolean> __tmp96 = f_tmp_108;
				Boolean[] f_tmp_110 = env.inputOfAlice(Utils.fromInt(j, 64));
				Boolean[] f_tmp_109 = __tmp96.read(f_tmp_110);
				Boolean[] __tmp97 = f_tmp_109;
				Boolean[] f_tmp_111 = lib.multiply(__tmp94,__tmp97);
				Boolean[] __tmp98 = f_tmp_111;
				Boolean[] f_tmp_112 = lib.add(__tmp91,__tmp98);
				Boolean[] __tmp99 = f_tmp_112;
				res[i]=__tmp99;
				int f_tmp_113 = 1;
				int __tmp100 = f_tmp_113;
				int f_tmp_114 = j + __tmp100;
				int __tmp101 = f_tmp_114;
				j = __tmp101;
				int f_tmp_115 = 1000;
				__tmp89 = f_tmp_115;
				boolean f_tmp_116 = j < __tmp89;
				__tmp90 = f_tmp_116;
			}
			int f_tmp_117 = 1;
			int __tmp102 = f_tmp_117;
			int f_tmp_118 = i + __tmp102;
			int __tmp103 = f_tmp_118;
			i = __tmp103;
			int f_tmp_119 = 10;
			__tmp85 = f_tmp_119;
			boolean f_tmp_120 = i < __tmp85;
			__tmp86 = f_tmp_120;
		}
		int f_tmp_121 = 0;
		int __tmp104 = f_tmp_121;
		i = __tmp104;
		int f_tmp_122 = 10;
		int __tmp105 = f_tmp_122;
		boolean f_tmp_123 = i < __tmp105;
		boolean __tmp106 = f_tmp_123;
		while(__tmp106) {
			int f_tmp_124 = 0;
			int __tmp107 = f_tmp_124;
			j = __tmp107;
			int f_tmp_125 = 10;
			int __tmp108 = f_tmp_125;
			boolean f_tmp_126 = j < __tmp108;
			boolean __tmp109 = f_tmp_126;
			while(__tmp109) {
				Boolean[] f_tmp_127 = res[i];
				Boolean[] __tmp110 = f_tmp_127;
				Boolean[] f_tmp_128 = res[j];
				Boolean[] __tmp111 = f_tmp_128;
				Boolean f_tmp_129 = lib.not(lib.geq(__tmp110, __tmp111));
				Boolean __tmp112 = f_tmp_129;
				Boolean[] f_tmp_130 = res[i];
				Boolean[] __tmp113 = f_tmp_130;
				Boolean[] f_tmp_131 = res[j];
				Boolean[] __tmp114 = f_tmp_131;
				Boolean[] f_tmp_132 = lib.xor(__tmp113,__tmp114);
				Boolean[] __tmp115 = f_tmp_132;
				Boolean[] f_tmp_133 = res[i];
				Boolean[] __tmp116 = f_tmp_133;
				Boolean[] f_tmp_134 = lib.mux(__tmp116, __tmp115,__tmp112);
				Boolean[] __tmp117 = f_tmp_134;
				res[i]=__tmp117;
				Boolean[] f_tmp_135 = res[i];
				Boolean[] __tmp118 = f_tmp_135;
				Boolean[] f_tmp_136 = res[j];
				Boolean[] __tmp119 = f_tmp_136;
				Boolean[] f_tmp_137 = lib.xor(__tmp118,__tmp119);
				Boolean[] __tmp120 = f_tmp_137;
				Boolean[] f_tmp_138 = res[j];
				Boolean[] __tmp121 = f_tmp_138;
				Boolean[] f_tmp_139 = lib.mux(__tmp121, __tmp120,__tmp112);
				Boolean[] __tmp122 = f_tmp_139;
				res[j]=__tmp122;
				Boolean[] f_tmp_140 = res[i];
				Boolean[] __tmp123 = f_tmp_140;
				Boolean[] f_tmp_141 = res[j];
				Boolean[] __tmp124 = f_tmp_141;
				Boolean[] f_tmp_142 = lib.xor(__tmp123,__tmp124);
				Boolean[] __tmp125 = f_tmp_142;
				Boolean[] f_tmp_143 = res[i];
				Boolean[] __tmp126 = f_tmp_143;
				Boolean[] f_tmp_144 = lib.mux(__tmp126, __tmp125,__tmp112);
				Boolean[] __tmp127 = f_tmp_144;
				res[i]=__tmp127;
				Boolean f_tmp_145 = lib.not(__tmp112);
				Boolean __tmp128 = f_tmp_145;
				int f_tmp_146 = 1;
				int __tmp129 = f_tmp_146;
				int f_tmp_147 = j + __tmp129;
				int __tmp130 = f_tmp_147;
				j = __tmp130;
				int f_tmp_148 = 10;
				__tmp108 = f_tmp_148;
				boolean f_tmp_149 = j < __tmp108;
				__tmp109 = f_tmp_149;
			}
			int f_tmp_150 = 1;
			int __tmp131 = f_tmp_150;
			int f_tmp_151 = i + __tmp131;
			int __tmp132 = f_tmp_151;
			i = __tmp132;
			int f_tmp_152 = 10;
			__tmp105 = f_tmp_152;
			boolean f_tmp_153 = i < __tmp105;
			__tmp106 = f_tmp_153;
		}
		int f_tmp_154 = 5;
		int __tmp133 = f_tmp_154;
		Boolean[] f_tmp_155 = res[__tmp133];
		Boolean[] __tmp134 = f_tmp_155;
		return __tmp134;
	}
}