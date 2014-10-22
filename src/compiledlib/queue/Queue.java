package compiledlib.queue;
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
public class Queue<T extends IWritable<T,GCSignal>> {
	public CircuitOram<GCSignal> oram;
	public GCSignal[] front_pos;
	public GCSignal[] back_pos;
	public GCSignal[] front_id;
	public GCSignal[] back_id;

	private CompEnv<GCSignal> env;
	private IntegerLib<GCSignal> lib;
	private T factoryT;
	private int m;

	public Queue(CompEnv<GCSignal> env, IntegerLib<GCSignal> lib, int m, T factoryT, CircuitOram<GCSignal> oram) throws Exception {
		this.env = env;
		this.lib = lib;
		this.m = m;
		this.factoryT = factoryT;
		this.oram = oram;
		this.front_pos = env.inputOfAlice(Utils.fromInt(0, m));
		this.back_pos = env.inputOfAlice(Utils.fromInt(0, m));
		this.front_id = env.inputOfAlice(Utils.fromInt(0, m));
		this.back_id = env.inputOfAlice(Utils.fromInt(0, m));
	}

	public T queue_op(T operand, GCSignal op) throws Exception {
		T ret = factoryT.newObj(null);
		QueueNode<T> node = new QueueNode<T>(env, lib, m, factoryT);
		GCSignal[] tmp = env.inputOfAlice(Utils.fromInt(0, m));
		boolean f_tmp_0 = true;
		boolean __tmp0 = f_tmp_0;
		GCSignal f_tmp_2 = env.inputOfAlice(__tmp0);
		GCSignal f_tmp_1 = lib.eq(op, f_tmp_2);
		GCSignal __tmp1 = f_tmp_1;
		GCSignal[] f_tmp_3 = this.front_id;
		GCSignal[] __tmp2 = f_tmp_3;
		GCSignal[] f_tmp_4 = this.front_pos;
		GCSignal[] __tmp3 = f_tmp_4;
		CircuitOram<GCSignal> f_tmp_5 = this.oram;
		CircuitOram<GCSignal> __tmp4 = f_tmp_5;
		QueueNode<T> f_tmp_6 = new QueueNode<T>(env, lib, m, factoryT).newObj(__tmp4.conditionalReadAndRemove(__tmp2, __tmp3, __tmp1));
		QueueNode<T> __tmp5 = f_tmp_6;
		QueueNode<T> f_tmp_7 = new QueueNode<T>(env, lib, m, factoryT).newObj(lib.mux(node.getBits(), __tmp5.getBits(),__tmp1));
		QueueNode<T> __tmp6 = f_tmp_7;
		node = __tmp6;
		GCSignal[] f_tmp_8 = this.front_id;
		GCSignal[] __tmp7 = f_tmp_8;
		int f_tmp_9 = 1;
		int __tmp8 = f_tmp_9;
		GCSignal[] f_tmp_11 = env.inputOfAlice(Utils.fromInt(__tmp8, m));
		GCSignal[] f_tmp_10 = lib.add(__tmp7,f_tmp_11);
		GCSignal[] __tmp9 = f_tmp_10;
		GCSignal[] f_tmp_12 = this.front_id;
		GCSignal[] __tmp10 = f_tmp_12;
		GCSignal[] f_tmp_13 = lib.mux(__tmp10, __tmp9,__tmp1);
		GCSignal[] __tmp11 = f_tmp_13;
		this.front_id = __tmp11;
		GCSignal[] f_tmp_14 = node.next;
		GCSignal[] __tmp12 = f_tmp_14;
		GCSignal[] f_tmp_15 = this.front_pos;
		GCSignal[] __tmp13 = f_tmp_15;
		GCSignal[] f_tmp_16 = lib.mux(__tmp13, __tmp12,__tmp1);
		GCSignal[] __tmp14 = f_tmp_16;
		this.front_pos = __tmp14;
		T f_tmp_17 = node.data;
		T __tmp15 = f_tmp_17;
		T f_tmp_18 = this.factoryT.newObj(lib.mux(ret.getBits(), __tmp15.getBits(),__tmp1));
		T __tmp16 = f_tmp_18;
		ret = __tmp16;
		GCSignal f_tmp_19 = lib.not(__tmp1);
		GCSignal __tmp17 = f_tmp_19;
		GCSignal[] f_tmp_20 = this.back_pos;
		GCSignal[] __tmp18 = f_tmp_20;
		GCSignal[] f_tmp_21 = lib.mux(tmp, __tmp18,__tmp17);
		GCSignal[] __tmp19 = f_tmp_21;
		tmp = __tmp19;
		int f_tmp_22 = 32;
		int __tmp20 = f_tmp_22;
		GCSignal[] f_tmp_23 = lib.randBools(__tmp20);
		GCSignal[] __tmp21 = f_tmp_23;
		GCSignal[] f_tmp_24 = this.back_pos;
		GCSignal[] __tmp22 = f_tmp_24;
		GCSignal[] f_tmp_25 = lib.mux(__tmp22, __tmp21,__tmp17);
		GCSignal[] __tmp23 = f_tmp_25;
		this.back_pos = __tmp23;
		GCSignal[] f_tmp_26 = this.back_pos;
		GCSignal[] __tmp24 = f_tmp_26;
		QueueNode<T> f_tmp_27 = new QueueNode<T>(env, lib, m, factoryT);
		f_tmp_27.next = __tmp24;
		f_tmp_27.data = operand;
		QueueNode<T> __tmp25 = f_tmp_27;
		QueueNode<T> f_tmp_28 = new QueueNode<T>(env, lib, m, factoryT).newObj(lib.mux(node.getBits(), __tmp25.getBits(),__tmp17));
		QueueNode<T> __tmp26 = f_tmp_28;
		node = __tmp26;
		GCSignal[] f_tmp_29 = this.back_id;
		GCSignal[] __tmp27 = f_tmp_29;
		CircuitOram<GCSignal> f_tmp_30 = this.oram;
		CircuitOram<GCSignal> __tmp28 = f_tmp_30;
		__tmp28.conditionalPutBack(__tmp27, tmp, node.getBits(), __tmp17);
		GCSignal[] f_tmp_32 = this.back_id;
		GCSignal[] __tmp30 = f_tmp_32;
		int f_tmp_33 = 1;
		int __tmp31 = f_tmp_33;
		GCSignal[] f_tmp_35 = env.inputOfAlice(Utils.fromInt(__tmp31, m));
		GCSignal[] f_tmp_34 = lib.add(__tmp30,f_tmp_35);
		GCSignal[] __tmp32 = f_tmp_34;
		GCSignal[] f_tmp_36 = this.back_id;
		GCSignal[] __tmp33 = f_tmp_36;
		GCSignal[] f_tmp_37 = lib.mux(__tmp33, __tmp32,__tmp17);
		GCSignal[] __tmp34 = f_tmp_37;
		this.back_id = __tmp34;
		return ret;
	}
}
