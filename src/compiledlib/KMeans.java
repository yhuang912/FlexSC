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
public class KMeans<t__T> implements IWritable<KMeans<t__T>, t__T> {

	public CompEnv<t__T> env;
	public IntegerLib<t__T> intLib;
	public FloatLib<t__T> floatLib;

	public KMeans(CompEnv<t__T> env) throws Exception {
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

	public KMeans<t__T> newObj(t__T[] data) throws Exception {
		if(data == null) {
			data = env.newTArray(this.numBits());
			for(int i=0; i<this.numBits(); ++i) { data[i] = intLib.SIGNAL_ZERO; }
		}
		if(data.length != this.numBits()) return null;
		KMeans<t__T> ret = new KMeans<t__T>(env);
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
	public Pair<t__T, Int<t__T>, Pair<t__T, Point<t__T>, Int<t__T>>> map(Point<t__T> p, Point<t__T>[] center) throws Exception {
		t__T[] dist = env.inputOfAlice(Utils.fromInt(0, 32));
		t__T[] id = env.inputOfAlice(Utils.fromInt(0, 32));
		int i = 0;
		t__T[] new_dist = env.inputOfAlice(Utils.fromInt(0, 32));
		int f_tmp_20 = 0;
		int __tmp18 = f_tmp_20;
		Point<t__T> f_tmp_21 = center[__tmp18];
		Point<t__T> __tmp19 = f_tmp_21;
		t__T[] f_tmp_22 = this.distance(p, __tmp19);
		t__T[] __tmp20 = f_tmp_22;
		dist = __tmp20;
		int f_tmp_23 = 0;
		int __tmp21 = f_tmp_23;
		id = env.inputOfAlice(Utils.fromInt(__tmp21, 32));
		int f_tmp_24 = 1;
		int __tmp22 = f_tmp_24;
		i = __tmp22;
		int f_tmp_25 = 1000;
		int __tmp23 = f_tmp_25;
		boolean f_tmp_26 = i < __tmp23;
		boolean __tmp24 = f_tmp_26;
		while(__tmp24) {
			Point<t__T> f_tmp_27 = center[i];
			Point<t__T> __tmp25 = f_tmp_27;
			t__T[] f_tmp_28 = this.distance(p, __tmp25);
			t__T[] __tmp26 = f_tmp_28;
			new_dist = __tmp26;
			t__T f_tmp_29 = intLib.not(intLib.leq(dist, new_dist));
			t__T __tmp27 = f_tmp_29;
			t__T[] __tmp28 = intLib.mux(dist, new_dist,__tmp27);
			dist = __tmp28;
			t__T[] f_tmp_32 = env.inputOfAlice(Utils.fromInt(i, 32));
			t__T[] __tmp29 = intLib.mux(id, f_tmp_32,__tmp27);
			id = __tmp29;
			int f_tmp_33 = 1;
			int __tmp30 = f_tmp_33;
			int f_tmp_34 = i + __tmp30;
			int __tmp31 = f_tmp_34;
			i = __tmp31;
			int f_tmp_35 = 1000;
			__tmp23 = f_tmp_35;
			boolean f_tmp_36 = i < __tmp23;
			__tmp24 = f_tmp_36;
		}
		Int<t__T> f_tmp_37 = new Int<t__T>(env);
		f_tmp_37.data = id;
		Int<t__T> __tmp32 = f_tmp_37;
		int f_tmp_38 = 1;
		int __tmp33 = f_tmp_38;
		Int<t__T> f_tmp_39 = new Int<t__T>(env);
		f_tmp_39.data = env.inputOfAlice(Utils.fromInt(__tmp33, 32));
		Int<t__T> __tmp34 = f_tmp_39;
		Pair<t__T, Point<t__T>, Int<t__T>> f_tmp_40 = new Pair<t__T, Point<t__T>, Int<t__T>>(env, new Point<t__T>(env, new t__T[2][]), new Int<t__T>(env), new Point<t__T>(env, new t__T[2][]));
		f_tmp_40.first = p;
		f_tmp_40.second = __tmp34;
		Pair<t__T, Point<t__T>, Int<t__T>> __tmp35 = f_tmp_40;
		Pair<t__T, Int<t__T>, Pair<t__T, Point<t__T>, Int<t__T>>> f_tmp_41 = new Pair<t__T, Int<t__T>, Pair<t__T, Point<t__T>, Int<t__T>>>(env, new Int<t__T>(env), new Pair<t__T, Point<t__T>, Int<t__T>>(env, new Point<t__T>(env, new t__T[2][]), new Int<t__T>(env), new Point<t__T>(env, new t__T[2][])), new Pair<t__T, Point<t__T>, Int<t__T>>(env, new Point<t__T>(env, new t__T[2][]), new Int<t__T>(env), new Point<t__T>(env, new t__T[2][])));
		f_tmp_41.first = __tmp32;
		f_tmp_41.second = __tmp35;
		Pair<t__T, Int<t__T>, Pair<t__T, Point<t__T>, Int<t__T>>> __tmp36 = f_tmp_41;
		return __tmp36;

	}
	public Pair<t__T, Point<t__T>, Int<t__T>> reduce(t__T[] id, Pair<t__T, Point<t__T>, Int<t__T>> val1, Pair<t__T, Point<t__T>, Int<t__T>> val2) throws Exception {
		Point<t__T> added = new Point<t__T>(env, new t__T[2][]);
		for(int _j_2 = 0; _j_2 < 2; ++_j_2) {
			added.cor[_j_2] = env.inputOfAlice(Utils.fromInt(0, 32));
		}
		int i = 0;
		int f_tmp_42 = 0;
		int __tmp37 = f_tmp_42;
		i = __tmp37;
		int f_tmp_43 = 2;
		int __tmp38 = f_tmp_43;
		boolean f_tmp_44 = i < __tmp38;
		boolean __tmp39 = f_tmp_44;
		while(__tmp39) {
			Point<t__T> f_tmp_45 = val1.first;
			Point<t__T> __tmp40 = f_tmp_45;
			t__T[][] f_tmp_46 = __tmp40.cor;
			t__T[][] __tmp41 = f_tmp_46;
			t__T[] f_tmp_47 = __tmp41[i];
			t__T[] __tmp42 = f_tmp_47;
			Point<t__T> f_tmp_48 = val2.first;
			Point<t__T> __tmp43 = f_tmp_48;
			t__T[][] f_tmp_49 = __tmp43.cor;
			t__T[][] __tmp44 = f_tmp_49;
			t__T[] f_tmp_50 = __tmp44[i];
			t__T[] __tmp45 = f_tmp_50;
			t__T[] f_tmp_51 = intLib.add(__tmp42,__tmp45);
			t__T[] __tmp46 = f_tmp_51;
			t__T[][] f_tmp_52 = added.cor;
			t__T[][] __tmp47 = f_tmp_52;
			__tmp47[i]=__tmp46;
			int f_tmp_53 = 1;
			int __tmp48 = f_tmp_53;
			int f_tmp_54 = i + __tmp48;
			int __tmp49 = f_tmp_54;
			i = __tmp49;
			int f_tmp_55 = 2;
			__tmp38 = f_tmp_55;
			boolean f_tmp_56 = i < __tmp38;
			__tmp39 = f_tmp_56;
		}
		Int<t__T> f_tmp_57 = val1.second;
		Int<t__T> __tmp50 = f_tmp_57;
		t__T[] f_tmp_58 = __tmp50.data;
		t__T[] __tmp51 = f_tmp_58;
		Int<t__T> f_tmp_59 = val2.second;
		Int<t__T> __tmp52 = f_tmp_59;
		t__T[] f_tmp_60 = __tmp52.data;
		t__T[] __tmp53 = f_tmp_60;
		t__T[] f_tmp_61 = intLib.add(__tmp51,__tmp53);
		t__T[] __tmp54 = f_tmp_61;
		Int<t__T> f_tmp_62 = new Int<t__T>(env);
		f_tmp_62.data = __tmp54;
		Int<t__T> __tmp55 = f_tmp_62;
		Pair<t__T, Point<t__T>, Int<t__T>> f_tmp_63 = new Pair<t__T, Point<t__T>, Int<t__T>>(env, new Point<t__T>(env, new t__T[2][]), new Int<t__T>(env), new Point<t__T>(env, new t__T[2][]));
		f_tmp_63.first = added;
		f_tmp_63.second = __tmp55;
		Pair<t__T, Point<t__T>, Int<t__T>> __tmp56 = f_tmp_63;
		return __tmp56;

	}
}
