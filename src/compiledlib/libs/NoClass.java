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

	public t__T[] countOnes(int n, t__T[] x) throws Exception {
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
		t__T[] f_tmp_2 = countOnes((n)/(2), __tmp4);
		t__T[] __tmp5 = f_tmp_2;
		t__T[] __tmp6 = intLib.enforceBits(__tmp5, Utils.logFloor(((n)/(2))+(1)));
		first = __tmp6;
		t__T[] __tmp7 = Arrays.copyOfRange(x, (n)/(2), n);
		t__T[] __tmp8 = intLib.enforceBits(__tmp7, (n)-((n)/(2)));
		t__T[] f_tmp_3 = countOnes((n)-((n)/(2)), __tmp8);
		t__T[] __tmp9 = f_tmp_3;
		t__T[] __tmp10 = intLib.enforceBits(__tmp9, Utils.logFloor(((n)-((n)/(2)))+(1)));
		second = __tmp10;
		t__T[] __tmp11 = intLib.enforceBits(first, Utils.logFloor((n)+(1)));
		t__T[] __tmp12 = intLib.enforceBits(second, Utils.logFloor((n)+(1)));
		t__T[] f_tmp_4 = intLib.add(__tmp11,__tmp12);
		t__T[] __tmp13 = f_tmp_4;
		r = __tmp13;
		return r;
	}
	public t__T[] leadingZero(int n, t__T[] x) throws Exception {
		t__T[] y = env.inputOfAlice(Utils.fromInt(0, n));
		int i = 0;
		int f_tmp_5 = 0;
		int __tmp14 = f_tmp_5;
		y = env.inputOfAlice(Utils.fromInt(__tmp14, n));
		t__T __tmp15 = x[(n)-(1)];
		t__T f_tmp_6 = intLib.not(__tmp15);
		t__T __tmp17 = f_tmp_6;
		boolean f_tmp_7 = true;
		boolean __tmp18 = f_tmp_7;
		t__T __tmp19 = y[(n)-(1)];
		t__T f_tmp_9 = env.inputOfAlice(__tmp18);
		t__T __tmp20 = intLib.mux(__tmp19, f_tmp_9,__tmp17);
		y[(n)-(1)]=__tmp20;
		int __tmp21 = n;
		int f_tmp_10 = 2;
		int __tmp22 = f_tmp_10;
		int f_tmp_11 = __tmp21 - __tmp22;
		int __tmp23 = f_tmp_11;
		i = __tmp23;
		int f_tmp_12 = 0;
		int __tmp24 = f_tmp_12;
		boolean f_tmp_13 = i >= __tmp24;
		boolean __tmp25 = f_tmp_13;
		while(__tmp25) {
			t__T __tmp26 = x[i];
			t__T f_tmp_14 = intLib.not(__tmp26);
			t__T __tmp28 = f_tmp_14;
			t__T __tmp29 = y[(i)+(1)];
			t__T __tmp31 = __tmp29;
			t__T f_tmp_15 = intLib.and(__tmp28,__tmp31);
			t__T __tmp32 = f_tmp_15;
			boolean f_tmp_16 = true;
			boolean __tmp33 = f_tmp_16;
			t__T __tmp34 = y[i];
			t__T f_tmp_18 = env.inputOfAlice(__tmp33);
			t__T __tmp35 = intLib.mux(__tmp34, f_tmp_18,__tmp32);
			y[i]=__tmp35;
			int __tmp36 = i;
			int f_tmp_19 = 1;
			int __tmp37 = f_tmp_19;
			int f_tmp_20 = __tmp36 - __tmp37;
			int __tmp38 = f_tmp_20;
			i = __tmp38;
			int f_tmp_21 = 0;
			__tmp24 = f_tmp_21;
			boolean f_tmp_22 = i >= __tmp24;
			__tmp25 = f_tmp_22;
		}
		t__T[] __tmp39 = intLib.enforceBits(y, n);
		t__T[] f_tmp_23 = countOnes(n, __tmp39);
		t__T[] __tmp40 = f_tmp_23;
		return __tmp40;
	}
	public t__T[] leadingZero2(int n, t__T[] x) throws Exception {
		t__T[] y = env.inputOfAlice(Utils.fromInt(0, n));
		int i = 0;
		int f_tmp_24 = 0;
		int __tmp41 = f_tmp_24;
		y = env.inputOfAlice(Utils.fromInt(__tmp41, n));
		int f_tmp_25 = 1;
		int __tmp42 = f_tmp_25;
		i = __tmp42;
		int __tmp43 = n;
		boolean f_tmp_26 = i < __tmp43;
		boolean __tmp44 = f_tmp_26;
		while(__tmp44) {
			t__T __tmp45 = x[i];
			t__T __tmp46 = __tmp45;//intLib.enforceBits(__tmp45, 1);
			y[i]=__tmp46;
			int __tmp47 = i;
			int f_tmp_27 = 1;
			int __tmp48 = f_tmp_27;
			int f_tmp_28 = __tmp47 + __tmp48;
			int __tmp49 = f_tmp_28;
			i = __tmp49;
			__tmp43 = n;
			boolean f_tmp_29 = i < __tmp43;
			__tmp44 = f_tmp_29;
		}
		int __tmp50 = n;
		int f_tmp_30 = 2;
		int __tmp51 = f_tmp_30;
		int f_tmp_31 = __tmp50 - __tmp51;
		int __tmp52 = f_tmp_31;
		i = __tmp52;
		int f_tmp_32 = 0;
		int __tmp53 = f_tmp_32;
		boolean f_tmp_33 = i >= __tmp53;
		boolean __tmp54 = f_tmp_33;
		while(__tmp54) {
			t__T __tmp55 = y[i];
			t__T __tmp57 = __tmp55;
			t__T __tmp58 = y[(i)+(1)];
			t__T __tmp60 = __tmp58;
			t__T f_tmp_34 = intLib.or(__tmp57,__tmp60);
			t__T __tmp61 = f_tmp_34;
			boolean f_tmp_35 = true;
			boolean __tmp62 = f_tmp_35;
			t__T __tmp63 = y[i];
			t__T f_tmp_37 = env.inputOfAlice(__tmp62);
			t__T __tmp64 = intLib.mux(__tmp63, f_tmp_37,__tmp61);
			y[i]=__tmp64;
			int __tmp65 = i;
			int f_tmp_38 = 1;
			int __tmp66 = f_tmp_38;
			int f_tmp_39 = __tmp65 - __tmp66;
			int __tmp67 = f_tmp_39;
			i = __tmp67;
			int f_tmp_40 = 0;
			__tmp53 = f_tmp_40;
			boolean f_tmp_41 = i >= __tmp53;
			__tmp54 = f_tmp_41;
		}
		t__T[] __tmp68 = intLib.enforceBits(y, n);
		t__T[] f_tmp_42 = countOnes(n, __tmp68);
		t__T[] __tmp69 = f_tmp_42;
		return __tmp69;
	}
}
