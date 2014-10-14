package compiledlib;

import oram.SecureArray;
import util.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.IWritable;

class MEM implements IWritable<MEM, Boolean> {

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;

	public MEM(CompEnv<Boolean> env, IntegerLib<Boolean> lib) throws Exception {
		this.env = env;
		this.lib = lib;
	}

	public int numBits() {
		int sum = 0;
		return sum;
	}

	public Boolean[] getBits() {
		Boolean[] ret = new Boolean[this.numBits()];
		Boolean[] tmp;
		int now = 0;
		return ret;
	}

	public MEM newObj(Boolean[] data) throws Exception {
		if (data == null) {
			data = new Boolean[this.numBits()];
			for (int i = 0; i < this.numBits(); ++i) {
				data[i] = lib.SIGNAL_ZERO;
			}
		}
		if (data.length != this.numBits())
			return null;
		MEM ret = new MEM(env, lib);
		Boolean[] tmp;
		int now = 0;
		return ret;
	}

	public Boolean[] function(SecureArray<Boolean> reg,
			SecureArray<Boolean> mem, Boolean[] pc, Boolean[] inst)
			throws Exception {
		Boolean[] index = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] newInst = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] rt = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] rs = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] unsignExt = env.inputOfAlice(Utils.fromInt(0, 32));
		Boolean[] op = env.inputOfAlice(Utils.fromInt(0, 32));
		int f_tmp_0 = 10;
		int __tmp0 = f_tmp_0;
		Boolean[] f_tmp_2 = env.inputOfAlice(Utils.fromInt(__tmp0, 32));
		Boolean[] f_tmp_1 = lib.sub(pc, f_tmp_2);
		Boolean[] __tmp1 = f_tmp_1;
		Boolean[] f_tmp_3 = lib.rightPublicShift(__tmp1, 2);
		Boolean[] __tmp2 = f_tmp_3;
		index = __tmp2;
		Boolean[] f_tmp_4 = mem.read(index);
		Boolean[] __tmp3 = f_tmp_4;
		newInst = __tmp3;
		Boolean[] f_tmp_5 = lib.leftPublicShift(inst, 11);
		Boolean[] __tmp4 = f_tmp_5;
		Boolean[] f_tmp_6 = lib.rightPublicShift(__tmp4, 27);
		Boolean[] __tmp5 = f_tmp_6;
		rt = __tmp5;
		Boolean[] f_tmp_7 = lib.leftPublicShift(inst, 6);
		Boolean[] __tmp6 = f_tmp_7;
		Boolean[] f_tmp_8 = lib.rightPublicShift(__tmp6, 27);
		Boolean[] __tmp7 = f_tmp_8;
		rs = __tmp7;
		Boolean[] f_tmp_9 = lib.leftPublicShift(inst, 16);
		Boolean[] __tmp8 = f_tmp_9;
		Boolean[] f_tmp_10 = lib.rightPublicShift(__tmp8, 16);
		Boolean[] __tmp9 = f_tmp_10;
		unsignExt = __tmp9;
		Boolean[] f_tmp_11 = lib.rightPublicShift(unsignExt, 15);
		Boolean[] __tmp10 = f_tmp_11;
		int f_tmp_12 = 1;
		int __tmp11 = f_tmp_12;
		Boolean[] f_tmp_14 = env.inputOfAlice(Utils.fromInt(__tmp11, 32));
		Boolean f_tmp_13 = lib.eq(__tmp10, f_tmp_14);
		Boolean __tmp12 = f_tmp_13;
		int f_tmp_15 = -65536;
		int __tmp13 = f_tmp_15;
		Boolean[] f_tmp_17 = env.inputOfAlice(Utils.fromInt(__tmp13, 32));
		Boolean[] f_tmp_16 = lib.add(unsignExt, f_tmp_17);
		Boolean[] __tmp14 = f_tmp_16;
		Boolean[] f_tmp_18 = lib.mux(unsignExt, __tmp14, __tmp12);
		Boolean[] __tmp15 = f_tmp_18;
		unsignExt = __tmp15;
		Boolean f_tmp_19 = lib.not(__tmp12);
		Boolean __tmp16 = f_tmp_19;
		Boolean[] f_tmp_20 = lib.rightPublicShift(inst, 26);
		Boolean[] __tmp17 = f_tmp_20;
		op = __tmp17;
		int f_tmp_21 = 35;
		int __tmp18 = f_tmp_21;
		Boolean[] f_tmp_23 = env.inputOfAlice(Utils.fromInt(__tmp18, 32));
		Boolean f_tmp_22 = lib.eq(op, f_tmp_23);
		Boolean __tmp19 = f_tmp_22;
		Boolean[] f_tmp_24 = reg.read(rs);
		Boolean[] __tmp20 = f_tmp_24;
		Boolean[] f_tmp_25 = lib.add(__tmp20, unsignExt);
		Boolean[] __tmp21 = f_tmp_25;
		int f_tmp_26 = 10;
		int __tmp22 = f_tmp_26;
		Boolean[] f_tmp_28 = env.inputOfAlice(Utils.fromInt(__tmp22, 32));
		Boolean[] f_tmp_27 = lib.sub(__tmp21, f_tmp_28);
		Boolean[] __tmp23 = f_tmp_27;
		Boolean[] f_tmp_29 = lib.rightPublicShift(__tmp23, 2);
		Boolean[] __tmp24 = f_tmp_29;
		Boolean[] f_tmp_30 = mem.read(__tmp24);
		Boolean[] __tmp25 = f_tmp_30;
		Boolean[] f_tmp_31 = reg.read(rt);
		Boolean[] __tmp26 = f_tmp_31;
		Boolean[] f_tmp_32 = lib.mux(__tmp26, __tmp25, __tmp19);
		Boolean[] __tmp27 = f_tmp_32;
		reg.write(rt, __tmp27);
		Boolean f_tmp_33 = lib.not(__tmp19);
		Boolean __tmp28 = f_tmp_33;
		int f_tmp_34 = 43;
		int __tmp29 = f_tmp_34;
		Boolean[] f_tmp_36 = env.inputOfAlice(Utils.fromInt(__tmp29, 32));
		Boolean f_tmp_35 = lib.eq(op, f_tmp_36);
		Boolean __tmp30 = f_tmp_35;
		Boolean f_tmp_37 = lib.and(__tmp28, __tmp30);
		Boolean __tmp31 = f_tmp_37;
		Boolean[] f_tmp_38 = reg.read(rt);
		Boolean[] __tmp32 = f_tmp_38;
		Boolean[] f_tmp_39 = reg.read(rs);
		Boolean[] __tmp33 = f_tmp_39;
		Boolean[] f_tmp_40 = lib.add(__tmp33, unsignExt);
		Boolean[] __tmp34 = f_tmp_40;
		int f_tmp_41 = 10;
		int __tmp35 = f_tmp_41;
		Boolean[] f_tmp_43 = env.inputOfAlice(Utils.fromInt(__tmp35, 32));
		Boolean[] f_tmp_42 = lib.sub(__tmp34, f_tmp_43);
		Boolean[] __tmp36 = f_tmp_42;
		Boolean[] f_tmp_44 = lib.rightPublicShift(__tmp36, 2);
		Boolean[] __tmp37 = f_tmp_44;
		Boolean[] f_tmp_45 = mem.read(__tmp37);
		Boolean[] __tmp38 = f_tmp_45;
		Boolean[] f_tmp_46 = lib.mux(__tmp38, __tmp32, __tmp31);
		Boolean[] __tmp39 = f_tmp_46;
		mem.write(__tmp37, __tmp39);
		Boolean f_tmp_47 = lib.not(__tmp30);
		Boolean __tmp40 = f_tmp_47;
		Boolean f_tmp_48 = lib.and(__tmp28, __tmp40);
		Boolean __tmp41 = f_tmp_48;
		return newInst;
	}
}
