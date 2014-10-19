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
public class MEM implements IWritable<MEM, Boolean> {

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;

	public MEM(CompEnv<Boolean> env, IntegerLib<Boolean> lib) throws Exception {
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

	public MEM newObj(Boolean[] data) throws Exception {
		if(data == null) {
			data = new Boolean[this.numBits()];
			for(int i=0; i<this.numBits(); ++i) { data[i] = lib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		MEM ret = new MEM(env, lib);
		Boolean[] tmp;
		int now = 0;
		return ret;
}

	public Boolean[] func(SecureArray<Boolean> reg, SecureArray<Boolean> mem, Boolean[] pc, Boolean[] inst, int pcOffset, int dataOffset) throws Exception {
		Boolean[] index = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] newInst = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] rt = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] rs = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] unsignExt = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] op = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] tmpindex = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] f_tmp_1 = env.inputOfAlice(Utils.fromInt(pcOffset, 32));
		Boolean[] f_tmp_0 = lib.sub(pc,f_tmp_1);
		Boolean[] __tmp0 = f_tmp_0;
		Boolean[] f_tmp_2 = lib.rightPublicShift(__tmp0, 2);
		Boolean[] __tmp1 = f_tmp_2;
		index = __tmp1;
		Boolean[] f_tmp_3 = mem.read(index);
		Boolean[] __tmp2 = f_tmp_3;
		newInst = __tmp2;
		Boolean[] f_tmp_4 = lib.leftPublicShift(inst, 11);
		Boolean[] __tmp3 = f_tmp_4;
		Boolean[] f_tmp_5 = lib.rightPublicShift(__tmp3, 27);
		Boolean[] __tmp4 = f_tmp_5;
		rt = __tmp4;
		Boolean[] f_tmp_6 = lib.leftPublicShift(inst, 6);
		Boolean[] __tmp5 = f_tmp_6;
		Boolean[] f_tmp_7 = lib.rightPublicShift(__tmp5, 27);
		Boolean[] __tmp6 = f_tmp_7;
		rs = __tmp6;
		Boolean[] f_tmp_8 = lib.leftPublicShift(inst, 16);
		Boolean[] __tmp7 = f_tmp_8;
		Boolean[] f_tmp_9 = lib.rightPublicShift(__tmp7, 16);
		Boolean[] __tmp8 = f_tmp_9;
		unsignExt = __tmp8;
		Boolean[] f_tmp_10 = lib.rightPublicShift(unsignExt, 15);
		Boolean[] __tmp9 = f_tmp_10;
		int f_tmp_11 = 1;
		int __tmp10 = f_tmp_11;
		Boolean[] f_tmp_13 = env.inputOfAlice(Utils.fromInt(__tmp10, 32));
		Boolean f_tmp_12 = lib.eq(__tmp9, f_tmp_13);
		Boolean __tmp11 = f_tmp_12;
		int f_tmp_14 = -65536;
		int __tmp12 = f_tmp_14;
		Boolean[] f_tmp_16 = env.inputOfAlice(Utils.fromInt(__tmp12, 32));
		Boolean[] f_tmp_15 = lib.add(unsignExt,f_tmp_16);
		Boolean[] __tmp13 = f_tmp_15;
		Boolean[] f_tmp_17 = lib.mux(unsignExt, __tmp13,__tmp11);
		Boolean[] __tmp14 = f_tmp_17;
		unsignExt = __tmp14;
		Boolean f_tmp_18 = lib.not(__tmp11);
		Boolean __tmp15 = f_tmp_18;
		Boolean[] f_tmp_19 = lib.rightPublicShift(inst, 26);
		Boolean[] __tmp16 = f_tmp_19;
		op = __tmp16;
		int f_tmp_20 = 35;
		int __tmp17 = f_tmp_20;
		Boolean[] f_tmp_22 = env.inputOfAlice(Utils.fromInt(__tmp17, 32));
		Boolean f_tmp_21 = lib.eq(op, f_tmp_22);
		Boolean __tmp18 = f_tmp_21;
		Boolean[] f_tmp_23 = reg.read(rs);
		Boolean[] __tmp19 = f_tmp_23;
		Boolean[] f_tmp_24 = lib.add(__tmp19,unsignExt);
		Boolean[] __tmp20 = f_tmp_24;
		Boolean[] f_tmp_26 = env.inputOfAlice(Utils.fromInt(dataOffset, 32));
		Boolean[] f_tmp_25 = lib.sub(__tmp20,f_tmp_26);
		Boolean[] __tmp21 = f_tmp_25;
		Boolean[] f_tmp_27 = lib.rightPublicShift(__tmp21, 2);
		Boolean[] __tmp22 = f_tmp_27;
		Boolean[] f_tmp_28 = mem.read(__tmp22);
		Boolean[] __tmp23 = f_tmp_28;
		Boolean[] f_tmp_29 = reg.read(rt);
		Boolean[] __tmp24 = f_tmp_29;
		Boolean[] f_tmp_30 = lib.mux(__tmp24, __tmp23,__tmp18);
		Boolean[] __tmp25 = f_tmp_30;
		reg.write(rt,__tmp25);
		Boolean f_tmp_31 = lib.not(__tmp18);
		Boolean __tmp26 = f_tmp_31;
		int f_tmp_32 = 43;
		int __tmp27 = f_tmp_32;
		Boolean[] f_tmp_34 = env.inputOfAlice(Utils.fromInt(__tmp27, 32));
		Boolean f_tmp_33 = lib.eq(op, f_tmp_34);
		Boolean __tmp28 = f_tmp_33;
		Boolean f_tmp_35 = lib.and(__tmp26,__tmp28);
		Boolean __tmp29 = f_tmp_35;
		Boolean[] f_tmp_36 = reg.read(rs);
		Boolean[] __tmp30 = f_tmp_36;
		Boolean[] f_tmp_37 = lib.add(__tmp30,unsignExt);
		Boolean[] __tmp31 = f_tmp_37;
		Boolean[] f_tmp_39 = env.inputOfAlice(Utils.fromInt(dataOffset, 32));
		Boolean[] f_tmp_38 = lib.sub(__tmp31,f_tmp_39);
		Boolean[] __tmp32 = f_tmp_38;
		Boolean[] f_tmp_40 = lib.rightPublicShift(__tmp32, 2);
		Boolean[] __tmp33 = f_tmp_40;
		Boolean[] f_tmp_41 = lib.mux(tmpindex, __tmp33,__tmp29);
		Boolean[] __tmp34 = f_tmp_41;
		tmpindex = __tmp34;
		Boolean[] f_tmp_42 = reg.read(rt);
		Boolean[] __tmp35 = f_tmp_42;
		Boolean[] f_tmp_43 = mem.read(tmpindex);
		Boolean[] __tmp36 = f_tmp_43;
		Boolean[] f_tmp_44 = lib.mux(__tmp36, __tmp35,__tmp29);
		Boolean[] __tmp37 = f_tmp_44;
		mem.write(tmpindex,__tmp37);
		Boolean f_tmp_45 = lib.not(__tmp28);
		Boolean __tmp38 = f_tmp_45;
		Boolean f_tmp_46 = lib.and(__tmp26,__tmp38);
		Boolean __tmp39 = f_tmp_46;
		return newInst;
	}
}
