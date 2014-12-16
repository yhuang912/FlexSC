package compiledlib.dov;
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
public class CPU implements IWritable<CPU, Boolean> {

	public CompEnv<Boolean> env;
	public IntegerLib<Boolean> intLib;
	public FloatLib<Boolean> floatLib;

	public CPU(CompEnv<Boolean> env) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<Boolean>(env);
		this.floatLib = new FloatLib<Boolean>(env, 24, 8);
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
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		CPU ret = new CPU(env);
		Boolean[] tmp;
		int now = 0;
		return ret;
}

	public Boolean checkTerminate(Boolean[] inst) throws Exception {
		Boolean res = env.inputOfAlice(false);
		boolean f_tmp_0 = false;
		boolean __tmp0 = f_tmp_0;
		res = env.inputOfAlice(__tmp0);
		int f_tmp_1 = 268437280;
		int __tmp1 = f_tmp_1;
		Boolean[] f_tmp_3 = env.inputOfAlice(Utils.fromInt(__tmp1, 32));
		Boolean f_tmp_2 = intLib.eq(inst, f_tmp_3);
		Boolean __tmp2 = f_tmp_2;
		boolean f_tmp_4 = true;
		boolean __tmp3 = f_tmp_4;
		Boolean f_tmp_6 = env.inputOfAlice(__tmp3);
		Boolean f_tmp_5 = intLib.mux(res, f_tmp_6,__tmp2);
		Boolean __tmp4 = f_tmp_5;
		res = __tmp4;
		Boolean f_tmp_7 = intLib.not(__tmp2);
		Boolean __tmp5 = f_tmp_7;
		return res;
	}
	public Boolean[] function(SecureArray<Boolean> reg, Boolean[] inst, Boolean[] pc) throws Exception {
		Boolean[] op = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] rt = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] rs = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] rd = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] unsignExt = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] zeroExt = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] funct = env.inputOfAlice(Utils.fromInt(0, 32));
		int f_tmp_8 = 26;
		int __tmp6 = f_tmp_8;
		Boolean[] f_tmp_9 = intLib.rightPublicShift(inst, __tmp6);
		Boolean[] __tmp7 = f_tmp_9;
		op = __tmp7;
		int f_tmp_10 = 11;
		int __tmp8 = f_tmp_10;
		Boolean[] f_tmp_11 = intLib.leftPublicShift(inst, __tmp8);
		Boolean[] __tmp9 = f_tmp_11;
		int f_tmp_12 = 27;
		int __tmp10 = f_tmp_12;
		Boolean[] f_tmp_13 = intLib.rightPublicShift(__tmp9, __tmp10);
		Boolean[] __tmp11 = f_tmp_13;
		rt = __tmp11;
		int f_tmp_14 = 6;
		int __tmp12 = f_tmp_14;
		Boolean[] f_tmp_15 = intLib.leftPublicShift(inst, __tmp12);
		Boolean[] __tmp13 = f_tmp_15;
		int f_tmp_16 = 27;
		int __tmp14 = f_tmp_16;
		Boolean[] f_tmp_17 = intLib.rightPublicShift(__tmp13, __tmp14);
		Boolean[] __tmp15 = f_tmp_17;
		rs = __tmp15;
		int f_tmp_18 = 16;
		int __tmp16 = f_tmp_18;
		Boolean[] f_tmp_19 = intLib.leftPublicShift(inst, __tmp16);
		Boolean[] __tmp17 = f_tmp_19;
		int f_tmp_20 = 27;
		int __tmp18 = f_tmp_20;
		Boolean[] f_tmp_21 = intLib.rightPublicShift(__tmp17, __tmp18);
		Boolean[] __tmp19 = f_tmp_21;
		rd = __tmp19;
		int f_tmp_22 = 16;
		int __tmp20 = f_tmp_22;
		Boolean[] f_tmp_23 = intLib.leftPublicShift(inst, __tmp20);
		Boolean[] __tmp21 = f_tmp_23;
		int f_tmp_24 = 16;
		int __tmp22 = f_tmp_24;
		Boolean[] f_tmp_25 = intLib.rightPublicShift(__tmp21, __tmp22);
		Boolean[] __tmp23 = f_tmp_25;
		unsignExt = __tmp23;
		zeroExt = unsignExt;
		int f_tmp_26 = 0;
		int __tmp24 = f_tmp_26;
		funct = env.inputOfAlice(Utils.fromInt(__tmp24, 32));
		int f_tmp_27 = 15;
		int __tmp25 = f_tmp_27;
		Boolean[] f_tmp_28 = intLib.rightPublicShift(unsignExt, __tmp25);
		Boolean[] __tmp26 = f_tmp_28;
		int f_tmp_29 = 0;
		int __tmp27 = f_tmp_29;
		Boolean[] f_tmp_31 = env.inputOfAlice(Utils.fromInt(__tmp27, 32));
		Boolean f_tmp_30 = intLib.not(intLib.eq(__tmp26, f_tmp_31));
		Boolean __tmp28 = f_tmp_30;
		int f_tmp_32 = -65536;
		int __tmp29 = f_tmp_32;
		Boolean[] f_tmp_34 = env.inputOfAlice(Utils.fromInt(__tmp29, 32));
		Boolean[] f_tmp_33 = intLib.add(unsignExt,f_tmp_34);
		Boolean[] __tmp30 = f_tmp_33;
		Boolean[] f_tmp_35 = intLib.mux(unsignExt, __tmp30,__tmp28);
		Boolean[] __tmp31 = f_tmp_35;
		unsignExt = __tmp31;
		Boolean f_tmp_36 = intLib.not(__tmp28);
		Boolean __tmp32 = f_tmp_36;
		int f_tmp_37 = 9;
		int __tmp33 = f_tmp_37;
		Boolean[] f_tmp_39 = env.inputOfAlice(Utils.fromInt(__tmp33, 32));
		Boolean f_tmp_38 = intLib.eq(op, f_tmp_39);
		Boolean __tmp34 = f_tmp_38;
		Boolean[] f_tmp_40 = reg.read(rs);
		Boolean[] __tmp35 = f_tmp_40;
		Boolean[] f_tmp_41 = intLib.add(__tmp35,unsignExt);
		Boolean[] __tmp36 = f_tmp_41;
		Boolean[] f_tmp_42 = reg.read(rt);
		Boolean[] __tmp37 = f_tmp_42;
		Boolean[] f_tmp_43 = intLib.mux(__tmp37, __tmp36,__tmp34);
		Boolean[] __tmp38 = f_tmp_43;
		reg.write(rt,__tmp38);
		Boolean f_tmp_44 = intLib.not(__tmp34);
		Boolean __tmp39 = f_tmp_44;
		int f_tmp_45 = 3;
		int __tmp40 = f_tmp_45;
		Boolean[] f_tmp_47 = env.inputOfAlice(Utils.fromInt(__tmp40, 32));
		Boolean f_tmp_46 = intLib.eq(op, f_tmp_47);
		Boolean __tmp41 = f_tmp_46;
		Boolean f_tmp_48 = intLib.and(__tmp39,__tmp41);
		Boolean __tmp42 = f_tmp_48;
		int f_tmp_49 = 8;
		int __tmp43 = f_tmp_49;
		Boolean[] f_tmp_51 = env.inputOfAlice(Utils.fromInt(__tmp43, 32));
		Boolean[] f_tmp_50 = intLib.add(pc,f_tmp_51);
		Boolean[] __tmp44 = f_tmp_50;
		int f_tmp_52 = 31;
		int __tmp45 = f_tmp_52;
		Boolean[] f_tmp_54 = env.inputOfAlice(Utils.fromInt(__tmp45, 32));
		Boolean[] f_tmp_53 = reg.read(f_tmp_54);
		Boolean[] __tmp46 = f_tmp_53;
		Boolean[] f_tmp_55 = intLib.mux(__tmp46, __tmp44,__tmp42);
		Boolean[] __tmp47 = f_tmp_55;
		Boolean[] f_tmp_56 = env.inputOfAlice(Utils.fromInt(__tmp45, 32));
		reg.write(f_tmp_56,__tmp47);
		int f_tmp_57 = 6;
		int __tmp48 = f_tmp_57;
		Boolean[] f_tmp_58 = intLib.leftPublicShift(inst, __tmp48);
		Boolean[] __tmp49 = f_tmp_58;
		int f_tmp_59 = 6;
		int __tmp50 = f_tmp_59;
		Boolean[] f_tmp_60 = intLib.rightPublicShift(__tmp49, __tmp50);
		Boolean[] __tmp51 = f_tmp_60;
		Boolean[] f_tmp_61 = intLib.mux(pc, __tmp51,__tmp42);
		Boolean[] __tmp52 = f_tmp_61;
		pc = __tmp52;
		Boolean f_tmp_62 = intLib.not(__tmp41);
		Boolean __tmp53 = f_tmp_62;
		Boolean f_tmp_63 = intLib.and(__tmp39,__tmp53);
		Boolean __tmp54 = f_tmp_63;
		int f_tmp_64 = 1;
		int __tmp55 = f_tmp_64;
		Boolean[] f_tmp_66 = env.inputOfAlice(Utils.fromInt(__tmp55, 32));
		Boolean f_tmp_65 = intLib.eq(op, f_tmp_66);
		Boolean __tmp56 = f_tmp_65;
		Boolean f_tmp_67 = intLib.and(__tmp54,__tmp56);
		Boolean __tmp57 = f_tmp_67;
		int f_tmp_68 = 8;
		int __tmp58 = f_tmp_68;
		Boolean[] f_tmp_70 = env.inputOfAlice(Utils.fromInt(__tmp58, 32));
		Boolean[] f_tmp_69 = intLib.add(pc,f_tmp_70);
		Boolean[] __tmp59 = f_tmp_69;
		int f_tmp_71 = 31;
		int __tmp60 = f_tmp_71;
		Boolean[] f_tmp_73 = env.inputOfAlice(Utils.fromInt(__tmp60, 32));
		Boolean[] f_tmp_72 = reg.read(f_tmp_73);
		Boolean[] __tmp61 = f_tmp_72;
		Boolean[] f_tmp_74 = intLib.mux(__tmp61, __tmp59,__tmp57);
		Boolean[] __tmp62 = f_tmp_74;
		Boolean[] f_tmp_75 = env.inputOfAlice(Utils.fromInt(__tmp60, 32));
		reg.write(f_tmp_75,__tmp62);
		Boolean[] f_tmp_76 = reg.read(rt);
		Boolean[] __tmp63 = f_tmp_76;
		int f_tmp_77 = 0;
		int __tmp64 = f_tmp_77;
		Boolean[] f_tmp_79 = env.inputOfAlice(Utils.fromInt(__tmp64, 32));
		Boolean f_tmp_78 = intLib.geq(__tmp63, f_tmp_79);
		Boolean __tmp65 = f_tmp_78;
		Boolean f_tmp_80 = intLib.and(__tmp57,__tmp65);
		Boolean __tmp66 = f_tmp_80;
		int f_tmp_81 = 2;
		int __tmp67 = f_tmp_81;
		Boolean[] f_tmp_82 = intLib.leftPublicShift(unsignExt, __tmp67);
		Boolean[] __tmp68 = f_tmp_82;
		Boolean[] f_tmp_83 = intLib.add(pc,__tmp68);
		Boolean[] __tmp69 = f_tmp_83;
		Boolean[] f_tmp_84 = intLib.mux(pc, __tmp69,__tmp66);
		Boolean[] __tmp70 = f_tmp_84;
		pc = __tmp70;
		Boolean f_tmp_85 = intLib.not(__tmp65);
		Boolean __tmp71 = f_tmp_85;
		Boolean f_tmp_86 = intLib.and(__tmp57,__tmp71);
		Boolean __tmp72 = f_tmp_86;
		Boolean f_tmp_87 = intLib.not(__tmp56);
		Boolean __tmp73 = f_tmp_87;
		Boolean f_tmp_88 = intLib.and(__tmp54,__tmp73);
		Boolean __tmp74 = f_tmp_88;
		int f_tmp_89 = 12;
		int __tmp75 = f_tmp_89;
		Boolean[] f_tmp_91 = env.inputOfAlice(Utils.fromInt(__tmp75, 32));
		Boolean f_tmp_90 = intLib.eq(op, f_tmp_91);
		Boolean __tmp76 = f_tmp_90;
		Boolean f_tmp_92 = intLib.and(__tmp74,__tmp76);
		Boolean __tmp77 = f_tmp_92;
		Boolean[] f_tmp_93 = reg.read(rs);
		Boolean[] __tmp78 = f_tmp_93;
		Boolean[] f_tmp_94 = intLib.and(__tmp78,zeroExt);
		Boolean[] __tmp79 = f_tmp_94;
		Boolean[] f_tmp_95 = reg.read(rt);
		Boolean[] __tmp80 = f_tmp_95;
		Boolean[] f_tmp_96 = intLib.mux(__tmp80, __tmp79,__tmp77);
		Boolean[] __tmp81 = f_tmp_96;
		reg.write(rt,__tmp81);
		Boolean f_tmp_97 = intLib.not(__tmp76);
		Boolean __tmp82 = f_tmp_97;
		Boolean f_tmp_98 = intLib.and(__tmp74,__tmp82);
		Boolean __tmp83 = f_tmp_98;
		int f_tmp_99 = 0;
		int __tmp84 = f_tmp_99;
		Boolean[] f_tmp_101 = env.inputOfAlice(Utils.fromInt(__tmp84, 32));
		Boolean f_tmp_100 = intLib.eq(op, f_tmp_101);
		Boolean __tmp85 = f_tmp_100;
		Boolean f_tmp_102 = intLib.and(__tmp83,__tmp85);
		Boolean __tmp86 = f_tmp_102;
		int f_tmp_103 = 26;
		int __tmp87 = f_tmp_103;
		Boolean[] f_tmp_104 = intLib.leftPublicShift(inst, __tmp87);
		Boolean[] __tmp88 = f_tmp_104;
		int f_tmp_105 = 26;
		int __tmp89 = f_tmp_105;
		Boolean[] f_tmp_106 = intLib.rightPublicShift(__tmp88, __tmp89);
		Boolean[] __tmp90 = f_tmp_106;
		Boolean[] f_tmp_107 = intLib.mux(funct, __tmp90,__tmp86);
		Boolean[] __tmp91 = f_tmp_107;
		funct = __tmp91;
		int f_tmp_108 = 33;
		int __tmp92 = f_tmp_108;
		Boolean[] f_tmp_110 = env.inputOfAlice(Utils.fromInt(__tmp92, 32));
		Boolean f_tmp_109 = intLib.eq(funct, f_tmp_110);
		Boolean __tmp93 = f_tmp_109;
		Boolean f_tmp_111 = intLib.and(__tmp86,__tmp93);
		Boolean __tmp94 = f_tmp_111;
		Boolean[] f_tmp_112 = reg.read(rs);
		Boolean[] __tmp95 = f_tmp_112;
		Boolean[] f_tmp_113 = reg.read(rt);
		Boolean[] __tmp96 = f_tmp_113;
		Boolean[] f_tmp_114 = intLib.add(__tmp95,__tmp96);
		Boolean[] __tmp97 = f_tmp_114;
		Boolean[] f_tmp_115 = reg.read(rd);
		Boolean[] __tmp98 = f_tmp_115;
		Boolean[] f_tmp_116 = intLib.mux(__tmp98, __tmp97,__tmp94);
		Boolean[] __tmp99 = f_tmp_116;
		reg.write(rd,__tmp99);
		Boolean f_tmp_117 = intLib.not(__tmp93);
		Boolean __tmp100 = f_tmp_117;
		Boolean f_tmp_118 = intLib.and(__tmp86,__tmp100);
		Boolean __tmp101 = f_tmp_118;
		int f_tmp_119 = 38;
		int __tmp102 = f_tmp_119;
		Boolean[] f_tmp_121 = env.inputOfAlice(Utils.fromInt(__tmp102, 32));
		Boolean f_tmp_120 = intLib.eq(funct, f_tmp_121);
		Boolean __tmp103 = f_tmp_120;
		Boolean f_tmp_122 = intLib.and(__tmp101,__tmp103);
		Boolean __tmp104 = f_tmp_122;
		Boolean[] f_tmp_123 = reg.read(rs);
		Boolean[] __tmp105 = f_tmp_123;
		Boolean[] f_tmp_124 = reg.read(rt);
		Boolean[] __tmp106 = f_tmp_124;
		Boolean[] f_tmp_125 = intLib.xor(__tmp105,__tmp106);
		Boolean[] __tmp107 = f_tmp_125;
		Boolean[] f_tmp_126 = reg.read(rd);
		Boolean[] __tmp108 = f_tmp_126;
		Boolean[] f_tmp_127 = intLib.mux(__tmp108, __tmp107,__tmp104);
		Boolean[] __tmp109 = f_tmp_127;
		reg.write(rd,__tmp109);
		Boolean f_tmp_128 = intLib.not(__tmp103);
		Boolean __tmp110 = f_tmp_128;
		Boolean f_tmp_129 = intLib.and(__tmp101,__tmp110);
		Boolean __tmp111 = f_tmp_129;
		int f_tmp_130 = 42;
		int __tmp112 = f_tmp_130;
		Boolean[] f_tmp_132 = env.inputOfAlice(Utils.fromInt(__tmp112, 32));
		Boolean f_tmp_131 = intLib.eq(funct, f_tmp_132);
		Boolean __tmp113 = f_tmp_131;
		Boolean f_tmp_133 = intLib.and(__tmp111,__tmp113);
		Boolean __tmp114 = f_tmp_133;
		Boolean[] f_tmp_134 = reg.read(rs);
		Boolean[] __tmp115 = f_tmp_134;
		Boolean[] f_tmp_135 = reg.read(rt);
		Boolean[] __tmp116 = f_tmp_135;
		Boolean f_tmp_136 = intLib.not(intLib.geq(__tmp115, __tmp116));
		Boolean __tmp117 = f_tmp_136;
		Boolean f_tmp_137 = intLib.and(__tmp114,__tmp117);
		Boolean __tmp118 = f_tmp_137;
		int f_tmp_138 = 1;
		int __tmp119 = f_tmp_138;
		Boolean[] f_tmp_139 = reg.read(rd);
		Boolean[] __tmp120 = f_tmp_139;
		Boolean[] f_tmp_141 = env.inputOfAlice(Utils.fromInt(__tmp119, 32));
		Boolean[] f_tmp_140 = intLib.mux(__tmp120, f_tmp_141,__tmp118);
		Boolean[] __tmp121 = f_tmp_140;
		reg.write(rd,__tmp121);
		Boolean f_tmp_142 = intLib.not(__tmp117);
		Boolean __tmp122 = f_tmp_142;
		Boolean f_tmp_143 = intLib.and(__tmp114,__tmp122);
		Boolean __tmp123 = f_tmp_143;
		int f_tmp_144 = 0;
		int __tmp124 = f_tmp_144;
		Boolean[] f_tmp_145 = reg.read(rd);
		Boolean[] __tmp125 = f_tmp_145;
		Boolean[] f_tmp_147 = env.inputOfAlice(Utils.fromInt(__tmp124, 32));
		Boolean[] f_tmp_146 = intLib.mux(__tmp125, f_tmp_147,__tmp123);
		Boolean[] __tmp126 = f_tmp_146;
		reg.write(rd,__tmp126);
		Boolean f_tmp_148 = intLib.not(__tmp113);
		Boolean __tmp127 = f_tmp_148;
		Boolean f_tmp_149 = intLib.and(__tmp111,__tmp127);
		Boolean __tmp128 = f_tmp_149;
		int f_tmp_150 = 35;
		int __tmp129 = f_tmp_150;
		Boolean[] f_tmp_152 = env.inputOfAlice(Utils.fromInt(__tmp129, 32));
		Boolean f_tmp_151 = intLib.eq(funct, f_tmp_152);
		Boolean __tmp130 = f_tmp_151;
		Boolean f_tmp_153 = intLib.and(__tmp128,__tmp130);
		Boolean __tmp131 = f_tmp_153;
		Boolean[] f_tmp_154 = reg.read(rs);
		Boolean[] __tmp132 = f_tmp_154;
		Boolean[] f_tmp_155 = reg.read(rt);
		Boolean[] __tmp133 = f_tmp_155;
		Boolean[] f_tmp_156 = intLib.sub(__tmp132,__tmp133);
		Boolean[] __tmp134 = f_tmp_156;
		Boolean[] f_tmp_157 = reg.read(rd);
		Boolean[] __tmp135 = f_tmp_157;
		Boolean[] f_tmp_158 = intLib.mux(__tmp135, __tmp134,__tmp131);
		Boolean[] __tmp136 = f_tmp_158;
		reg.write(rd,__tmp136);
		Boolean f_tmp_159 = intLib.not(__tmp130);
		Boolean __tmp137 = f_tmp_159;
		Boolean f_tmp_160 = intLib.and(__tmp128,__tmp137);
		Boolean __tmp138 = f_tmp_160;
		Boolean f_tmp_161 = intLib.not(__tmp85);
		Boolean __tmp139 = f_tmp_161;
		Boolean f_tmp_162 = intLib.and(__tmp83,__tmp139);
		Boolean __tmp140 = f_tmp_162;
		int f_tmp_163 = 8;
		int __tmp141 = f_tmp_163;
		Boolean[] f_tmp_165 = env.inputOfAlice(Utils.fromInt(__tmp141, 32));
		Boolean f_tmp_164 = intLib.eq(op, f_tmp_165);
		Boolean __tmp142 = f_tmp_164;
		Boolean[] f_tmp_166 = reg.read(rs);
		Boolean[] __tmp143 = f_tmp_166;
		Boolean[] f_tmp_167 = intLib.mux(pc, __tmp143,__tmp142);
		Boolean[] __tmp144 = f_tmp_167;
		pc = __tmp144;
		Boolean f_tmp_168 = intLib.not(__tmp142);
		Boolean __tmp145 = f_tmp_168;
		int f_tmp_169 = 3;
		int __tmp146 = f_tmp_169;
		Boolean[] f_tmp_171 = env.inputOfAlice(Utils.fromInt(__tmp146, 32));
		Boolean f_tmp_170 = intLib.eq(op, f_tmp_171);
		Boolean __tmp147 = f_tmp_170;
		Boolean f_tmp_172 = intLib.and(__tmp145,__tmp147);
		Boolean __tmp148 = f_tmp_172;
		int f_tmp_173 = 6;
		int __tmp149 = f_tmp_173;
		Boolean[] f_tmp_174 = intLib.leftPublicShift(inst, __tmp149);
		Boolean[] __tmp150 = f_tmp_174;
		int f_tmp_175 = 6;
		int __tmp151 = f_tmp_175;
		Boolean[] f_tmp_176 = intLib.rightPublicShift(__tmp150, __tmp151);
		Boolean[] __tmp152 = f_tmp_176;
		Boolean[] f_tmp_177 = intLib.mux(pc, __tmp152,__tmp148);
		Boolean[] __tmp153 = f_tmp_177;
		pc = __tmp153;
		Boolean f_tmp_178 = intLib.not(__tmp147);
		Boolean __tmp154 = f_tmp_178;
		Boolean f_tmp_179 = intLib.and(__tmp145,__tmp154);
		Boolean __tmp155 = f_tmp_179;
		int f_tmp_180 = 5;
		int __tmp156 = f_tmp_180;
		Boolean[] f_tmp_182 = env.inputOfAlice(Utils.fromInt(__tmp156, 32));
		Boolean f_tmp_181 = intLib.eq(op, f_tmp_182);
		Boolean __tmp157 = f_tmp_181;
		Boolean[] f_tmp_183 = reg.read(rs);
		Boolean[] __tmp158 = f_tmp_183;
		Boolean[] f_tmp_184 = reg.read(rt);
		Boolean[] __tmp159 = f_tmp_184;
		Boolean f_tmp_185 = intLib.not(intLib.eq(__tmp158, __tmp159));
		Boolean __tmp160 = f_tmp_185;
		Boolean f_tmp_186 = intLib.and(__tmp157,__tmp160);
		Boolean __tmp161 = f_tmp_186;
		int f_tmp_187 = 4;
		int __tmp162 = f_tmp_187;
		Boolean[] f_tmp_189 = env.inputOfAlice(Utils.fromInt(__tmp162, 32));
		Boolean f_tmp_188 = intLib.eq(op, f_tmp_189);
		Boolean __tmp163 = f_tmp_188;
		Boolean[] f_tmp_190 = reg.read(rs);
		Boolean[] __tmp164 = f_tmp_190;
		Boolean[] f_tmp_191 = reg.read(rt);
		Boolean[] __tmp165 = f_tmp_191;
		Boolean f_tmp_192 = intLib.eq(__tmp164, __tmp165);
		Boolean __tmp166 = f_tmp_192;
		Boolean f_tmp_193 = intLib.and(__tmp163,__tmp166);
		Boolean __tmp167 = f_tmp_193;
		Boolean f_tmp_194 = intLib.or(__tmp161,__tmp167);
		Boolean __tmp168 = f_tmp_194;
		Boolean f_tmp_195 = intLib.and(__tmp155,__tmp168);
		Boolean __tmp169 = f_tmp_195;
		int f_tmp_196 = 2;
		int __tmp170 = f_tmp_196;
		Boolean[] f_tmp_197 = intLib.leftPublicShift(unsignExt, __tmp170);
		Boolean[] __tmp171 = f_tmp_197;
		Boolean[] f_tmp_198 = intLib.add(pc,__tmp171);
		Boolean[] __tmp172 = f_tmp_198;
		Boolean[] f_tmp_199 = intLib.mux(pc, __tmp172,__tmp169);
		Boolean[] __tmp173 = f_tmp_199;
		pc = __tmp173;
		Boolean f_tmp_200 = intLib.not(__tmp168);
		Boolean __tmp174 = f_tmp_200;
		Boolean f_tmp_201 = intLib.and(__tmp155,__tmp174);
		Boolean __tmp175 = f_tmp_201;
		int f_tmp_202 = 4;
		int __tmp176 = f_tmp_202;
		Boolean[] f_tmp_204 = env.inputOfAlice(Utils.fromInt(__tmp176, 32));
		Boolean[] f_tmp_203 = intLib.add(pc,f_tmp_204);
		Boolean[] __tmp177 = f_tmp_203;
		Boolean[] f_tmp_205 = intLib.mux(pc, __tmp177,__tmp175);
		Boolean[] __tmp178 = f_tmp_205;
		pc = __tmp178;
		return pc;
	}
}
