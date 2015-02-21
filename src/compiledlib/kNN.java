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
public class kNN<t__T> implements IWritable<kNN<t__T>, t__T> {

	public CompEnv<t__T> env;
	public IntegerLib<t__T> intLib;
	public FloatLib<t__T> floatLib;

	public kNN(CompEnv<t__T> env) throws Exception {
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

	public kNN<t__T> newObj(t__T[] data) throws Exception {
		if(data == null) {
			data = env.newTArray(this.numBits());
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		kNN<t__T> ret = new kNN<t__T>(env);
		t__T[] tmp;
		int now = 0;
		return ret;
}

	public t__T[] distance(Point<t__T> p1, Point<t__T> p2) throws Exception {
		t__T[] ret = env.inputOfAlice(Utils.fromInt(0, 32));
		int i = 0;
		int f_tmp_0 = 0;
		int __tmp0 = f_tmp_0;
		ret = env.inputOfAlice(Utils.fromInt(__tmp0, 32));
		int f_tmp_1 = 0;
		int __tmp1 = f_tmp_1;
		i = __tmp1;
		int f_tmp_2 = 2;
		int __tmp2 = f_tmp_2;
		boolean f_tmp_3 = i < __tmp2;
		boolean __tmp3 = f_tmp_3;
		while(__tmp3) {
			t__T[][] f_tmp_4 = p1.cor;
			t__T[][] __tmp4 = f_tmp_4;
			t__T[] f_tmp_5 = __tmp4[i];
			t__T[] __tmp5 = f_tmp_5;
			t__T[][] f_tmp_6 = p2.cor;
			t__T[][] __tmp6 = f_tmp_6;
			t__T[] f_tmp_7 = __tmp6[i];
			t__T[] __tmp7 = f_tmp_7;
			t__T[] f_tmp_8 = intLib.sub(__tmp5,__tmp7);
			t__T[] __tmp8 = f_tmp_8;
			t__T[][] f_tmp_9 = p1.cor;
			t__T[][] __tmp9 = f_tmp_9;
			t__T[] f_tmp_10 = __tmp9[i];
			t__T[] __tmp10 = f_tmp_10;
			t__T[][] f_tmp_11 = p2.cor;
			t__T[][] __tmp11 = f_tmp_11;
			t__T[] f_tmp_12 = __tmp11[i];
			t__T[] __tmp12 = f_tmp_12;
			t__T[] f_tmp_13 = intLib.sub(__tmp10,__tmp12);
			t__T[] __tmp13 = f_tmp_13;
			t__T[] f_tmp_14 = intLib.multiply(__tmp8,__tmp13);
			t__T[] __tmp14 = f_tmp_14;
			t__T[] f_tmp_15 = intLib.add(ret,__tmp14);
			t__T[] __tmp15 = f_tmp_15;
			ret = __tmp15;
			int f_tmp_16 = 1;
			int __tmp16 = f_tmp_16;
			int f_tmp_17 = i + __tmp16;
			int __tmp17 = f_tmp_17;
			i = __tmp17;
			int f_tmp_18 = 2;
			__tmp2 = f_tmp_18;
			boolean f_tmp_19 = i < __tmp2;
			__tmp3 = f_tmp_19;
		}
		return ret;

	}
	public t__T[] function(Point<t__T>[] data, t__T[][] labels, Point<t__T> query) throws Exception {
		t__T[][] dist = new t__T[1000][];
		for(int _j_2 = 0; _j_2 < 1000; ++_j_2) {
			dist[_j_2] = env.inputOfAlice(Utils.fromInt(0, 32));
		}
		int i = 0;
		t__T[] ret = env.inputOfAlice(Utils.fromInt(0, 32));
		int f_tmp_20 = 0;
		int __tmp18 = f_tmp_20;
		i = __tmp18;
		int f_tmp_21 = 1000;
		int __tmp19 = f_tmp_21;
		boolean f_tmp_22 = i < __tmp19;
		boolean __tmp20 = f_tmp_22;
		while(__tmp20) {
			Point<t__T> f_tmp_23 = data[i];
			Point<t__T> __tmp21 = f_tmp_23;
			t__T[] f_tmp_24 = this.distance(__tmp21, query);
			t__T[] __tmp22 = f_tmp_24;
			dist[i]=__tmp22;
			int f_tmp_25 = 1;
			int __tmp23 = f_tmp_25;
			int f_tmp_26 = i + __tmp23;
			int __tmp24 = f_tmp_26;
			i = __tmp24;
			int f_tmp_27 = 1000;
			__tmp19 = f_tmp_27;
			boolean f_tmp_28 = i < __tmp19;
			__tmp20 = f_tmp_28;
		}
		t__T[][] f_tmp_30 = new t__T[1000][];
		for(int _tmp_i=0; _tmp_i<1000; ++_tmp_i) {
			f_tmp_30[_tmp_i] = dist[_tmp_i].getBits();
		}
		t__T[][] f_tmp_31 = new t__T[1000][];
		for(int _tmp_i=0; _tmp_i<1000; ++_tmp_i) {
			f_tmp_31[_tmp_i] = labels[_tmp_i].getBits();
		}
		intLib.sort(f_tmp_30, f_tmp_31);
		int f_tmp_32 = 0;
		int __tmp26 = f_tmp_32;
		i = __tmp26;
		int f_tmp_33 = 4;
		int __tmp27 = f_tmp_33;
		boolean f_tmp_34 = i < __tmp27;
		boolean __tmp28 = f_tmp_34;
		while(__tmp28) {
			t__T[] f_tmp_35 = labels[i];
			t__T[] __tmp29 = f_tmp_35;
			t__T[] f_tmp_36 = intLib.add(ret,__tmp29);
			t__T[] __tmp30 = f_tmp_36;
			ret = __tmp30;
			int f_tmp_37 = 1;
			int __tmp31 = f_tmp_37;
			int f_tmp_38 = i + __tmp31;
			int __tmp32 = f_tmp_38;
			i = __tmp32;
			int f_tmp_39 = 4;
			__tmp27 = f_tmp_39;
			boolean f_tmp_40 = i < __tmp27;
			__tmp28 = f_tmp_40;
		}
		return ret;

	}
}
