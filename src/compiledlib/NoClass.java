package compiledlib;
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

	public t__T[] karatsubaMult(int n, t__T[] x, t__T[] y) throws Exception {
		t__T[] res = env.inputOfAlice(Utils.fromInt(0, (2)*(n)));
		t__T[] ret = env.inputOfAlice(Utils.fromInt(0, (2)*(n)));
		t__T[] a = env.inputOfAlice(Utils.fromInt(0, (n)-((n)/(2))));
		t__T[] b = env.inputOfAlice(Utils.fromInt(0, (n)/(2)));
		t__T[] c = env.inputOfAlice(Utils.fromInt(0, (n)-((n)/(2))));
		t__T[] d = env.inputOfAlice(Utils.fromInt(0, (n)/(2)));
		t__T[] term1 = env.inputOfAlice(Utils.fromInt(0, (2)*((n)-((n)/(2)))));
		t__T[] term2 = env.inputOfAlice(Utils.fromInt(0, (2)*((n)/(2))));
		t__T[] aPb = env.inputOfAlice(Utils.fromInt(0, ((n)-((n)/(2)))+(1)));
		t__T[] cPd = env.inputOfAlice(Utils.fromInt(0, ((n)-((n)/(2)))+(1)));
		t__T[] crossTerm = env.inputOfAlice(Utils.fromInt(0, (2)*(((n)-((n)/(2)))+(1))));
		t__T[] padTerm1 = env.inputOfAlice(Utils.fromInt(0, (2)*(n)));
		t__T[] padTerm2 = env.inputOfAlice(Utils.fromInt(0, (2)*(n)));
		t__T[] padTerm3 = env.inputOfAlice(Utils.fromInt(0, (2)*(n)));
		t__T[] sum = env.inputOfAlice(Utils.fromInt(0, (2)*(n)));
		int f_tmp_0 = 13;
		int __tmp0 = f_tmp_0;
		boolean f_tmp_1 = n < __tmp0;
		boolean __tmp1 = f_tmp_1;
		if(__tmp1) {
			t__T[] __tmp2 = intLib.enforceBits(x, (2)*(n));
			t__T[] __tmp3 = intLib.enforceBits(y, (2)*(n));
			t__T[] f_tmp_2 = intLib.multiply(__tmp2,__tmp3);
			t__T[] __tmp4 = f_tmp_2;
			res = __tmp4;
			ret = res;
			return res;
		} else {
			t__T[] __tmp5 = Arrays.copyOfRange(x, (n)/(2), n);
			a = __tmp5;
			t__T[] __tmp6 = Arrays.copyOfRange(x, 0, (n)/(2));
			t__T[] __tmp7 = intLib.enforceBits(__tmp6, (n)/(2));
			b = __tmp7;
			t__T[] __tmp8 = Arrays.copyOfRange(y, (n)/(2), n);
			c = __tmp8;
			t__T[] __tmp9 = Arrays.copyOfRange(y, 0, (n)/(2));
			t__T[] __tmp10 = intLib.enforceBits(__tmp9, (n)/(2));
			d = __tmp10;
			t__T[] f_tmp_3 = karatsubaMult((n)-((n)/(2)), a, c);
			t__T[] __tmp11 = f_tmp_3;
			t__T[] __tmp12 = intLib.enforceBits(__tmp11, (2)*((n)-((n)/(2))));
			term1 = __tmp12;
			t__T[] f_tmp_4 = karatsubaMult((n)/(2), b, d);
			t__T[] __tmp13 = f_tmp_4;
			t__T[] __tmp14 = intLib.enforceBits(__tmp13, (2)*((n)/(2)));
			term2 = __tmp14;
			t__T[] __tmp15 = intLib.enforceBits(a, ((n)-((n)/(2)))+(1));
			t__T[] __tmp16 = intLib.enforceBits(b, ((n)-((n)/(2)))+(1));
			t__T[] f_tmp_5 = intLib.add(__tmp15,__tmp16);
			t__T[] __tmp17 = f_tmp_5;
			aPb = __tmp17;
			t__T[] __tmp18 = intLib.enforceBits(c, ((n)-((n)/(2)))+(1));
			t__T[] __tmp19 = intLib.enforceBits(d, ((n)-((n)/(2)))+(1));
			t__T[] f_tmp_6 = intLib.add(__tmp18,__tmp19);
			t__T[] __tmp20 = f_tmp_6;
			cPd = __tmp20;
			t__T[] f_tmp_7 = karatsubaMult(((n)-((n)/(2)))+(1), cPd, aPb);
			t__T[] __tmp21 = f_tmp_7;
			t__T[] __tmp22 = intLib.enforceBits(__tmp21, (2)*(((n)-((n)/(2)))+(1)));
			crossTerm = __tmp22;
			t__T[] __tmp23 = intLib.enforceBits(term1, (2)*(n));
			padTerm1 = __tmp23;
			t__T[] __tmp24 = intLib.enforceBits(term2, (2)*(n));
			padTerm2 = __tmp24;
			t__T[] __tmp25 = intLib.enforceBits(crossTerm, (2)*(n));
			padTerm3 = __tmp25;
			int __tmp26 = n;
			int f_tmp_8 = 2;
			int __tmp27 = f_tmp_8;
			int f_tmp_9 = __tmp26 / __tmp27;
			int __tmp28 = f_tmp_9;
			int f_tmp_10 = 2;
			int __tmp29 = f_tmp_10;
			int f_tmp_11 = __tmp28 * __tmp29;
			int __tmp30 = f_tmp_11;
			t__T[] f_tmp_12 = intLib.leftPublicShift(padTerm1, __tmp30);
			t__T[] __tmp31 = f_tmp_12;
			t__T[] f_tmp_13 = intLib.add(__tmp31,padTerm2);
			t__T[] __tmp32 = f_tmp_13;
			t__T[] f_tmp_14 = intLib.sub(padTerm3,padTerm1);
			t__T[] __tmp33 = f_tmp_14;
			t__T[] f_tmp_15 = intLib.sub(__tmp33,padTerm2);
			t__T[] __tmp34 = f_tmp_15;
			int __tmp35 = n;
			int f_tmp_16 = 2;
			int __tmp36 = f_tmp_16;
			int f_tmp_17 = __tmp35 / __tmp36;
			int __tmp37 = f_tmp_17;
			t__T[] f_tmp_18 = intLib.leftPublicShift(__tmp34, __tmp37);
			t__T[] __tmp38 = f_tmp_18;
			t__T[] f_tmp_19 = intLib.add(__tmp32,__tmp38);
			t__T[] __tmp39 = f_tmp_19;
			sum = __tmp39;
			return sum;
		}

	}
}
