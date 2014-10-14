package compiledlib.stack;
import oram.SecureArray;
import util.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
import flexsc.IWritable;
public class OramStack<T extends IWritable<T,Boolean>> {
	public SecureArray<Boolean> data;
	public Boolean[] size;

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;
	private T factoryT;

	public OramStack(CompEnv<Boolean> env, IntegerLib<Boolean> lib, T factoryT, SecureArray<Boolean> data) throws Exception {
		this.env = env;
		this.lib = lib;
		this.factoryT = factoryT;
		this.data = data;
		this.size = env.inputOfAlice(Utils.fromInt(0, 32));
	}

	public T stack_op(T operand, Boolean op) throws Exception {
		T ret = factoryT.newObj(null);
		boolean f_tmp_0 = true;
		boolean __tmp0 = f_tmp_0;
		Boolean f_tmp_2 = env.inputOfAlice(__tmp0);
		Boolean f_tmp_1 = lib.eq(op, f_tmp_2);
		Boolean __tmp1 = f_tmp_1;
		SecureArray<Boolean> f_tmp_3 = this.data;
		SecureArray<Boolean> __tmp2 = f_tmp_3;
		Boolean[] f_tmp_4 = this.size;
		Boolean[] __tmp3 = f_tmp_4;
		T f_tmp_5 = factoryT.newObj(__tmp2.read(__tmp3));
		T __tmp4 = f_tmp_5;
		T f_tmp_6 = this.factoryT.newObj(lib.mux(ret.getBits(), __tmp4.getBits(),__tmp1));
		T __tmp5 = f_tmp_6;
		ret = __tmp5;
		Boolean[] f_tmp_7 = this.size;
		Boolean[] __tmp6 = f_tmp_7;
		int f_tmp_8 = 1;
		int __tmp7 = f_tmp_8;
		Boolean[] f_tmp_10 = env.inputOfAlice(Utils.fromInt(__tmp7, 32));
		Boolean[] f_tmp_9 = lib.sub(__tmp6,f_tmp_10);
		Boolean[] __tmp8 = f_tmp_9;
		Boolean[] f_tmp_11 = this.size;
		Boolean[] __tmp9 = f_tmp_11;
		Boolean[] f_tmp_12 = lib.mux(__tmp9, __tmp8,__tmp1);
		Boolean[] __tmp10 = f_tmp_12;
		this.size = __tmp10;
		Boolean f_tmp_13 = lib.not(__tmp1);
		Boolean __tmp11 = f_tmp_13;
		Boolean[] f_tmp_14 = this.size;
		Boolean[] __tmp12 = f_tmp_14;
		int f_tmp_15 = 1;
		int __tmp13 = f_tmp_15;
		Boolean[] f_tmp_17 = env.inputOfAlice(Utils.fromInt(__tmp13, 32));
		Boolean[] f_tmp_16 = lib.add(__tmp12,f_tmp_17);
		Boolean[] __tmp14 = f_tmp_16;
		Boolean[] f_tmp_18 = this.size;
		Boolean[] __tmp15 = f_tmp_18;
		Boolean[] f_tmp_19 = lib.mux(__tmp15, __tmp14,__tmp11);
		Boolean[] __tmp16 = f_tmp_19;
		this.size = __tmp16;
		SecureArray<Boolean> f_tmp_20 = this.data;
		SecureArray<Boolean> __tmp17 = f_tmp_20;
		Boolean[] f_tmp_21 = this.size;
		Boolean[] __tmp18 = f_tmp_21;
		T f_tmp_22 = factoryT.newObj(__tmp17.read(__tmp18));
		T __tmp19 = f_tmp_22;
		T f_tmp_23 = this.factoryT.newObj(lib.mux(__tmp19.getBits(), operand.getBits(),__tmp11));
		T __tmp20 = f_tmp_23;
		__tmp17.write(__tmp18,__tmp20.getBits());
		return ret;
	}
}
