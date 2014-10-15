package compiledlib.sketch;
import oram.SecureArray;
import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
public class count_min_sketch {
	public SecureArray<Boolean>[] sketch;
	public Boolean[][][] hash_seed;

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;

	public count_min_sketch(CompEnv<Boolean> env, IntegerLib<Boolean> lib, SecureArray<Boolean>[] sketch, Boolean[][][] hash_seed) throws Exception {
		this.env = env;
		this.lib = lib;
		this.sketch = sketch;
		this.hash_seed = hash_seed;
	}

	public void init() throws Exception {
		int i = 0;
		int j = 0;
		int f_tmp_0 = 0;
		int __tmp0 = f_tmp_0;
		i = __tmp0;
		int f_tmp_1 = 10;
		int __tmp1 = f_tmp_1;
		boolean f_tmp_2 = i < __tmp1;
		boolean __tmp2 = f_tmp_2;
		while(__tmp2) {
			int f_tmp_3 = 0;
			int __tmp3 = f_tmp_3;
			j = __tmp3;
			int f_tmp_4 = 2;
			int __tmp4 = f_tmp_4;
			boolean f_tmp_5 = j < __tmp4;
			boolean __tmp5 = f_tmp_5;
			while(__tmp5) {
				int f_tmp_6 = 64;
				int __tmp6 = f_tmp_6;
				Boolean[] f_tmp_7 = lib.randBools(__tmp6);
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
				int f_tmp_12 = 2;
				__tmp4 = f_tmp_12;
				boolean f_tmp_13 = j < __tmp4;
				__tmp5 = f_tmp_13;
			}
			int f_tmp_14 = 1;
			int __tmp12 = f_tmp_14;
			int f_tmp_15 = i + __tmp12;
			int __tmp13 = f_tmp_15;
			i = __tmp13;
			int f_tmp_16 = 10;
			__tmp1 = f_tmp_16;
			boolean f_tmp_17 = i < __tmp1;
			__tmp2 = f_tmp_17;
		}

	}
	public Boolean[] fast_mod(Boolean[] v) throws Exception {
		Boolean[] f_tmp_18 = lib.rightPublicShift(v, 31);
		Boolean[] __tmp14 = f_tmp_18;
		Boolean[] f_tmp_19 = lib.add(__tmp14,v);
		Boolean[] __tmp15 = f_tmp_19;
		int f_tmp_20 = 2147483647;
		int __tmp16 = f_tmp_20;
		Boolean[] f_tmp_22 = env.inputOfAlice(Utils.fromInt(__tmp16, 64));
		Boolean[] f_tmp_21 = lib.and(__tmp15,f_tmp_22);
		Boolean[] __tmp17 = f_tmp_21;
		return __tmp17;
	}
	public Boolean[] hash(int row_number, Boolean[] element) throws Exception {
		Boolean[] h = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[][][] f_tmp_23 = this.hash_seed;
		Boolean[][][] __tmp18 = f_tmp_23;
		Boolean[][] f_tmp_24 = __tmp18[row_number];
		Boolean[][] __tmp19 = f_tmp_24;
		int f_tmp_25 = 0;
		int __tmp20 = f_tmp_25;
		Boolean[] f_tmp_26 = __tmp19[__tmp20];
		Boolean[] __tmp21 = f_tmp_26;
		Boolean[] f_tmp_27 = lib.multiply(__tmp21,element);
		Boolean[] __tmp22 = f_tmp_27;
		Boolean[][][] f_tmp_28 = this.hash_seed;
		Boolean[][][] __tmp23 = f_tmp_28;
		Boolean[][] f_tmp_29 = __tmp23[row_number];
		Boolean[][] __tmp24 = f_tmp_29;
		int f_tmp_30 = 1;
		int __tmp25 = f_tmp_30;
		Boolean[] f_tmp_31 = __tmp24[__tmp25];
		Boolean[] __tmp26 = f_tmp_31;
		Boolean[] f_tmp_32 = lib.add(__tmp22,__tmp26);
		Boolean[] __tmp27 = f_tmp_32;
		h = __tmp27;
		Boolean[] f_tmp_33 = this.fast_mod(h);
		Boolean[] __tmp28 = f_tmp_33;
		int f_tmp_34 = 1000;
		int __tmp29 = f_tmp_34;
		Boolean[] f_tmp_36 = env.inputOfAlice(Utils.fromInt(__tmp29, 64));
		Boolean[] f_tmp_35 = lib.mod(__tmp28,f_tmp_36);
		Boolean[] __tmp30 = f_tmp_35;
		return __tmp30;
	}
	public void insert(Boolean[] element, Boolean[] frequency) throws Exception {
		int i = 0;
		Boolean[] pos = env.inputOfAlice(Utils.fromInt(0, 64));
		int f_tmp_37 = 0;
		int __tmp31 = f_tmp_37;
		i = __tmp31;
		int f_tmp_38 = 10;
		int __tmp32 = f_tmp_38;
		boolean f_tmp_39 = i < __tmp32;
		boolean __tmp33 = f_tmp_39;
		while(__tmp33) {
			Boolean[] f_tmp_40 = this.hash(i, element);
			Boolean[] __tmp34 = f_tmp_40;
			pos = __tmp34;
			SecureArray<Boolean>[] f_tmp_41 = this.sketch;
			SecureArray<Boolean>[] __tmp35 = f_tmp_41;
			SecureArray<Boolean> f_tmp_42 = __tmp35[i];
			SecureArray<Boolean> __tmp36 = f_tmp_42;
			Boolean[] f_tmp_43 = __tmp36.read(pos);
			Boolean[] __tmp37 = f_tmp_43;
			Boolean[] f_tmp_44 = lib.add(__tmp37,frequency);
			Boolean[] __tmp38 = f_tmp_44;
			SecureArray<Boolean>[] f_tmp_45 = this.sketch;
			SecureArray<Boolean>[] __tmp39 = f_tmp_45;
			SecureArray<Boolean> f_tmp_46 = __tmp39[i];
			SecureArray<Boolean> __tmp40 = f_tmp_46;
			__tmp40.write(pos,__tmp38);
			int f_tmp_47 = 1;
			int __tmp41 = f_tmp_47;
			int f_tmp_48 = i + __tmp41;
			int __tmp42 = f_tmp_48;
			i = __tmp42;
			int f_tmp_49 = 10;
			__tmp32 = f_tmp_49;
			boolean f_tmp_50 = i < __tmp32;
			__tmp33 = f_tmp_50;
		}

	}
	public Boolean[] query(Boolean[] element) throws Exception {
		Boolean[] minimum = env.inputOfAlice(Utils.fromInt(0, 64));
		int i = 0;
		Boolean[] pos = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[] s = env.inputOfAlice(Utils.fromInt(0, 64));
		int f_tmp_51 = 1;
		int __tmp43 = f_tmp_51;
		int f_tmp_52 = __tmp43 << 31;
		int __tmp44 = f_tmp_52;
		minimum = env.inputOfAlice(Utils.fromInt(__tmp44, 64));
		int f_tmp_53 = 0;
		int __tmp45 = f_tmp_53;
		i = __tmp45;
		int f_tmp_54 = 10;
		int __tmp46 = f_tmp_54;
		boolean f_tmp_55 = i < __tmp46;
		boolean __tmp47 = f_tmp_55;
		while(__tmp47) {
			Boolean[] f_tmp_56 = this.hash(i, element);
			Boolean[] __tmp48 = f_tmp_56;
			pos = __tmp48;
			SecureArray<Boolean>[] f_tmp_57 = this.sketch;
			SecureArray<Boolean>[] __tmp49 = f_tmp_57;
			SecureArray<Boolean> f_tmp_58 = __tmp49[i];
			SecureArray<Boolean> __tmp50 = f_tmp_58;
			Boolean[] f_tmp_59 = __tmp50.read(pos);
			Boolean[] __tmp51 = f_tmp_59;
			s = __tmp51;
			Boolean f_tmp_60 = lib.not(lib.geq(s, minimum));
			Boolean __tmp52 = f_tmp_60;
			Boolean[] f_tmp_61 = lib.mux(minimum, s,__tmp52);
			Boolean[] __tmp53 = f_tmp_61;
			minimum = __tmp53;
			Boolean f_tmp_62 = lib.not(__tmp52);
			Boolean __tmp54 = f_tmp_62;
			int f_tmp_63 = 1;
			int __tmp55 = f_tmp_63;
			int f_tmp_64 = i + __tmp55;
			int __tmp56 = f_tmp_64;
			i = __tmp56;
			int f_tmp_65 = 10;
			__tmp46 = f_tmp_65;
			boolean f_tmp_66 = i < __tmp46;
			__tmp47 = f_tmp_66;
		}
		return minimum;
	}
}
