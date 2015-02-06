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
public class NoClass<t__T> implements IWritable<NoClass, t__T> {

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

	public NoClass newObj(t__T[] data) throws Exception {
		if(data == null) {
			data = env.newTArray(this.numBits());
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		NoClass ret = new NoClass(env);
		t__T[] tmp;
		int now = 0;
		return ret;
}

	public t__T[] countOnes(int n, t__T[] x, t__T __isPhantom) throws Exception {
		x = intLib.mux(x, env.inputOfAlice(Utils.fromInt(0, n)), __isPhantom);
		t__T[] first = env.inputOfAlice(Utils.fromInt(0, Utils.logFloor(((n)/(2))+(1))));
		t__T[] second = env.inputOfAlice(Utils.fromInt(0, Utils.logFloor(((n)-((n)/(2)))+(1))));
		t__T[] r = env.inputOfAlice(Utils.fromInt(0, Utils.logFloor((n)+(1))));
		int __tmp0 = n;
		int f_tmp_0 = 1;
		int __tmp1 = f_tmp_0;
		boolean f_tmp_1 = __tmp0 == __tmp1;
		boolean __tmp2 = f_tmp_1;
		if(__tmp2) {
			return x;		} else {
		}
		t__T[] __tmp3 = Arrays.copyOfRange(x, 0, (n)/(2));
		t__T[] __tmp4 = intLib.enforceBits(__tmp3, ((n)/(2))-(0));
		boolean f_tmp_2 = true;
		t__T __tmp5 = env.inputOfAlice(f_tmp_2);
		t__T[] f_tmp_3 = countOnes((n)/(2), __tmp4, __tmp5);
		t__T[] __tmp6 = f_tmp_3;
		t__T[] __tmp7 = intLib.enforceBits(__tmp6, Utils.logFloor(((n)/(2))+(1)));
		t__T[] __tmp8 = intLib.mux(first, __tmp7,__isPhantom);
		first = __tmp8;
		t__T[] __tmp9 = Arrays.copyOfRange(x, (n)/(2), n);
		t__T[] __tmp10 = intLib.enforceBits(__tmp9, (n)-((n)/(2)));
		boolean f_tmp_5 = true;
		t__T __tmp11 = env.inputOfAlice(f_tmp_5);
		t__T[] f_tmp_6 = countOnes((n)-((n)/(2)), __tmp10, __tmp11);
		t__T[] __tmp12 = f_tmp_6;
		t__T[] __tmp13 = intLib.enforceBits(__tmp12, Utils.logFloor(((n)-((n)/(2)))+(1)));
		t__T[] __tmp14 = intLib.mux(second, __tmp13,__isPhantom);
		second = __tmp14;
		t__T[] __tmp15 = intLib.enforceBits(first, Utils.logFloor((n)+(1)));
		t__T[] __tmp16 = intLib.enforceBits(second, Utils.logFloor((n)+(1)));
		t__T[] f_tmp_8 = intLib.add(__tmp15,__tmp16);
		t__T[] __tmp17 = f_tmp_8;
		t__T[] __tmp18 = intLib.mux(r, __tmp17,__isPhantom);
		r = __tmp18;
		return r;
	}
	public t__T[] leadingZero(int n, t__T[] x, t__T __isPhantom) throws Exception {
		x = intLib.mux(x, env.inputOfAlice(Utils.fromInt(0, n)), __isPhantom);
		t__T[] y = env.inputOfAlice(Utils.fromInt(0, n));
		int i = 0;
		int f_tmp_10 = 0;
		int __tmp19 = f_tmp_10;
		t__T[] f_tmp_12 = env.inputOfAlice(Utils.fromInt(__tmp19, n));
		t__T[] __tmp20 = intLib.mux(y, f_tmp_12,__isPhantom);
		y = __tmp20;
		t__T __tmp21 = x[(n)-(1)];
		t__T f_tmp_13 = intLib.not(__tmp21);
		t__T __tmp23 = f_tmp_13;
		t__T f_tmp_14 = intLib.and(__isPhantom,__tmp23);
		t__T __tmp24 = f_tmp_14;
		boolean f_tmp_15 = true;
		boolean __tmp25 = f_tmp_15;
		t__T __tmp26 = y[(n)-(1)];
		t__T f_tmp_17 = env.inputOfAlice(__tmp25);
		t__T __tmp27 = intLib.mux(__tmp26, f_tmp_17,__tmp24);
		y[(n)-(1)]=__tmp27;
		int __tmp28 = n;
		int f_tmp_18 = 2;
		int __tmp29 = f_tmp_18;
		int f_tmp_19 = __tmp28 - __tmp29;
		int __tmp30 = f_tmp_19;
		i = __tmp30;
		int f_tmp_20 = 0;
		int __tmp31 = f_tmp_20;
		boolean f_tmp_21 = i >= __tmp31;
		boolean __tmp32 = f_tmp_21;
		while(__tmp32) {
			t__T __tmp33 = x[i];
			t__T f_tmp_22 = intLib.not(__tmp33);
			t__T __tmp35 = f_tmp_22;
			t__T __tmp36 = y[(i)+(1)];
			t__T __tmp38 = __tmp36;
			t__T f_tmp_23 = intLib.and(__tmp35,__tmp38);
			t__T __tmp39 = f_tmp_23;
			t__T f_tmp_24 = intLib.and(__isPhantom,__tmp39);
			t__T __tmp40 = f_tmp_24;
			boolean f_tmp_25 = true;
			boolean __tmp41 = f_tmp_25;
			t__T __tmp42 = y[i];
			t__T f_tmp_27 = env.inputOfAlice(__tmp41);
			t__T __tmp43 = intLib.mux(__tmp42, f_tmp_27,__tmp40);
			y[i]=__tmp43;
			int __tmp44 = i;
			int f_tmp_28 = 1;
			int __tmp45 = f_tmp_28;
			int f_tmp_29 = __tmp44 - __tmp45;
			int __tmp46 = f_tmp_29;
			i = __tmp46;
			int f_tmp_30 = 0;
			__tmp31 = f_tmp_30;
			boolean f_tmp_31 = i >= __tmp31;
			__tmp32 = f_tmp_31;
		}
		t__T[] __tmp47 = intLib.enforceBits(y, n);
		boolean f_tmp_32 = true;
		t__T __tmp48 = env.inputOfAlice(f_tmp_32);
		t__T[] f_tmp_33 = countOnes(n, __tmp47, __tmp48);
		t__T[] __tmp49 = f_tmp_33;
		return __tmp49;
	}
}
