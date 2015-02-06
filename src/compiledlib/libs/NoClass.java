package compiledlib.libs;
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
public class NoClass<t__T> implements IWritable<NoClass<t__T>, t__T> {

	public CompEnv<t__T> env;
	public IntegerLib<t__T> intLib;
	public FloatLib<t__T> floatLib;

	public NoClass(CompEnv<t__T> env) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<t__T>(env);
		this.floatLib = new FloatLib<t__T>(env, 24, 8);
	}

	public int numBits() {
		return 0;
	}
	public t__T[] getBits() {
		t__T[] ret = env.newTArray(this.numBits());
		t__T[] tmp_b;
		t__T tmp;
		int now = 0;
		return ret;
}

	public NoClass<t__T> newObj(t__T[] data) throws Exception {
		if(data == null) {
			data = env.newTArray(this.numBits());
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		NoClass<t__T> ret = new NoClass<t__T>(env);
		t__T[] tmp;
		int now = 0;
		return ret;
}

	public Pair<t__T, bit<t__T>, Int<t__T>> add(int n, t__T[] x, t__T[] y) throws Exception {
		bit<t__T> cin = new bit<t__T>(env);
		Int<t__T> ret = new Int<t__T>(env, n);
		bit<t__T> t1 = new bit<t__T>(env);
		bit<t__T> t2 = new bit<t__T>(env);
		int i = 0;
		int f_tmp_0 = 0;
		int __tmp0 = f_tmp_0;
		i = __tmp0;
		boolean f_tmp_1 = i < n;
		boolean __tmp1 = f_tmp_1;
		while(__tmp1) {
			t__T __tmp2 = x[i];
			t__T f_tmp_2 = cin.v;
			t__T __tmp3 = f_tmp_2;
			t__T f_tmp_3 = intLib.xor(__tmp2,__tmp3);
			t__T __tmp4 = f_tmp_3;
			t1.v = __tmp4;
			t__T __tmp5 = y[i];
			t__T f_tmp_4 = cin.v;
			t__T __tmp6 = f_tmp_4;
			t__T f_tmp_5 = intLib.xor(__tmp5,__tmp6);
			t__T __tmp7 = f_tmp_5;
			t2.v = __tmp7;
			t__T __tmp8 = x[i];
			t__T f_tmp_6 = cin.v;
			t__T __tmp9 = f_tmp_6;
			t__T f_tmp_7 = intLib.xor(__tmp8,__tmp9);
			t__T __tmp10 = f_tmp_7;
			t__T[] f_tmp_8 = ret.v;
			t__T[] __tmp11 = f_tmp_8;
			__tmp11[i]=__tmp10;
			t__T f_tmp_9 = t1.v;
			t__T __tmp12 = f_tmp_9;
			t__T f_tmp_10 = t2.v;
			t__T __tmp13 = f_tmp_10;
			t__T f_tmp_11 = intLib.and(__tmp12,__tmp13);
			t__T __tmp14 = f_tmp_11;
			t1.v = __tmp14;
			t__T f_tmp_12 = cin.v;
			t__T __tmp15 = f_tmp_12;
			t__T f_tmp_13 = t1.v;
			t__T __tmp16 = f_tmp_13;
			t__T f_tmp_14 = intLib.xor(__tmp15,__tmp16);
			t__T __tmp17 = f_tmp_14;
			cin.v = __tmp17;
			int f_tmp_15 = 1;
			int __tmp18 = f_tmp_15;
			int f_tmp_16 = i + __tmp18;
			int __tmp19 = f_tmp_16;
			i = __tmp19;
			boolean f_tmp_17 = i < n;
			__tmp1 = f_tmp_17;
		}
		Pair<t__T, bit<t__T>, Int<t__T>> f_tmp_18 = new Pair<t__T, bit<t__T>, Int<t__T>>(env, new bit<t__T>(env), new Int<t__T>(env, n));
		f_tmp_18.left = cin;
		f_tmp_18.right = ret;
		Pair<t__T, bit<t__T>, Int<t__T>> __tmp20 = f_tmp_18;
		return __tmp20;

	}
	public t__T[] countOnes(int n, t__T[] x) throws Exception {
		t__T[] first = env.inputOfAlice(Utils.fromInt(0, Utils.logFloor(((n)-((n)/(2)))+(1))));
		t__T[] second = env.inputOfAlice(Utils.fromInt(0, Utils.logFloor(((n)-((n)/(2)))+(1))));
		Pair<t__T, bit<t__T>, Int<t__T>> ret = new Pair<t__T, bit<t__T>, Int<t__T>>(env, new bit<t__T>(env), new Int<t__T>(env, Utils.logFloor((n)-((n)/(2)))));
		t__T[] r = env.inputOfAlice(Utils.fromInt(0, Utils.logFloor((n)+(1))));
		int f_tmp_19 = 1;
		int __tmp21 = f_tmp_19;
		boolean f_tmp_20 = n == __tmp21;
		boolean __tmp22 = f_tmp_20;
		if(__tmp22) {
			return x;
		} else {
		}
		t__T[] __tmp23 = Arrays.copyOfRange(x, 0, (n)/(2));
		t__T[] f_tmp_21 = countOnes((n)/(2), __tmp23);
		t__T[] __tmp24 = f_tmp_21;
		t__T[] __tmp25 = intLib.enforceBits(__tmp24, Utils.logFloor(((n)-((n)/(2)))+(1)));
		first = __tmp25;
		t__T[] __tmp26 = Arrays.copyOfRange(x, (n)/(2), n);
		t__T[] f_tmp_22 = countOnes((n)-((n)/(2)), __tmp26);
		t__T[] __tmp27 = f_tmp_22;
		t__T[] __tmp28 = intLib.enforceBits(__tmp27, Utils.logFloor(((n)-((n)/(2)))+(1)));
		second = __tmp28;
		t__T[] __tmp29 = intLib.enforceBits(first, Utils.logFloor(((n)-((n)/(2)))+(1)));
		t__T[] __tmp30 = intLib.enforceBits(second, Utils.logFloor(((n)-((n)/(2)))+(1)));
		Pair<t__T, bit<t__T>, Int<t__T>> f_tmp_23 = add((n)-((n)/(2)), __tmp29, __tmp30);
		Pair<t__T, bit<t__T>, Int<t__T>> __tmp31 = f_tmp_23;
		ret = __tmp31;
		Int<t__T> f_tmp_24 = ret.right;
		Int<t__T> __tmp32 = f_tmp_24;
		t__T[] f_tmp_25 = __tmp32.v;
		t__T[] __tmp33 = f_tmp_25;
		t__T[] __tmp34 = intLib.enforceBits(__tmp33, Utils.logFloor((n)+(1)));
		r = __tmp34;
		bit<t__T> f_tmp_26 = ret.left;
		bit<t__T> __tmp35 = f_tmp_26;
		t__T f_tmp_27 = __tmp35.v;
		t__T __tmp36 = f_tmp_27;
		r[(Utils.logFloor((n)+(1)))-(1)]=__tmp36;
		return r;

	}
	public t__T[] leadingZero(int n, t__T[] x, t__T __isPhantom) throws Exception {
		x = intLib.mux(x, env.inputOfAlice(Utils.fromInt(0, n)), __isPhantom);
		t__T[] y = env.inputOfAlice(Utils.fromInt(0, n));
		int i = 0;
		int f_tmp_28 = 1;
		int __tmp37 = f_tmp_28;
		int __tmp38 = n;
		int f_tmp_29 = 1;
		int __tmp39 = f_tmp_29;
		int f_tmp_30 = __tmp38 - __tmp39;
		int __tmp40 = f_tmp_30;
		int f_tmp_31 = __tmp37 << __tmp40;
		int __tmp41 = f_tmp_31;
		t__T[] f_tmp_33 = env.inputOfAlice(Utils.fromInt(__tmp41, n));
		t__T[] __tmp42 = intLib.mux(y, f_tmp_33,__isPhantom);
		y = __tmp42;
		int f_tmp_34 = 2;
		int __tmp43 = f_tmp_34;
		int f_tmp_35 = n - __tmp43;
		int __tmp44 = f_tmp_35;
		i = __tmp44;
		int f_tmp_36 = 0;
		int __tmp45 = f_tmp_36;
		boolean f_tmp_37 = i >= __tmp45;
		boolean __tmp46 = f_tmp_37;
		while(__tmp46) {
			t__T __tmp47 = y[(i)+(1)];
			t__T __tmp48 = y[(i)+(1)];
			t__T __tmp49 = x[i];
			t__T f_tmp_38 = intLib.xor(__tmp48,__tmp49);
			t__T __tmp50 = f_tmp_38;
			t__T f_tmp_39 = intLib.and(__tmp47,__tmp50);
			t__T __tmp51 = f_tmp_39;
			t__T __tmp52 = y[i];
			t__T __tmp53 = intLib.mux(__tmp52, __tmp51,__isPhantom);
			y[i]=__tmp53;
			int f_tmp_41 = 1;
			int __tmp54 = f_tmp_41;
			int f_tmp_42 = i - __tmp54;
			int __tmp55 = f_tmp_42;
			i = __tmp55;
			int f_tmp_43 = 0;
			__tmp45 = f_tmp_43;
			boolean f_tmp_44 = i >= __tmp45;
			__tmp46 = f_tmp_44;
		}
		t__T[] f_tmp_45 = countOnes(n, y);
		t__T[] __tmp56 = f_tmp_45;
		return __tmp56;

	}
}
