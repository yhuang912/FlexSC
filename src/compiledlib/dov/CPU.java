package compiledlib.dov;
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
public class CPU implements IWritable<CPU, Boolean> {

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;

	public CPU(CompEnv<Boolean> env, IntegerLib<Boolean> lib) throws Exception {
		this.env = env;
		this.lib = lib;
	}

	public int numBits() {
		return 0;
	}
	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp_b;
		Boolean tmp;
		int now = 0;
		return ret;
}

	public CPU newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		CPU ret = new CPU(env, lib);
		Boolean[] tmp;
		int now = 0;
		return ret;
}

	public Boolean[] function(SecureArray<Boolean> reg, Boolean[] inst, Boolean[] pc) throws Exception {
		Boolean[] i = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] op = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] rt = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] rs = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] rd = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] unsignExt = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] zeroExt = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] funct = env.inputOfAlice(Utils.fromInt(0, 32));
		SecureArray<Boolean> b = new SecureArray<Boolean>(env, 64, 32);
		int k = 0;
		int f_tmp_0 = 0;
		int __tmp0 = f_tmp_0;
		i = env.inputOfAlice(Utils.fromInt(__tmp0, 32));
		Boolean[] f_tmp_1 = lib.rightPublicShift(inst, 26);
		Boolean[] __tmp1 = f_tmp_1;
		op = __tmp1;
		Boolean[] f_tmp_2 = lib.leftPublicShift(inst, 11);
		Boolean[] __tmp2 = f_tmp_2;
		Boolean[] f_tmp_3 = lib.rightPublicShift(__tmp2, 27);
		Boolean[] __tmp3 = f_tmp_3;
		rt = __tmp3;
		Boolean[] f_tmp_4 = lib.leftPublicShift(inst, 6);
		Boolean[] __tmp4 = f_tmp_4;
		Boolean[] f_tmp_5 = lib.rightPublicShift(__tmp4, 27);
		Boolean[] __tmp5 = f_tmp_5;
		rs = __tmp5;
		Boolean[] f_tmp_6 = lib.leftPublicShift(inst, 16);
		Boolean[] __tmp6 = f_tmp_6;
		Boolean[] f_tmp_7 = lib.rightPublicShift(__tmp6, 27);
		Boolean[] __tmp7 = f_tmp_7;
		rd = __tmp7;
		Boolean[] f_tmp_8 = lib.leftPublicShift(inst, 16);
		Boolean[] __tmp8 = f_tmp_8;
		Boolean[] f_tmp_9 = lib.rightPublicShift(__tmp8, 16);
		Boolean[] __tmp9 = f_tmp_9;
		unsignExt = __tmp9;
		zeroExt = unsignExt;
		int f_tmp_10 = 0;
		int __tmp10 = f_tmp_10;
		funct = env.inputOfAlice(Utils.fromInt(__tmp10, 32));
		int f_tmp_11 = 0;
		int __tmp11 = f_tmp_11;
		k = __tmp11;
		int f_tmp_12 = 32;
		int __tmp12 = f_tmp_12;
		boolean f_tmp_13 = k < __tmp12;
		boolean __tmp13 = f_tmp_13;
		while(__tmp13) {
			Boolean[] f_tmp_15 = env.inputOfAlice(Utils.fromInt(k, 32));
			Boolean[] f_tmp_14 = reg.read(f_tmp_15);
			Boolean[] __tmp14 = f_tmp_14;
			Boolean[] f_tmp_16 = env.inputOfAlice(Utils.fromInt(k, 32));
			b.write(f_tmp_16,__tmp14);
			int f_tmp_17 = 1;
			int __tmp15 = f_tmp_17;
			int f_tmp_18 = k + __tmp15;
			int __tmp16 = f_tmp_18;
			k = __tmp16;
			int f_tmp_19 = 32;
			__tmp12 = f_tmp_19;
			boolean f_tmp_20 = k < __tmp12;
			__tmp13 = f_tmp_20;
		}
		Boolean[] f_tmp_21 = lib.rightPublicShift(unsignExt, 15);
		Boolean[] __tmp17 = f_tmp_21;
		int f_tmp_22 = 0;
		int __tmp18 = f_tmp_22;
		Boolean[] f_tmp_24 = env.inputOfAlice(Utils.fromInt(__tmp18, 32));
		Boolean f_tmp_23 = lib.not(lib.eq(__tmp17, f_tmp_24));
		Boolean __tmp19 = f_tmp_23;
		int f_tmp_25 = -65536;
		int __tmp20 = f_tmp_25;
		Boolean[] f_tmp_27 = env.inputOfAlice(Utils.fromInt(__tmp20, 32));
		Boolean[] f_tmp_26 = lib.add(unsignExt,f_tmp_27);
		Boolean[] __tmp21 = f_tmp_26;
		Boolean[] f_tmp_28 = lib.mux(unsignExt, __tmp21,__tmp19);
		Boolean[] __tmp22 = f_tmp_28;
		unsignExt = __tmp22;
		Boolean f_tmp_29 = lib.not(__tmp19);
		Boolean __tmp23 = f_tmp_29;
		int f_tmp_30 = 9;
		int __tmp24 = f_tmp_30;
		Boolean[] f_tmp_32 = env.inputOfAlice(Utils.fromInt(__tmp24, 32));
		Boolean f_tmp_31 = lib.eq(op, f_tmp_32);
		Boolean __tmp25 = f_tmp_31;
		Boolean[] f_tmp_33 = reg.read(rs);
		Boolean[] __tmp26 = f_tmp_33;
		Boolean[] f_tmp_34 = lib.add(__tmp26,unsignExt);
		Boolean[] __tmp27 = f_tmp_34;
		Boolean[] f_tmp_35 = reg.read(rt);
		Boolean[] __tmp28 = f_tmp_35;
		Boolean[] f_tmp_36 = lib.mux(__tmp28, __tmp27,__tmp25);
		Boolean[] __tmp29 = f_tmp_36;
		reg.write(rt,__tmp29);
		Boolean f_tmp_37 = lib.not(__tmp25);
		Boolean __tmp30 = f_tmp_37;
		int f_tmp_38 = 3;
		int __tmp31 = f_tmp_38;
		Boolean[] f_tmp_40 = env.inputOfAlice(Utils.fromInt(__tmp31, 32));
		Boolean f_tmp_39 = lib.eq(op, f_tmp_40);
		Boolean __tmp32 = f_tmp_39;
		int f_tmp_41 = 1;
		int __tmp33 = f_tmp_41;
		Boolean[] f_tmp_43 = env.inputOfAlice(Utils.fromInt(__tmp33, 32));
		Boolean f_tmp_42 = lib.eq(op, f_tmp_43);
		Boolean __tmp34 = f_tmp_42;
		Boolean f_tmp_44 = lib.or(__tmp32,__tmp34);
		Boolean __tmp35 = f_tmp_44;
		Boolean f_tmp_45 = lib.and(__tmp30,__tmp35);
		Boolean __tmp36 = f_tmp_45;
		int f_tmp_46 = 8;
		int __tmp37 = f_tmp_46;
		Boolean[] f_tmp_48 = env.inputOfAlice(Utils.fromInt(__tmp37, 32));
		Boolean[] f_tmp_47 = lib.add(pc,f_tmp_48);
		Boolean[] __tmp38 = f_tmp_47;
		int f_tmp_49 = 31;
		int __tmp39 = f_tmp_49;
		Boolean[] f_tmp_51 = env.inputOfAlice(Utils.fromInt(__tmp39, 32));
		Boolean[] f_tmp_50 = reg.read(f_tmp_51);
		Boolean[] __tmp40 = f_tmp_50;
		Boolean[] f_tmp_52 = lib.mux(__tmp40, __tmp38,__tmp36);
		Boolean[] __tmp41 = f_tmp_52;
		Boolean[] f_tmp_53 = env.inputOfAlice(Utils.fromInt(__tmp39, 32));
		reg.write(f_tmp_53,__tmp41);
		Boolean f_tmp_54 = lib.not(__tmp35);
		Boolean __tmp42 = f_tmp_54;
		Boolean f_tmp_55 = lib.and(__tmp30,__tmp42);
		Boolean __tmp43 = f_tmp_55;
		int f_tmp_56 = 12;
		int __tmp44 = f_tmp_56;
		Boolean[] f_tmp_58 = env.inputOfAlice(Utils.fromInt(__tmp44, 32));
		Boolean f_tmp_57 = lib.eq(op, f_tmp_58);
		Boolean __tmp45 = f_tmp_57;
		Boolean f_tmp_59 = lib.and(__tmp43,__tmp45);
		Boolean __tmp46 = f_tmp_59;
		Boolean[] f_tmp_60 = reg.read(rs);
		Boolean[] __tmp47 = f_tmp_60;
		Boolean[] f_tmp_61 = lib.and(__tmp47,zeroExt);
		Boolean[] __tmp48 = f_tmp_61;
		Boolean[] f_tmp_62 = reg.read(rt);
		Boolean[] __tmp49 = f_tmp_62;
		Boolean[] f_tmp_63 = lib.mux(__tmp49, __tmp48,__tmp46);
		Boolean[] __tmp50 = f_tmp_63;
		reg.write(rt,__tmp50);
		Boolean f_tmp_64 = lib.not(__tmp45);
		Boolean __tmp51 = f_tmp_64;
		Boolean f_tmp_65 = lib.and(__tmp43,__tmp51);
		Boolean __tmp52 = f_tmp_65;
		int f_tmp_66 = 0;
		int __tmp53 = f_tmp_66;
		Boolean[] f_tmp_68 = env.inputOfAlice(Utils.fromInt(__tmp53, 32));
		Boolean f_tmp_67 = lib.eq(op, f_tmp_68);
		Boolean __tmp54 = f_tmp_67;
		Boolean f_tmp_69 = lib.and(__tmp52,__tmp54);
		Boolean __tmp55 = f_tmp_69;
		Boolean[] f_tmp_70 = lib.leftPublicShift(inst, 26);
		Boolean[] __tmp56 = f_tmp_70;
		Boolean[] f_tmp_71 = lib.rightPublicShift(__tmp56, 26);
		Boolean[] __tmp57 = f_tmp_71;
		Boolean[] f_tmp_72 = lib.mux(funct, __tmp57,__tmp55);
		Boolean[] __tmp58 = f_tmp_72;
		funct = __tmp58;
		int f_tmp_73 = 33;
		int __tmp59 = f_tmp_73;
		Boolean[] f_tmp_75 = env.inputOfAlice(Utils.fromInt(__tmp59, 32));
		Boolean f_tmp_74 = lib.eq(funct, f_tmp_75);
		Boolean __tmp60 = f_tmp_74;
		Boolean f_tmp_76 = lib.and(__tmp55,__tmp60);
		Boolean __tmp61 = f_tmp_76;
		Boolean[] f_tmp_77 = reg.read(rs);
		Boolean[] __tmp62 = f_tmp_77;
		Boolean[] f_tmp_78 = reg.read(rt);
		Boolean[] __tmp63 = f_tmp_78;
		Boolean[] f_tmp_79 = lib.add(__tmp62,__tmp63);
		Boolean[] __tmp64 = f_tmp_79;
		Boolean[] f_tmp_80 = reg.read(rd);
		Boolean[] __tmp65 = f_tmp_80;
		Boolean[] f_tmp_81 = lib.mux(__tmp65, __tmp64,__tmp61);
		Boolean[] __tmp66 = f_tmp_81;
		reg.write(rd,__tmp66);
		Boolean f_tmp_82 = lib.not(__tmp60);
		Boolean __tmp67 = f_tmp_82;
		Boolean f_tmp_83 = lib.and(__tmp55,__tmp67);
		Boolean __tmp68 = f_tmp_83;
		int f_tmp_84 = 38;
		int __tmp69 = f_tmp_84;
		Boolean[] f_tmp_86 = env.inputOfAlice(Utils.fromInt(__tmp69, 32));
		Boolean f_tmp_85 = lib.eq(funct, f_tmp_86);
		Boolean __tmp70 = f_tmp_85;
		Boolean f_tmp_87 = lib.and(__tmp68,__tmp70);
		Boolean __tmp71 = f_tmp_87;
		Boolean[] f_tmp_88 = reg.read(rs);
		Boolean[] __tmp72 = f_tmp_88;
		Boolean[] f_tmp_89 = reg.read(rt);
		Boolean[] __tmp73 = f_tmp_89;
		Boolean[] f_tmp_90 = lib.xor(__tmp72,__tmp73);
		Boolean[] __tmp74 = f_tmp_90;
		Boolean[] f_tmp_91 = reg.read(rd);
		Boolean[] __tmp75 = f_tmp_91;
		Boolean[] f_tmp_92 = lib.mux(__tmp75, __tmp74,__tmp71);
		Boolean[] __tmp76 = f_tmp_92;
		reg.write(rd,__tmp76);
		Boolean f_tmp_93 = lib.not(__tmp70);
		Boolean __tmp77 = f_tmp_93;
		Boolean f_tmp_94 = lib.and(__tmp68,__tmp77);
		Boolean __tmp78 = f_tmp_94;
		int f_tmp_95 = 42;
		int __tmp79 = f_tmp_95;
		Boolean[] f_tmp_97 = env.inputOfAlice(Utils.fromInt(__tmp79, 32));
		Boolean f_tmp_96 = lib.eq(funct, f_tmp_97);
		Boolean __tmp80 = f_tmp_96;
		Boolean f_tmp_98 = lib.and(__tmp78,__tmp80);
		Boolean __tmp81 = f_tmp_98;
		Boolean[] f_tmp_99 = reg.read(rs);
		Boolean[] __tmp82 = f_tmp_99;
		Boolean[] f_tmp_100 = reg.read(rt);
		Boolean[] __tmp83 = f_tmp_100;
		Boolean f_tmp_101 = lib.not(lib.geq(__tmp82, __tmp83));
		Boolean __tmp84 = f_tmp_101;
		Boolean f_tmp_102 = lib.and(__tmp81,__tmp84);
		Boolean __tmp85 = f_tmp_102;
		int f_tmp_103 = 1;
		int __tmp86 = f_tmp_103;
		Boolean[] f_tmp_104 = reg.read(rd);
		Boolean[] __tmp87 = f_tmp_104;
		Boolean[] f_tmp_106 = env.inputOfAlice(Utils.fromInt(__tmp86, 32));
		Boolean[] f_tmp_105 = lib.mux(__tmp87, f_tmp_106,__tmp85);
		Boolean[] __tmp88 = f_tmp_105;
		reg.write(rd,__tmp88);
		Boolean f_tmp_107 = lib.not(__tmp84);
		Boolean __tmp89 = f_tmp_107;
		Boolean f_tmp_108 = lib.and(__tmp81,__tmp89);
		Boolean __tmp90 = f_tmp_108;
		int f_tmp_109 = 0;
		int __tmp91 = f_tmp_109;
		Boolean[] f_tmp_110 = reg.read(rd);
		Boolean[] __tmp92 = f_tmp_110;
		Boolean[] f_tmp_112 = env.inputOfAlice(Utils.fromInt(__tmp91, 32));
		Boolean[] f_tmp_111 = lib.mux(__tmp92, f_tmp_112,__tmp90);
		Boolean[] __tmp93 = f_tmp_111;
		reg.write(rd,__tmp93);
		Boolean f_tmp_113 = lib.not(__tmp80);
		Boolean __tmp94 = f_tmp_113;
		Boolean f_tmp_114 = lib.and(__tmp78,__tmp94);
		Boolean __tmp95 = f_tmp_114;
		int f_tmp_115 = 35;
		int __tmp96 = f_tmp_115;
		Boolean[] f_tmp_117 = env.inputOfAlice(Utils.fromInt(__tmp96, 32));
		Boolean f_tmp_116 = lib.eq(funct, f_tmp_117);
		Boolean __tmp97 = f_tmp_116;
		Boolean f_tmp_118 = lib.and(__tmp95,__tmp97);
		Boolean __tmp98 = f_tmp_118;
		Boolean[] f_tmp_119 = reg.read(rs);
		Boolean[] __tmp99 = f_tmp_119;
		Boolean[] f_tmp_120 = reg.read(rt);
		Boolean[] __tmp100 = f_tmp_120;
		Boolean[] f_tmp_121 = lib.sub(__tmp99,__tmp100);
		Boolean[] __tmp101 = f_tmp_121;
		Boolean[] f_tmp_122 = reg.read(rd);
		Boolean[] __tmp102 = f_tmp_122;
		Boolean[] f_tmp_123 = lib.mux(__tmp102, __tmp101,__tmp98);
		Boolean[] __tmp103 = f_tmp_123;
		reg.write(rd,__tmp103);
		Boolean f_tmp_124 = lib.not(__tmp97);
		Boolean __tmp104 = f_tmp_124;
		Boolean f_tmp_125 = lib.and(__tmp95,__tmp104);
		Boolean __tmp105 = f_tmp_125;
		Boolean f_tmp_126 = lib.not(__tmp54);
		Boolean __tmp106 = f_tmp_126;
		Boolean f_tmp_127 = lib.and(__tmp52,__tmp106);
		Boolean __tmp107 = f_tmp_127;
		int f_tmp_128 = 8;
		int __tmp108 = f_tmp_128;
		Boolean[] f_tmp_130 = env.inputOfAlice(Utils.fromInt(__tmp108, 32));
		Boolean f_tmp_129 = lib.eq(op, f_tmp_130);
		Boolean __tmp109 = f_tmp_129;
		Boolean[] f_tmp_131 = reg.read(rs);
		Boolean[] __tmp110 = f_tmp_131;
		Boolean[] f_tmp_132 = lib.mux(pc, __tmp110,__tmp109);
		Boolean[] __tmp111 = f_tmp_132;
		pc = __tmp111;
		Boolean f_tmp_133 = lib.not(__tmp109);
		Boolean __tmp112 = f_tmp_133;
		int f_tmp_134 = 3;
		int __tmp113 = f_tmp_134;
		Boolean[] f_tmp_136 = env.inputOfAlice(Utils.fromInt(__tmp113, 32));
		Boolean f_tmp_135 = lib.eq(op, f_tmp_136);
		Boolean __tmp114 = f_tmp_135;
		Boolean f_tmp_137 = lib.and(__tmp112,__tmp114);
		Boolean __tmp115 = f_tmp_137;
		Boolean[] f_tmp_138 = lib.leftPublicShift(inst, 6);
		Boolean[] __tmp116 = f_tmp_138;
		Boolean[] f_tmp_139 = lib.rightPublicShift(__tmp116, 6);
		Boolean[] __tmp117 = f_tmp_139;
		Boolean[] f_tmp_140 = lib.mux(pc, __tmp117,__tmp115);
		Boolean[] __tmp118 = f_tmp_140;
		pc = __tmp118;
		Boolean f_tmp_141 = lib.not(__tmp114);
		Boolean __tmp119 = f_tmp_141;
		Boolean f_tmp_142 = lib.and(__tmp112,__tmp119);
		Boolean __tmp120 = f_tmp_142;
		int f_tmp_143 = 5;
		int __tmp121 = f_tmp_143;
		Boolean[] f_tmp_145 = env.inputOfAlice(Utils.fromInt(__tmp121, 32));
		Boolean f_tmp_144 = lib.eq(op, f_tmp_145);
		Boolean __tmp122 = f_tmp_144;
		Boolean[] f_tmp_146 = reg.read(rs);
		Boolean[] __tmp123 = f_tmp_146;
		Boolean[] f_tmp_147 = reg.read(rt);
		Boolean[] __tmp124 = f_tmp_147;
		Boolean f_tmp_148 = lib.not(lib.eq(__tmp123, __tmp124));
		Boolean __tmp125 = f_tmp_148;
		Boolean f_tmp_149 = lib.and(__tmp122,__tmp125);
		Boolean __tmp126 = f_tmp_149;
		int f_tmp_150 = 4;
		int __tmp127 = f_tmp_150;
		Boolean[] f_tmp_152 = env.inputOfAlice(Utils.fromInt(__tmp127, 32));
		Boolean f_tmp_151 = lib.eq(op, f_tmp_152);
		Boolean __tmp128 = f_tmp_151;
		Boolean[] f_tmp_153 = reg.read(rs);
		Boolean[] __tmp129 = f_tmp_153;
		Boolean[] f_tmp_154 = reg.read(rt);
		Boolean[] __tmp130 = f_tmp_154;
		Boolean f_tmp_155 = lib.eq(__tmp129, __tmp130);
		Boolean __tmp131 = f_tmp_155;
		Boolean f_tmp_156 = lib.and(__tmp128,__tmp131);
		Boolean __tmp132 = f_tmp_156;
		Boolean f_tmp_157 = lib.or(__tmp126,__tmp132);
		Boolean __tmp133 = f_tmp_157;
		Boolean f_tmp_158 = lib.and(__tmp120,__tmp133);
		Boolean __tmp134 = f_tmp_158;
		Boolean[] f_tmp_159 = lib.leftPublicShift(unsignExt, 2);
		Boolean[] __tmp135 = f_tmp_159;
		Boolean[] f_tmp_160 = lib.add(pc,__tmp135);
		Boolean[] __tmp136 = f_tmp_160;
		Boolean[] f_tmp_161 = lib.mux(pc, __tmp136,__tmp134);
		Boolean[] __tmp137 = f_tmp_161;
		pc = __tmp137;
		Boolean f_tmp_162 = lib.not(__tmp133);
		Boolean __tmp138 = f_tmp_162;
		Boolean f_tmp_163 = lib.and(__tmp120,__tmp138);
		Boolean __tmp139 = f_tmp_163;
		int f_tmp_164 = 4;
		int __tmp140 = f_tmp_164;
		Boolean[] f_tmp_166 = env.inputOfAlice(Utils.fromInt(__tmp140, 32));
		Boolean[] f_tmp_165 = lib.add(pc,f_tmp_166);
		Boolean[] __tmp141 = f_tmp_165;
		Boolean[] f_tmp_167 = lib.mux(pc, __tmp141,__tmp139);
		Boolean[] __tmp142 = f_tmp_167;
		pc = __tmp142;
		return pc;
	}
}
