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
public class MEM implements IWritable<MEM, GCSignal> {

	public CompEnv<GCSignal> env;
	public IntegerLib<GCSignal> intLib;
	public FloatLib<GCSignal> floatLib;

	public MEM(CompEnv<GCSignal> env) throws Exception {
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

	public MEM newObj(GCSignal[] data) throws Exception {
		if(data == null) {
			data = new GCSignal[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		MEM ret = new MEM(env);
		GCSignal[] tmp;
		int now = 0;
		return ret;
}

	public GCSignal[] func(SecureArray<GCSignal> reg, SecureArray<GCSignal> mem, GCSignal[] pc, GCSignal[] inst, int pcOffset, int dataOffset) throws Exception {
		GCSignal[] index = env.inputOfAlice(Utils.fromInt(0, 32));
		GCSignal[] newInst = env.inputOfAlice(Utils.fromInt(0, 32));
		GCSignal[] rt = env.inputOfAlice(Utils.fromInt(0, 32));
		GCSignal[] rs = env.inputOfAlice(Utils.fromInt(0, 32));
		GCSignal[] unsignExt = env.inputOfAlice(Utils.fromInt(0, 32));
		GCSignal[] op = env.inputOfAlice(Utils.fromInt(0, 32));
		GCSignal[] tmpindex = env.inputOfAlice(Utils.fromInt(0, 32));
		GCSignal[] f_tmp_1 = env.inputOfAlice(Utils.fromInt(pcOffset, 32));
		GCSignal[] f_tmp_0 = intLib.sub(pc,f_tmp_1);
		GCSignal[] __tmp0 = f_tmp_0;
		int f_tmp_2 = 2;
		int __tmp1 = f_tmp_2;
		GCSignal[] f_tmp_3 = intLib.rightPublicShift(__tmp0, __tmp1);
		GCSignal[] __tmp2 = f_tmp_3;
		index = __tmp2;
		GCSignal[] f_tmp_4 = mem.read(index);
		GCSignal[] __tmp3 = f_tmp_4;
		newInst = __tmp3;
		int f_tmp_5 = 11;
		int __tmp4 = f_tmp_5;
		GCSignal[] f_tmp_6 = intLib.leftPublicShift(inst, __tmp4);
		GCSignal[] __tmp5 = f_tmp_6;
		int f_tmp_7 = 27;
		int __tmp6 = f_tmp_7;
		GCSignal[] f_tmp_8 = intLib.rightPublicShift(__tmp5, __tmp6);
		GCSignal[] __tmp7 = f_tmp_8;
		rt = __tmp7;
		int f_tmp_9 = 6;
		int __tmp8 = f_tmp_9;
		GCSignal[] f_tmp_10 = intLib.leftPublicShift(inst, __tmp8);
		GCSignal[] __tmp9 = f_tmp_10;
		int f_tmp_11 = 27;
		int __tmp10 = f_tmp_11;
		GCSignal[] f_tmp_12 = intLib.rightPublicShift(__tmp9, __tmp10);
		GCSignal[] __tmp11 = f_tmp_12;
		rs = __tmp11;
		int f_tmp_13 = 16;
		int __tmp12 = f_tmp_13;
		GCSignal[] f_tmp_14 = intLib.leftPublicShift(inst, __tmp12);
		GCSignal[] __tmp13 = f_tmp_14;
		int f_tmp_15 = 16;
		int __tmp14 = f_tmp_15;
		GCSignal[] f_tmp_16 = intLib.rightPublicShift(__tmp13, __tmp14);
		GCSignal[] __tmp15 = f_tmp_16;
		unsignExt = __tmp15;
		int f_tmp_17 = 15;
		int __tmp16 = f_tmp_17;
		GCSignal[] f_tmp_18 = intLib.rightPublicShift(unsignExt, __tmp16);
		GCSignal[] __tmp17 = f_tmp_18;
		int f_tmp_19 = 1;
		int __tmp18 = f_tmp_19;
		GCSignal[] f_tmp_21 = env.inputOfAlice(Utils.fromInt(__tmp18, 32));
		GCSignal f_tmp_20 = intLib.eq(__tmp17, f_tmp_21);
		GCSignal __tmp19 = f_tmp_20;
		int f_tmp_22 = -65536;
		int __tmp20 = f_tmp_22;
		GCSignal[] f_tmp_24 = env.inputOfAlice(Utils.fromInt(__tmp20, 32));
		GCSignal[] f_tmp_23 = intLib.add(unsignExt,f_tmp_24);
		GCSignal[] __tmp21 = f_tmp_23;
		GCSignal[] f_tmp_25 = intLib.mux(unsignExt, __tmp21,__tmp19);
		GCSignal[] __tmp22 = f_tmp_25;
		unsignExt = __tmp22;
		GCSignal f_tmp_26 = intLib.not(__tmp19);
		GCSignal __tmp23 = f_tmp_26;
		int f_tmp_27 = 26;
		int __tmp24 = f_tmp_27;
		GCSignal[] f_tmp_28 = intLib.rightPublicShift(inst, __tmp24);
		GCSignal[] __tmp25 = f_tmp_28;
		op = __tmp25;
		int f_tmp_29 = 35;
		int __tmp26 = f_tmp_29;
		GCSignal[] f_tmp_31 = env.inputOfAlice(Utils.fromInt(__tmp26, 32));
		GCSignal f_tmp_30 = intLib.eq(op, f_tmp_31);
		GCSignal __tmp27 = f_tmp_30;
		GCSignal[] f_tmp_32 = reg.read(rs);
		GCSignal[] __tmp28 = f_tmp_32;
		GCSignal[] f_tmp_33 = intLib.add(__tmp28,unsignExt);
		GCSignal[] __tmp29 = f_tmp_33;
		GCSignal[] f_tmp_35 = env.inputOfAlice(Utils.fromInt(dataOffset, 32));
		GCSignal[] f_tmp_34 = intLib.sub(__tmp29,f_tmp_35);
		GCSignal[] __tmp30 = f_tmp_34;
		int f_tmp_36 = 2;
		int __tmp31 = f_tmp_36;
		GCSignal[] f_tmp_37 = intLib.rightPublicShift(__tmp30, __tmp31);
		GCSignal[] __tmp32 = f_tmp_37;
		GCSignal[] f_tmp_38 = mem.read(__tmp32);
		GCSignal[] __tmp33 = f_tmp_38;
		GCSignal[] f_tmp_39 = reg.read(rt);
		GCSignal[] __tmp34 = f_tmp_39;
		GCSignal[] f_tmp_40 = intLib.mux(__tmp34, __tmp33,__tmp27);
		GCSignal[] __tmp35 = f_tmp_40;
		reg.write(rt,__tmp35);
		GCSignal f_tmp_41 = intLib.not(__tmp27);
		GCSignal __tmp36 = f_tmp_41;
		int f_tmp_42 = 43;
		int __tmp37 = f_tmp_42;
		GCSignal[] f_tmp_44 = env.inputOfAlice(Utils.fromInt(__tmp37, 32));
		GCSignal f_tmp_43 = intLib.eq(op, f_tmp_44);
		GCSignal __tmp38 = f_tmp_43;
		GCSignal f_tmp_45 = intLib.and(__tmp36,__tmp38);
		GCSignal __tmp39 = f_tmp_45;
		GCSignal[] f_tmp_46 = reg.read(rs);
		GCSignal[] __tmp40 = f_tmp_46;
		GCSignal[] f_tmp_47 = intLib.add(__tmp40,unsignExt);
		GCSignal[] __tmp41 = f_tmp_47;
		GCSignal[] f_tmp_49 = env.inputOfAlice(Utils.fromInt(dataOffset, 32));
		GCSignal[] f_tmp_48 = intLib.sub(__tmp41,f_tmp_49);
		GCSignal[] __tmp42 = f_tmp_48;
		int f_tmp_50 = 2;
		int __tmp43 = f_tmp_50;
		GCSignal[] f_tmp_51 = intLib.rightPublicShift(__tmp42, __tmp43);
		GCSignal[] __tmp44 = f_tmp_51;
		GCSignal[] f_tmp_52 = intLib.mux(tmpindex, __tmp44,__tmp39);
		GCSignal[] __tmp45 = f_tmp_52;
		tmpindex = __tmp45;
		GCSignal[] f_tmp_53 = reg.read(rt);
		GCSignal[] __tmp46 = f_tmp_53;
		GCSignal[] f_tmp_54 = mem.read(tmpindex);
		GCSignal[] __tmp47 = f_tmp_54;
		GCSignal[] f_tmp_55 = intLib.mux(__tmp47, __tmp46,__tmp39);
		GCSignal[] __tmp48 = f_tmp_55;
		mem.write(tmpindex,__tmp48);
		GCSignal f_tmp_56 = intLib.not(__tmp38);
		GCSignal __tmp49 = f_tmp_56;
		GCSignal f_tmp_57 = intLib.and(__tmp36,__tmp49);
		GCSignal __tmp50 = f_tmp_57;
		return newInst;
	}
}
