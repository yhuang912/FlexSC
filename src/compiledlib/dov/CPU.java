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
public class CPU implements IWritable<CPU, GCSignal> {

	public CompEnv<GCSignal> env;
	public IntegerLib<GCSignal> intLib;
	public FloatLib<GCSignal> floatLib;

	public CPU(CompEnv<GCSignal> env) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<GCSignal>(env);
		this.floatLib = new FloatLib<GCSignal>(env, 24, 8);
	}

	public int numBits() {
		return 0;
	}
	public GCSignal[] getBits() {
		GCSignal[] ret = new GCSignal[this.numBits()];
		GCSignal[] tmp_b;
		GCSignal tmp;
		int now = 0;
		return ret;
}

	public CPU newObj(GCSignal[] data) throws Exception {
		if(data == null) {
			data = new GCSignal[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		CPU ret = new CPU(env);
		GCSignal[] tmp;
		int now = 0;
		return ret;
}

	public GCSignal checkTerminate(GCSignal[] inst) throws Exception {
		GCSignal res = env.inputOfAlice(false);
		boolean f_tmp_0 = false;
		boolean __tmp0 = f_tmp_0;
		res = env.inputOfAlice(__tmp0);
		int f_tmp_1 = 268437280;
		int __tmp1 = f_tmp_1;
		GCSignal[] f_tmp_3 = env.inputOfAlice(Utils.fromInt(__tmp1, 32));
		GCSignal f_tmp_2 = intLib.eq(inst, f_tmp_3);
		GCSignal __tmp2 = f_tmp_2;
		boolean f_tmp_4 = true;
		boolean __tmp3 = f_tmp_4;
		GCSignal f_tmp_6 = env.inputOfAlice(__tmp3);
		GCSignal f_tmp_5 = intLib.mux(res, f_tmp_6,__tmp2);
		GCSignal __tmp4 = f_tmp_5;
		res = __tmp4;
		GCSignal f_tmp_7 = intLib.not(__tmp2);
		GCSignal __tmp5 = f_tmp_7;
		return res;
	}
	public GCSignal[] function(SecureArray<GCSignal> reg, GCSignal[] inst, GCSignal[] pc) throws Exception {
		GCSignal[] op = env.inputOfAlice(Utils.fromInt(0, 32));
		GCSignal[] rt = env.inputOfAlice(Utils.fromInt(0, 32));
		GCSignal[] rs = env.inputOfAlice(Utils.fromInt(0, 32));
		GCSignal[] rd = env.inputOfAlice(Utils.fromInt(0, 32));
		GCSignal[] unsignExt = env.inputOfAlice(Utils.fromInt(0, 32));
		GCSignal[] zeroExt = env.inputOfAlice(Utils.fromInt(0, 32));
		GCSignal[] funct = env.inputOfAlice(Utils.fromInt(0, 32));
		int f_tmp_8 = 26;
		int __tmp6 = f_tmp_8;
		GCSignal[] f_tmp_9 = intLib.rightPublicShift(inst, __tmp6);
		GCSignal[] __tmp7 = f_tmp_9;
		op = __tmp7;
		int f_tmp_10 = 11;
		int __tmp8 = f_tmp_10;
		GCSignal[] f_tmp_11 = intLib.leftPublicShift(inst, __tmp8);
		GCSignal[] __tmp9 = f_tmp_11;
		int f_tmp_12 = 27;
		int __tmp10 = f_tmp_12;
		GCSignal[] f_tmp_13 = intLib.rightPublicShift(__tmp9, __tmp10);
		GCSignal[] __tmp11 = f_tmp_13;
		rt = __tmp11;
		int f_tmp_14 = 6;
		int __tmp12 = f_tmp_14;
		GCSignal[] f_tmp_15 = intLib.leftPublicShift(inst, __tmp12);
		GCSignal[] __tmp13 = f_tmp_15;
		int f_tmp_16 = 27;
		int __tmp14 = f_tmp_16;
		GCSignal[] f_tmp_17 = intLib.rightPublicShift(__tmp13, __tmp14);
		GCSignal[] __tmp15 = f_tmp_17;
		rs = __tmp15;
		int f_tmp_18 = 16;
		int __tmp16 = f_tmp_18;
		GCSignal[] f_tmp_19 = intLib.leftPublicShift(inst, __tmp16);
		GCSignal[] __tmp17 = f_tmp_19;
		int f_tmp_20 = 27;
		int __tmp18 = f_tmp_20;
		GCSignal[] f_tmp_21 = intLib.rightPublicShift(__tmp17, __tmp18);
		GCSignal[] __tmp19 = f_tmp_21;
		rd = __tmp19;
		int f_tmp_22 = 16;
		int __tmp20 = f_tmp_22;
		GCSignal[] f_tmp_23 = intLib.leftPublicShift(inst, __tmp20);
		GCSignal[] __tmp21 = f_tmp_23;
		int f_tmp_24 = 16;
		int __tmp22 = f_tmp_24;
		GCSignal[] f_tmp_25 = intLib.rightPublicShift(__tmp21, __tmp22);
		GCSignal[] __tmp23 = f_tmp_25;
		unsignExt = __tmp23;
		zeroExt = unsignExt;
		int f_tmp_26 = 0;
		int __tmp24 = f_tmp_26;
		funct = env.inputOfAlice(Utils.fromInt(__tmp24, 32));
		int f_tmp_27 = 15;
		int __tmp25 = f_tmp_27;
		GCSignal[] f_tmp_28 = intLib.rightPublicShift(unsignExt, __tmp25);
		GCSignal[] __tmp26 = f_tmp_28;
		int f_tmp_29 = 0;
		int __tmp27 = f_tmp_29;
		GCSignal[] f_tmp_31 = env.inputOfAlice(Utils.fromInt(__tmp27, 32));
		GCSignal f_tmp_30 = intLib.not(intLib.eq(__tmp26, f_tmp_31));
		GCSignal __tmp28 = f_tmp_30;
		int f_tmp_32 = -65536;
		int __tmp29 = f_tmp_32;
		GCSignal[] f_tmp_34 = env.inputOfAlice(Utils.fromInt(__tmp29, 32));
		GCSignal[] f_tmp_33 = intLib.add(unsignExt,f_tmp_34);
		GCSignal[] __tmp30 = f_tmp_33;
		GCSignal[] f_tmp_35 = intLib.mux(unsignExt, __tmp30,__tmp28);
		GCSignal[] __tmp31 = f_tmp_35;
		unsignExt = __tmp31;
		GCSignal f_tmp_36 = intLib.not(__tmp28);
		GCSignal __tmp32 = f_tmp_36;
		int f_tmp_37 = 9;
		int __tmp33 = f_tmp_37;
		GCSignal[] f_tmp_39 = env.inputOfAlice(Utils.fromInt(__tmp33, 32));
		GCSignal f_tmp_38 = intLib.eq(op, f_tmp_39);
		GCSignal __tmp34 = f_tmp_38;
		GCSignal[] f_tmp_40 = reg.read(rs);
		GCSignal[] __tmp35 = f_tmp_40;
		GCSignal[] f_tmp_41 = intLib.add(__tmp35,unsignExt);
		GCSignal[] __tmp36 = f_tmp_41;
		GCSignal[] f_tmp_42 = reg.read(rt);
		GCSignal[] __tmp37 = f_tmp_42;
		GCSignal[] f_tmp_43 = intLib.mux(__tmp37, __tmp36,__tmp34);
		GCSignal[] __tmp38 = f_tmp_43;
		reg.write(rt,__tmp38);
		GCSignal f_tmp_44 = intLib.not(__tmp34);
		GCSignal __tmp39 = f_tmp_44;
		int f_tmp_45 = 3;
		int __tmp40 = f_tmp_45;
		GCSignal[] f_tmp_47 = env.inputOfAlice(Utils.fromInt(__tmp40, 32));
		GCSignal f_tmp_46 = intLib.eq(op, f_tmp_47);
		GCSignal __tmp41 = f_tmp_46;
		GCSignal f_tmp_48 = intLib.and(__tmp39,__tmp41);
		GCSignal __tmp42 = f_tmp_48;
		int f_tmp_49 = 8;
		int __tmp43 = f_tmp_49;
		GCSignal[] f_tmp_51 = env.inputOfAlice(Utils.fromInt(__tmp43, 32));
		GCSignal[] f_tmp_50 = intLib.add(pc,f_tmp_51);
		GCSignal[] __tmp44 = f_tmp_50;
		int f_tmp_52 = 31;
		int __tmp45 = f_tmp_52;
		GCSignal[] f_tmp_54 = env.inputOfAlice(Utils.fromInt(__tmp45, 32));
		GCSignal[] f_tmp_53 = reg.read(f_tmp_54);
		GCSignal[] __tmp46 = f_tmp_53;
		GCSignal[] f_tmp_55 = intLib.mux(__tmp46, __tmp44,__tmp42);
		GCSignal[] __tmp47 = f_tmp_55;
		GCSignal[] f_tmp_56 = env.inputOfAlice(Utils.fromInt(__tmp45, 32));
		reg.write(f_tmp_56,__tmp47);
		int f_tmp_57 = 6;
		int __tmp48 = f_tmp_57;
		GCSignal[] f_tmp_58 = intLib.leftPublicShift(inst, __tmp48);
		GCSignal[] __tmp49 = f_tmp_58;
		int f_tmp_59 = 6;
		int __tmp50 = f_tmp_59;
		GCSignal[] f_tmp_60 = intLib.rightPublicShift(__tmp49, __tmp50);
		GCSignal[] __tmp51 = f_tmp_60;
		GCSignal[] f_tmp_61 = intLib.mux(pc, __tmp51,__tmp42);
		GCSignal[] __tmp52 = f_tmp_61;
		pc = __tmp52;
		GCSignal f_tmp_62 = intLib.not(__tmp41);
		GCSignal __tmp53 = f_tmp_62;
		GCSignal f_tmp_63 = intLib.and(__tmp39,__tmp53);
		GCSignal __tmp54 = f_tmp_63;
		int f_tmp_64 = 1;
		int __tmp55 = f_tmp_64;
		GCSignal[] f_tmp_66 = env.inputOfAlice(Utils.fromInt(__tmp55, 32));
		GCSignal f_tmp_65 = intLib.eq(op, f_tmp_66);
		GCSignal __tmp56 = f_tmp_65;
		GCSignal f_tmp_67 = intLib.and(__tmp54,__tmp56);
		GCSignal __tmp57 = f_tmp_67;
		int f_tmp_68 = 8;
		int __tmp58 = f_tmp_68;
		GCSignal[] f_tmp_70 = env.inputOfAlice(Utils.fromInt(__tmp58, 32));
		GCSignal[] f_tmp_69 = intLib.add(pc,f_tmp_70);
		GCSignal[] __tmp59 = f_tmp_69;
		int f_tmp_71 = 31;
		int __tmp60 = f_tmp_71;
		GCSignal[] f_tmp_73 = env.inputOfAlice(Utils.fromInt(__tmp60, 32));
		GCSignal[] f_tmp_72 = reg.read(f_tmp_73);
		GCSignal[] __tmp61 = f_tmp_72;
		GCSignal[] f_tmp_74 = intLib.mux(__tmp61, __tmp59,__tmp57);
		GCSignal[] __tmp62 = f_tmp_74;
		GCSignal[] f_tmp_75 = env.inputOfAlice(Utils.fromInt(__tmp60, 32));
		reg.write(f_tmp_75,__tmp62);
		GCSignal[] f_tmp_76 = reg.read(rt);
		GCSignal[] __tmp63 = f_tmp_76;
		int f_tmp_77 = 0;
		int __tmp64 = f_tmp_77;
		GCSignal[] f_tmp_79 = env.inputOfAlice(Utils.fromInt(__tmp64, 32));
		GCSignal f_tmp_78 = intLib.geq(__tmp63, f_tmp_79);
		GCSignal __tmp65 = f_tmp_78;
		GCSignal f_tmp_80 = intLib.and(__tmp57,__tmp65);
		GCSignal __tmp66 = f_tmp_80;
		int f_tmp_81 = 2;
		int __tmp67 = f_tmp_81;
		GCSignal[] f_tmp_82 = intLib.leftPublicShift(unsignExt, __tmp67);
		GCSignal[] __tmp68 = f_tmp_82;
		GCSignal[] f_tmp_83 = intLib.add(pc,__tmp68);
		GCSignal[] __tmp69 = f_tmp_83;
		GCSignal[] f_tmp_84 = intLib.mux(pc, __tmp69,__tmp66);
		GCSignal[] __tmp70 = f_tmp_84;
		pc = __tmp70;
		GCSignal f_tmp_85 = intLib.not(__tmp65);
		GCSignal __tmp71 = f_tmp_85;
		GCSignal f_tmp_86 = intLib.and(__tmp57,__tmp71);
		GCSignal __tmp72 = f_tmp_86;
		GCSignal f_tmp_87 = intLib.not(__tmp56);
		GCSignal __tmp73 = f_tmp_87;
		GCSignal f_tmp_88 = intLib.and(__tmp54,__tmp73);
		GCSignal __tmp74 = f_tmp_88;
		int f_tmp_89 = 12;
		int __tmp75 = f_tmp_89;
		GCSignal[] f_tmp_91 = env.inputOfAlice(Utils.fromInt(__tmp75, 32));
		GCSignal f_tmp_90 = intLib.eq(op, f_tmp_91);
		GCSignal __tmp76 = f_tmp_90;
		GCSignal f_tmp_92 = intLib.and(__tmp74,__tmp76);
		GCSignal __tmp77 = f_tmp_92;
		GCSignal[] f_tmp_93 = reg.read(rs);
		GCSignal[] __tmp78 = f_tmp_93;
		GCSignal[] f_tmp_94 = intLib.and(__tmp78,zeroExt);
		GCSignal[] __tmp79 = f_tmp_94;
		GCSignal[] f_tmp_95 = reg.read(rt);
		GCSignal[] __tmp80 = f_tmp_95;
		GCSignal[] f_tmp_96 = intLib.mux(__tmp80, __tmp79,__tmp77);
		GCSignal[] __tmp81 = f_tmp_96;
		reg.write(rt,__tmp81);
		GCSignal f_tmp_97 = intLib.not(__tmp76);
		GCSignal __tmp82 = f_tmp_97;
		GCSignal f_tmp_98 = intLib.and(__tmp74,__tmp82);
		GCSignal __tmp83 = f_tmp_98;
		int f_tmp_99 = 0;
		int __tmp84 = f_tmp_99;
		GCSignal[] f_tmp_101 = env.inputOfAlice(Utils.fromInt(__tmp84, 32));
		GCSignal f_tmp_100 = intLib.eq(op, f_tmp_101);
		GCSignal __tmp85 = f_tmp_100;
		GCSignal f_tmp_102 = intLib.and(__tmp83,__tmp85);
		GCSignal __tmp86 = f_tmp_102;
		int f_tmp_103 = 26;
		int __tmp87 = f_tmp_103;
		GCSignal[] f_tmp_104 = intLib.leftPublicShift(inst, __tmp87);
		GCSignal[] __tmp88 = f_tmp_104;
		int f_tmp_105 = 26;
		int __tmp89 = f_tmp_105;
		GCSignal[] f_tmp_106 = intLib.rightPublicShift(__tmp88, __tmp89);
		GCSignal[] __tmp90 = f_tmp_106;
		GCSignal[] f_tmp_107 = intLib.mux(funct, __tmp90,__tmp86);
		GCSignal[] __tmp91 = f_tmp_107;
		funct = __tmp91;
		int f_tmp_108 = 33;
		int __tmp92 = f_tmp_108;
		GCSignal[] f_tmp_110 = env.inputOfAlice(Utils.fromInt(__tmp92, 32));
		GCSignal f_tmp_109 = intLib.eq(funct, f_tmp_110);
		GCSignal __tmp93 = f_tmp_109;
		GCSignal f_tmp_111 = intLib.and(__tmp86,__tmp93);
		GCSignal __tmp94 = f_tmp_111;
		GCSignal[] f_tmp_112 = reg.read(rs);
		GCSignal[] __tmp95 = f_tmp_112;
		GCSignal[] f_tmp_113 = reg.read(rt);
		GCSignal[] __tmp96 = f_tmp_113;
		GCSignal[] f_tmp_114 = intLib.add(__tmp95,__tmp96);
		GCSignal[] __tmp97 = f_tmp_114;
		GCSignal[] f_tmp_115 = reg.read(rd);
		GCSignal[] __tmp98 = f_tmp_115;
		GCSignal[] f_tmp_116 = intLib.mux(__tmp98, __tmp97,__tmp94);
		GCSignal[] __tmp99 = f_tmp_116;
		reg.write(rd,__tmp99);
		GCSignal f_tmp_117 = intLib.not(__tmp93);
		GCSignal __tmp100 = f_tmp_117;
		GCSignal f_tmp_118 = intLib.and(__tmp86,__tmp100);
		GCSignal __tmp101 = f_tmp_118;
		int f_tmp_119 = 38;
		int __tmp102 = f_tmp_119;
		GCSignal[] f_tmp_121 = env.inputOfAlice(Utils.fromInt(__tmp102, 32));
		GCSignal f_tmp_120 = intLib.eq(funct, f_tmp_121);
		GCSignal __tmp103 = f_tmp_120;
		GCSignal f_tmp_122 = intLib.and(__tmp101,__tmp103);
		GCSignal __tmp104 = f_tmp_122;
		GCSignal[] f_tmp_123 = reg.read(rs);
		GCSignal[] __tmp105 = f_tmp_123;
		GCSignal[] f_tmp_124 = reg.read(rt);
		GCSignal[] __tmp106 = f_tmp_124;
		GCSignal[] f_tmp_125 = intLib.xor(__tmp105,__tmp106);
		GCSignal[] __tmp107 = f_tmp_125;
		GCSignal[] f_tmp_126 = reg.read(rd);
		GCSignal[] __tmp108 = f_tmp_126;
		GCSignal[] f_tmp_127 = intLib.mux(__tmp108, __tmp107,__tmp104);
		GCSignal[] __tmp109 = f_tmp_127;
		reg.write(rd,__tmp109);
		GCSignal f_tmp_128 = intLib.not(__tmp103);
		GCSignal __tmp110 = f_tmp_128;
		GCSignal f_tmp_129 = intLib.and(__tmp101,__tmp110);
		GCSignal __tmp111 = f_tmp_129;
		int f_tmp_130 = 42;
		int __tmp112 = f_tmp_130;
		GCSignal[] f_tmp_132 = env.inputOfAlice(Utils.fromInt(__tmp112, 32));
		GCSignal f_tmp_131 = intLib.eq(funct, f_tmp_132);
		GCSignal __tmp113 = f_tmp_131;
		GCSignal f_tmp_133 = intLib.and(__tmp111,__tmp113);
		GCSignal __tmp114 = f_tmp_133;
		GCSignal[] f_tmp_134 = reg.read(rs);
		GCSignal[] __tmp115 = f_tmp_134;
		GCSignal[] f_tmp_135 = reg.read(rt);
		GCSignal[] __tmp116 = f_tmp_135;
		GCSignal f_tmp_136 = intLib.not(intLib.geq(__tmp115, __tmp116));
		GCSignal __tmp117 = f_tmp_136;
		GCSignal f_tmp_137 = intLib.and(__tmp114,__tmp117);
		GCSignal __tmp118 = f_tmp_137;
		int f_tmp_138 = 1;
		int __tmp119 = f_tmp_138;
		GCSignal[] f_tmp_139 = reg.read(rd);
		GCSignal[] __tmp120 = f_tmp_139;
		GCSignal[] f_tmp_141 = env.inputOfAlice(Utils.fromInt(__tmp119, 32));
		GCSignal[] f_tmp_140 = intLib.mux(__tmp120, f_tmp_141,__tmp118);
		GCSignal[] __tmp121 = f_tmp_140;
		reg.write(rd,__tmp121);
		GCSignal f_tmp_142 = intLib.not(__tmp117);
		GCSignal __tmp122 = f_tmp_142;
		GCSignal f_tmp_143 = intLib.and(__tmp114,__tmp122);
		GCSignal __tmp123 = f_tmp_143;
		int f_tmp_144 = 0;
		int __tmp124 = f_tmp_144;
		GCSignal[] f_tmp_145 = reg.read(rd);
		GCSignal[] __tmp125 = f_tmp_145;
		GCSignal[] f_tmp_147 = env.inputOfAlice(Utils.fromInt(__tmp124, 32));
		GCSignal[] f_tmp_146 = intLib.mux(__tmp125, f_tmp_147,__tmp123);
		GCSignal[] __tmp126 = f_tmp_146;
		reg.write(rd,__tmp126);
		GCSignal f_tmp_148 = intLib.not(__tmp113);
		GCSignal __tmp127 = f_tmp_148;
		GCSignal f_tmp_149 = intLib.and(__tmp111,__tmp127);
		GCSignal __tmp128 = f_tmp_149;
		int f_tmp_150 = 35;
		int __tmp129 = f_tmp_150;
		GCSignal[] f_tmp_152 = env.inputOfAlice(Utils.fromInt(__tmp129, 32));
		GCSignal f_tmp_151 = intLib.eq(funct, f_tmp_152);
		GCSignal __tmp130 = f_tmp_151;
		GCSignal f_tmp_153 = intLib.and(__tmp128,__tmp130);
		GCSignal __tmp131 = f_tmp_153;
		GCSignal[] f_tmp_154 = reg.read(rs);
		GCSignal[] __tmp132 = f_tmp_154;
		GCSignal[] f_tmp_155 = reg.read(rt);
		GCSignal[] __tmp133 = f_tmp_155;
		GCSignal[] f_tmp_156 = intLib.sub(__tmp132,__tmp133);
		GCSignal[] __tmp134 = f_tmp_156;
		GCSignal[] f_tmp_157 = reg.read(rd);
		GCSignal[] __tmp135 = f_tmp_157;
		GCSignal[] f_tmp_158 = intLib.mux(__tmp135, __tmp134,__tmp131);
		GCSignal[] __tmp136 = f_tmp_158;
		reg.write(rd,__tmp136);
		GCSignal f_tmp_159 = intLib.not(__tmp130);
		GCSignal __tmp137 = f_tmp_159;
		GCSignal f_tmp_160 = intLib.and(__tmp128,__tmp137);
		GCSignal __tmp138 = f_tmp_160;
		GCSignal f_tmp_161 = intLib.not(__tmp85);
		GCSignal __tmp139 = f_tmp_161;
		GCSignal f_tmp_162 = intLib.and(__tmp83,__tmp139);
		GCSignal __tmp140 = f_tmp_162;
		int f_tmp_163 = 0;
		int __tmp141 = f_tmp_163;
		GCSignal[] f_tmp_165 = env.inputOfAlice(Utils.fromInt(__tmp141, 32));
		GCSignal f_tmp_164 = intLib.eq(op, f_tmp_165);
		GCSignal __tmp142 = f_tmp_164;
		int f_tmp_166 = 8;
		int __tmp143 = f_tmp_166;
		GCSignal[] f_tmp_168 = env.inputOfAlice(Utils.fromInt(__tmp143, 32));
		GCSignal f_tmp_167 = intLib.eq(funct, f_tmp_168);
		GCSignal __tmp144 = f_tmp_167;
		GCSignal f_tmp_169 = intLib.and(__tmp142,__tmp144);
		GCSignal __tmp145 = f_tmp_169;
		GCSignal[] f_tmp_170 = reg.read(rs);
		GCSignal[] __tmp146 = f_tmp_170;
		GCSignal[] f_tmp_171 = intLib.mux(pc, __tmp146,__tmp145);
		GCSignal[] __tmp147 = f_tmp_171;
		pc = __tmp147;
		GCSignal f_tmp_172 = intLib.not(__tmp145);
		GCSignal __tmp148 = f_tmp_172;
		int f_tmp_173 = 3;
		int __tmp149 = f_tmp_173;
		GCSignal[] f_tmp_175 = env.inputOfAlice(Utils.fromInt(__tmp149, 32));
		GCSignal f_tmp_174 = intLib.eq(op, f_tmp_175);
		GCSignal __tmp150 = f_tmp_174;
		GCSignal f_tmp_176 = intLib.and(__tmp148,__tmp150);
		GCSignal __tmp151 = f_tmp_176;
		int f_tmp_177 = 6;
		int __tmp152 = f_tmp_177;
		GCSignal[] f_tmp_178 = intLib.leftPublicShift(inst, __tmp152);
		GCSignal[] __tmp153 = f_tmp_178;
		int f_tmp_179 = 6;
		int __tmp154 = f_tmp_179;
		GCSignal[] f_tmp_180 = intLib.rightPublicShift(__tmp153, __tmp154);
		GCSignal[] __tmp155 = f_tmp_180;
		GCSignal[] f_tmp_181 = intLib.mux(pc, __tmp155,__tmp151);
		GCSignal[] __tmp156 = f_tmp_181;
		pc = __tmp156;
		GCSignal f_tmp_182 = intLib.not(__tmp150);
		GCSignal __tmp157 = f_tmp_182;
		GCSignal f_tmp_183 = intLib.and(__tmp148,__tmp157);
		GCSignal __tmp158 = f_tmp_183;
		int f_tmp_184 = 5;
		int __tmp159 = f_tmp_184;
		GCSignal[] f_tmp_186 = env.inputOfAlice(Utils.fromInt(__tmp159, 32));
		GCSignal f_tmp_185 = intLib.eq(op, f_tmp_186);
		GCSignal __tmp160 = f_tmp_185;
		GCSignal[] f_tmp_187 = reg.read(rs);
		GCSignal[] __tmp161 = f_tmp_187;
		GCSignal[] f_tmp_188 = reg.read(rt);
		GCSignal[] __tmp162 = f_tmp_188;
		GCSignal f_tmp_189 = intLib.not(intLib.eq(__tmp161, __tmp162));
		GCSignal __tmp163 = f_tmp_189;
		GCSignal f_tmp_190 = intLib.and(__tmp160,__tmp163);
		GCSignal __tmp164 = f_tmp_190;
		int f_tmp_191 = 4;
		int __tmp165 = f_tmp_191;
		GCSignal[] f_tmp_193 = env.inputOfAlice(Utils.fromInt(__tmp165, 32));
		GCSignal f_tmp_192 = intLib.eq(op, f_tmp_193);
		GCSignal __tmp166 = f_tmp_192;
		GCSignal[] f_tmp_194 = reg.read(rs);
		GCSignal[] __tmp167 = f_tmp_194;
		GCSignal[] f_tmp_195 = reg.read(rt);
		GCSignal[] __tmp168 = f_tmp_195;
		GCSignal f_tmp_196 = intLib.eq(__tmp167, __tmp168);
		GCSignal __tmp169 = f_tmp_196;
		GCSignal f_tmp_197 = intLib.and(__tmp166,__tmp169);
		GCSignal __tmp170 = f_tmp_197;
		GCSignal f_tmp_198 = intLib.or(__tmp164,__tmp170);
		GCSignal __tmp171 = f_tmp_198;
		GCSignal f_tmp_199 = intLib.and(__tmp158,__tmp171);
		GCSignal __tmp172 = f_tmp_199;
		int f_tmp_200 = 2;
		int __tmp173 = f_tmp_200;
		GCSignal[] f_tmp_201 = intLib.leftPublicShift(unsignExt, __tmp173);
		GCSignal[] __tmp174 = f_tmp_201;
		GCSignal[] f_tmp_202 = intLib.add(pc,__tmp174);
		GCSignal[] __tmp175 = f_tmp_202;
		GCSignal[] f_tmp_203 = intLib.mux(pc, __tmp175,__tmp172);
		GCSignal[] __tmp176 = f_tmp_203;
		pc = __tmp176;
		GCSignal f_tmp_204 = intLib.not(__tmp171);
		GCSignal __tmp177 = f_tmp_204;
		GCSignal f_tmp_205 = intLib.and(__tmp158,__tmp177);
		GCSignal __tmp178 = f_tmp_205;
		int f_tmp_206 = 4;
		int __tmp179 = f_tmp_206;
		GCSignal[] f_tmp_208 = env.inputOfAlice(Utils.fromInt(__tmp179, 32));
		GCSignal[] f_tmp_207 = intLib.add(pc,f_tmp_208);
		GCSignal[] __tmp180 = f_tmp_207;
		GCSignal[] f_tmp_209 = intLib.mux(pc, __tmp180,__tmp178);
		GCSignal[] __tmp181 = f_tmp_209;
		pc = __tmp181;
		return pc;
	}
}
