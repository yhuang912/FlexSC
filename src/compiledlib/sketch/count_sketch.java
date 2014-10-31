package compiledlib.sketch;
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
public class count_sketch {
	public Boolean[][][] hash_seed;
	public SecureArray<Boolean>[] sketch;

	public CompEnv<Boolean> env;
	public IntegerLib<Boolean> intLib;
	public FloatLib<Boolean> floatLib;

	public count_sketch(CompEnv<Boolean> env, IntegerLib<Boolean> intLib, FloatLib<Boolean> floatLib, Boolean[][][] hash_seed, SecureArray<Boolean>[] sketch) throws Exception {
		this.env = env;
		this.intLib = intLib;
		this.floatLib = floatLib;
		this.hash_seed = hash_seed;
		this.sketch = sketch;
	}

	public void init() throws Exception {
		int i = 0;
		int j = 0;
		int f_tmp_0 = 0;
		int __tmp0 = f_tmp_0;
		i = __tmp0;
		int f_tmp_1 = 5;
		int __tmp1 = f_tmp_1;
		boolean f_tmp_2 = i < __tmp1;
		boolean __tmp2 = f_tmp_2;
		while(__tmp2) {
			int f_tmp_3 = 0;
			int __tmp3 = f_tmp_3;
			j = __tmp3;
			int f_tmp_4 = 4;
			int __tmp4 = f_tmp_4;
			boolean f_tmp_5 = j < __tmp4;
			boolean __tmp5 = f_tmp_5;
			while(__tmp5) {
				int f_tmp_6 = 64;
				int __tmp6 = f_tmp_6;
				Boolean[] f_tmp_7 = intLib.randBools(__tmp6);
				Boolean[] __tmp7 = f_tmp_7;
				Boolean[][][] f_tmp_8 = this.hash_seed;
				Boolean[][][] __tmp8 = f_tmp_8;
				Boolean[][] f_tmp_9 = __tmp8[i];
				Boolean[][] __tmp9 = f_tmp_9;
				__tmp9[j]=__tmp7;
				int f_tmp_10 = 1;
				int __tmp10 = f_tmp_10;
				int f_tmp_11 = j + __tmp10;
				int __tmp11 = f_tmp_11;
				j = __tmp11;
				int f_tmp_12 = 4;
				__tmp4 = f_tmp_12;
				boolean f_tmp_13 = j < __tmp4;
				__tmp5 = f_tmp_13;
			}
			int f_tmp_14 = 1;
			int __tmp12 = f_tmp_14;
			int f_tmp_15 = i + __tmp12;
			int __tmp13 = f_tmp_15;
			i = __tmp13;
			int f_tmp_16 = 5;
			__tmp1 = f_tmp_16;
			boolean f_tmp_17 = i < __tmp1;
			__tmp2 = f_tmp_17;
		}

	}
	public Boolean[] fast_mod(Boolean[] v) throws Exception {
		int f_tmp_18 = 31;
		int __tmp14 = f_tmp_18;
		Boolean[] f_tmp_19 = intLib.rightPublicShift(v, __tmp14);
		Boolean[] __tmp15 = f_tmp_19;
		Boolean[] f_tmp_20 = intLib.add(__tmp15,v);
		Boolean[] __tmp16 = f_tmp_20;
		int f_tmp_21 = 2147483647;
		int __tmp17 = f_tmp_21;
		Boolean[] f_tmp_23 = env.inputOfAlice(Utils.fromInt(__tmp17, 64));
		Boolean[] f_tmp_22 = intLib.and(__tmp16,f_tmp_23);
		Boolean[] __tmp18 = f_tmp_22;
		return __tmp18;
	}
	public Boolean[] hash(int row_number, Boolean[] element) throws Exception {
		Boolean[] h = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[][][] f_tmp_24 = this.hash_seed;
		Boolean[][][] __tmp19 = f_tmp_24;
		int f_tmp_25 = 0;
		int __tmp20 = f_tmp_25;
		Boolean[][] f_tmp_26 = __tmp19[__tmp20];
		Boolean[][] __tmp21 = f_tmp_26;
		int f_tmp_27 = 0;
		int __tmp22 = f_tmp_27;
		Boolean[] f_tmp_28 = __tmp21[__tmp22];
		Boolean[] __tmp23 = f_tmp_28;
		Boolean[][][] f_tmp_29 = this.hash_seed;
		Boolean[][][] __tmp24 = f_tmp_29;
		Boolean[][] f_tmp_30 = __tmp24[row_number];
		Boolean[][] __tmp25 = f_tmp_30;
		int f_tmp_31 = 1;
		int __tmp26 = f_tmp_31;
		Boolean[] f_tmp_32 = __tmp25[__tmp26];
		Boolean[] __tmp27 = f_tmp_32;
		Boolean[] f_tmp_33 = intLib.add(__tmp23,__tmp27);
		Boolean[] __tmp28 = f_tmp_33;
		h = __tmp28;
		Boolean[] f_tmp_34 = this.fast_mod(h);
		Boolean[] __tmp29 = f_tmp_34;
		int f_tmp_35 = 5000;
		int __tmp30 = f_tmp_35;
		Boolean[] f_tmp_37 = env.inputOfAlice(Utils.fromInt(__tmp30, 64));
		Boolean[] f_tmp_36 = intLib.mod(__tmp29,f_tmp_37);
		Boolean[] __tmp31 = f_tmp_36;
		return __tmp31;
	}
	public Boolean[] hash2(int row_number, Boolean[] element) throws Exception {
		Boolean[] h = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[][][] f_tmp_38 = this.hash_seed;
		Boolean[][][] __tmp32 = f_tmp_38;
		int f_tmp_39 = 0;
		int __tmp33 = f_tmp_39;
		Boolean[][] f_tmp_40 = __tmp32[__tmp33];
		Boolean[][] __tmp34 = f_tmp_40;
		int f_tmp_41 = 2;
		int __tmp35 = f_tmp_41;
		Boolean[] f_tmp_42 = __tmp34[__tmp35];
		Boolean[] __tmp36 = f_tmp_42;
		Boolean[][][] f_tmp_43 = this.hash_seed;
		Boolean[][][] __tmp37 = f_tmp_43;
		Boolean[][] f_tmp_44 = __tmp37[row_number];
		Boolean[][] __tmp38 = f_tmp_44;
		int f_tmp_45 = 3;
		int __tmp39 = f_tmp_45;
		Boolean[] f_tmp_46 = __tmp38[__tmp39];
		Boolean[] __tmp40 = f_tmp_46;
		Boolean[] f_tmp_47 = intLib.add(__tmp36,__tmp40);
		Boolean[] __tmp41 = f_tmp_47;
		h = __tmp41;
		Boolean[] f_tmp_48 = this.fast_mod(h);
		Boolean[] __tmp42 = f_tmp_48;
		int f_tmp_49 = 1;
		int __tmp43 = f_tmp_49;
		Boolean[] f_tmp_51 = env.inputOfAlice(Utils.fromInt(__tmp43, 64));
		Boolean[] f_tmp_50 = intLib.and(__tmp42,f_tmp_51);
		Boolean[] __tmp44 = f_tmp_50;
		return __tmp44;
	}
	public void insert(Boolean[] element, Boolean[] frequency) throws Exception {
		int i = 0;
		Boolean[] pos = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[] g = env.inputOfAlice(Utils.fromInt(0, 64));
		int f_tmp_52 = 0;
		int __tmp45 = f_tmp_52;
		i = __tmp45;
		int f_tmp_53 = 5;
		int __tmp46 = f_tmp_53;
		boolean f_tmp_54 = i < __tmp46;
		boolean __tmp47 = f_tmp_54;
		while(__tmp47) {
			Boolean[] f_tmp_55 = this.hash(i, element);
			Boolean[] __tmp48 = f_tmp_55;
			pos = __tmp48;
			Boolean[] f_tmp_56 = this.hash2(i, element);
			Boolean[] __tmp49 = f_tmp_56;
			g = __tmp49;
			int f_tmp_57 = 0;
			int __tmp50 = f_tmp_57;
			Boolean[] f_tmp_59 = env.inputOfAlice(Utils.fromInt(__tmp50, 64));
			Boolean f_tmp_58 = intLib.eq(g, f_tmp_59);
			Boolean __tmp51 = f_tmp_58;
			SecureArray<Boolean>[] f_tmp_60 = this.sketch;
			SecureArray<Boolean>[] __tmp52 = f_tmp_60;
			SecureArray<Boolean> f_tmp_61 = __tmp52[i];
			SecureArray<Boolean> __tmp53 = f_tmp_61;
			Boolean[] f_tmp_62 = __tmp53.read(pos);
			Boolean[] __tmp54 = f_tmp_62;
			Boolean[] f_tmp_63 = intLib.add(__tmp54,frequency);
			Boolean[] __tmp55 = f_tmp_63;
			SecureArray<Boolean>[] f_tmp_64 = this.sketch;
			SecureArray<Boolean>[] __tmp56 = f_tmp_64;
			SecureArray<Boolean> f_tmp_65 = __tmp56[i];
			SecureArray<Boolean> __tmp57 = f_tmp_65;
			Boolean[] f_tmp_66 = __tmp57.read(pos);
			Boolean[] __tmp58 = f_tmp_66;
			Boolean[] f_tmp_67 = intLib.mux(__tmp58, __tmp55,__tmp51);
			Boolean[] __tmp59 = f_tmp_67;
			__tmp57.write(pos,__tmp59);
			Boolean f_tmp_68 = intLib.not(__tmp51);
			Boolean __tmp60 = f_tmp_68;
			SecureArray<Boolean>[] f_tmp_69 = this.sketch;
			SecureArray<Boolean>[] __tmp61 = f_tmp_69;
			SecureArray<Boolean> f_tmp_70 = __tmp61[i];
			SecureArray<Boolean> __tmp62 = f_tmp_70;
			Boolean[] f_tmp_71 = __tmp62.read(pos);
			Boolean[] __tmp63 = f_tmp_71;
			Boolean[] f_tmp_72 = intLib.sub(__tmp63,frequency);
			Boolean[] __tmp64 = f_tmp_72;
			SecureArray<Boolean>[] f_tmp_73 = this.sketch;
			SecureArray<Boolean>[] __tmp65 = f_tmp_73;
			SecureArray<Boolean> f_tmp_74 = __tmp65[i];
			SecureArray<Boolean> __tmp66 = f_tmp_74;
			Boolean[] f_tmp_75 = __tmp66.read(pos);
			Boolean[] __tmp67 = f_tmp_75;
			Boolean[] f_tmp_76 = intLib.mux(__tmp67, __tmp64,__tmp60);
			Boolean[] __tmp68 = f_tmp_76;
			__tmp66.write(pos,__tmp68);
			int f_tmp_77 = 1;
			int __tmp69 = f_tmp_77;
			int f_tmp_78 = i + __tmp69;
			int __tmp70 = f_tmp_78;
			i = __tmp70;
			int f_tmp_79 = 5;
			__tmp46 = f_tmp_79;
			boolean f_tmp_80 = i < __tmp46;
			__tmp47 = f_tmp_80;
		}

	}
	public Boolean[] query(Boolean[] element) throws Exception {
		return element;
	}
}
