package compiledlib.queue;
import oram.CircuitOram;
import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.IWritable;
public class Queue<T extends IWritable<T,Boolean>> {
	public CircuitOram<Boolean> oram;
	public Boolean[] back_id;
	public Boolean[] front_id;
	public Boolean[] front_pos;
	public Boolean[] back_pos;

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;
	private T factoryT;
	private int m;

	public Queue(CompEnv<Boolean> env, IntegerLib<Boolean> lib, int m, T factoryT, CircuitOram<Boolean> oram) throws Exception {
		this.env = env;
		this.lib = lib;
		this.m = m;
		this.factoryT = factoryT;
		this.oram = oram;
		this.back_id = env.inputOfAlice(Utils.fromInt(0, m));
		this.front_id = env.inputOfAlice(Utils.fromInt(0, m));
		this.front_pos = env.inputOfAlice(Utils.fromInt(0, m));
		this.back_pos = env.inputOfAlice(Utils.fromInt(0, m));
	}

	public T queue_op(T operand, Boolean op) throws Exception {
		T ret = factoryT.newObj(null);
		QueueNode<T> node = new QueueNode<T>(env, lib, m, factoryT);
		Boolean[] tmp = env.inputOfAlice(Utils.fromInt(0, m));
		boolean f_tmp_0 = true;
		boolean __tmp0 = f_tmp_0;
		Boolean f_tmp_2 = env.inputOfAlice(__tmp0);
		Boolean f_tmp_1 = lib.eq(op, f_tmp_2);
		Boolean __tmp1 = f_tmp_1;
		Boolean[] f_tmp_3 = this.front_id;
		Boolean[] __tmp2 = f_tmp_3;
		Boolean[] f_tmp_4 = this.front_pos;
		Boolean[] __tmp3 = f_tmp_4;
		CircuitOram<Boolean> f_tmp_5 = this.oram;
		CircuitOram<Boolean> __tmp4 = f_tmp_5;
		QueueNode<T> f_tmp_6 = new QueueNode<T>(env, lib, m, factoryT).newObj(__tmp4.conditionalReadAndRemove(__tmp2, __tmp3, __tmp1));
		QueueNode<T> __tmp5 = f_tmp_6;
		QueueNode<T> f_tmp_7 = new QueueNode<T>(env, lib, m, factoryT).newObj(lib.mux(node.getBits(), __tmp5.getBits(),__tmp1));
		QueueNode<T> __tmp6 = f_tmp_7;
		node = __tmp6;
		Boolean[] f_tmp_8 = this.front_id;
		Boolean[] __tmp7 = f_tmp_8;
		int f_tmp_9 = 1;
		int __tmp8 = f_tmp_9;
		Boolean[] f_tmp_11 = env.inputOfAlice(Utils.fromInt(__tmp8, m));
		Boolean[] f_tmp_10 = lib.add(__tmp7,f_tmp_11);
		Boolean[] __tmp9 = f_tmp_10;
		Boolean[] f_tmp_12 = this.front_id;
		Boolean[] __tmp10 = f_tmp_12;
		Boolean[] f_tmp_13 = lib.mux(__tmp10, __tmp9,__tmp1);
		Boolean[] __tmp11 = f_tmp_13;
		this.front_id = __tmp11;
		Boolean[] f_tmp_14 = node.next;
		Boolean[] __tmp12 = f_tmp_14;
		Boolean[] f_tmp_15 = this.front_pos;
		Boolean[] __tmp13 = f_tmp_15;
		Boolean[] f_tmp_16 = lib.mux(__tmp13, __tmp12,__tmp1);
		Boolean[] __tmp14 = f_tmp_16;
		this.front_pos = __tmp14;
		T f_tmp_17 = node.data;
		T __tmp15 = f_tmp_17;
		T f_tmp_18 = this.factoryT.newObj(lib.mux(ret.getBits(), __tmp15.getBits(),__tmp1));
		T __tmp16 = f_tmp_18;
		ret = __tmp16;
		Boolean f_tmp_19 = lib.not(__tmp1);
		Boolean __tmp17 = f_tmp_19;
		Boolean[] f_tmp_20 = this.back_pos;
		Boolean[] __tmp18 = f_tmp_20;
		Boolean[] f_tmp_21 = lib.mux(tmp, __tmp18,__tmp17);
		Boolean[] __tmp19 = f_tmp_21;
		tmp = __tmp19;
		int f_tmp_22 = 32;
		int __tmp20 = f_tmp_22;
		Boolean[] f_tmp_23 = lib.randBools(__tmp20);
		Boolean[] __tmp21 = f_tmp_23;
		Boolean[] f_tmp_24 = this.back_pos;
		Boolean[] __tmp22 = f_tmp_24;
		Boolean[] f_tmp_25 = lib.mux(__tmp22, __tmp21,__tmp17);
		Boolean[] __tmp23 = f_tmp_25;
		this.back_pos = __tmp23;
		Boolean[] f_tmp_26 = this.back_pos;
		Boolean[] __tmp24 = f_tmp_26;
		QueueNode<T> f_tmp_27 = new QueueNode<T>(env, lib, m, factoryT);
		f_tmp_27.next = __tmp24;
		f_tmp_27.data = operand;
		QueueNode<T> __tmp25 = f_tmp_27;
		QueueNode<T> f_tmp_28 = new QueueNode<T>(env, lib, m, factoryT).newObj(lib.mux(node.getBits(), __tmp25.getBits(),__tmp17));
		QueueNode<T> __tmp26 = f_tmp_28;
		node = __tmp26;
		Boolean[] f_tmp_29 = this.back_id;
		Boolean[] __tmp27 = f_tmp_29;
		CircuitOram<Boolean> f_tmp_30 = this.oram;
		CircuitOram<Boolean> __tmp28 = f_tmp_30;
		__tmp28.conditionalPutBack(__tmp27, tmp, node.getBits(), __tmp17);
		Boolean[] f_tmp_32 = this.back_id;
		Boolean[] __tmp30 = f_tmp_32;
		int f_tmp_33 = 1;
		int __tmp31 = f_tmp_33;
		Boolean[] f_tmp_35 = env.inputOfAlice(Utils.fromInt(__tmp31, m));
		Boolean[] f_tmp_34 = lib.add(__tmp30,f_tmp_35);
		Boolean[] __tmp32 = f_tmp_34;
		Boolean[] f_tmp_36 = this.back_id;
		Boolean[] __tmp33 = f_tmp_36;
		Boolean[] f_tmp_37 = lib.mux(__tmp33, __tmp32,__tmp17);
		Boolean[] __tmp34 = f_tmp_37;
		this.back_id = __tmp34;
		return ret;
	}
}
