package compiledlib.sketch;
import oram.SecureArray;
import util.Utils;
import circuits.IntegerLib;
import flexsc.CompEnv;
public class count_sketch {
	public SecureArray<Boolean>[] sketch;
	public Boolean[][][] hash_seed;

	private CompEnv<Boolean> env;
	private IntegerLib<Boolean> lib;

	public count_sketch(CompEnv<Boolean> env, IntegerLib<Boolean> lib, SecureArray<Boolean>[] sketch, Boolean[][][] hash_seed) throws Exception {
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
			int f_tmp_4 = 4;
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
		int f_tmp_24 = 0;
		int __tmp19 = f_tmp_24;
		Boolean[][] f_tmp_25 = __tmp18[__tmp19];
		Boolean[][] __tmp20 = f_tmp_25;
		int f_tmp_26 = 0;
		int __tmp21 = f_tmp_26;
		Boolean[] f_tmp_27 = __tmp20[__tmp21];
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
	public Boolean[] hash2(int row_number, Boolean[] element) throws Exception {
		Boolean[] h = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[][][] f_tmp_37 = this.hash_seed;
		Boolean[][][] __tmp31 = f_tmp_37;
		int f_tmp_38 = 0;
		int __tmp32 = f_tmp_38;
		Boolean[][] f_tmp_39 = __tmp31[__tmp32];
		Boolean[][] __tmp33 = f_tmp_39;
		int f_tmp_40 = 2;
		int __tmp34 = f_tmp_40;
		Boolean[] f_tmp_41 = __tmp33[__tmp34];
		Boolean[] __tmp35 = f_tmp_41;
		Boolean[][][] f_tmp_42 = this.hash_seed;
		Boolean[][][] __tmp36 = f_tmp_42;
		Boolean[][] f_tmp_43 = __tmp36[row_number];
		Boolean[][] __tmp37 = f_tmp_43;
		int f_tmp_44 = 3;
		int __tmp38 = f_tmp_44;
		Boolean[] f_tmp_45 = __tmp37[__tmp38];
		Boolean[] __tmp39 = f_tmp_45;
		Boolean[] f_tmp_46 = lib.add(__tmp35,__tmp39);
		Boolean[] __tmp40 = f_tmp_46;
		h = __tmp40;
		Boolean[] f_tmp_47 = this.fast_mod(h);
		Boolean[] __tmp41 = f_tmp_47;
		int f_tmp_48 = 1;
		int __tmp42 = f_tmp_48;
		Boolean[] f_tmp_50 = env.inputOfAlice(Utils.fromInt(__tmp42, 64));
		Boolean[] f_tmp_49 = lib.and(__tmp41,f_tmp_50);
		Boolean[] __tmp43 = f_tmp_49;
		return __tmp43;
	}
	public void insert(Boolean[] element, Boolean[] frequency) throws Exception {
		int i = 0;
		Boolean[] pos = env.inputOfAlice(Utils.fromInt(0, 64));
		Boolean[] g = env.inputOfAlice(Utils.fromInt(0, 64));
		int f_tmp_51 = 0;
		int __tmp44 = f_tmp_51;
		i = __tmp44;
		int f_tmp_52 = 10;
		int __tmp45 = f_tmp_52;
		boolean f_tmp_53 = i < __tmp45;
		boolean __tmp46 = f_tmp_53;
		while(__tmp46) {
			Boolean[] f_tmp_54 = this.hash(i, element);
			Boolean[] __tmp47 = f_tmp_54;
			pos = __tmp47;
			Boolean[] f_tmp_55 = this.hash2(i, element);
			Boolean[] __tmp48 = f_tmp_55;
			g = __tmp48;
			int f_tmp_56 = 0;
			int __tmp49 = f_tmp_56;
			Boolean[] f_tmp_58 = env.inputOfAlice(Utils.fromInt(__tmp49, 64));
			Boolean f_tmp_57 = lib.eq(g, f_tmp_58);
			Boolean __tmp50 = f_tmp_57;
			SecureArray<Boolean>[] f_tmp_59 = this.sketch;
			SecureArray<Boolean>[] __tmp51 = f_tmp_59;
			SecureArray<Boolean> f_tmp_60 = __tmp51[i];
			SecureArray<Boolean> __tmp52 = f_tmp_60;
			Boolean[] f_tmp_61 = __tmp52.read(pos);
			Boolean[] __tmp53 = f_tmp_61;
			Boolean[] f_tmp_62 = lib.add(__tmp53,frequency);
			Boolean[] __tmp54 = f_tmp_62;
			SecureArray<Boolean>[] f_tmp_63 = this.sketch;
			SecureArray<Boolean>[] __tmp55 = f_tmp_63;
			SecureArray<Boolean> f_tmp_64 = __tmp55[i];
			SecureArray<Boolean> __tmp56 = f_tmp_64;
			Boolean[] f_tmp_65 = __tmp56.read(pos);
			Boolean[] __tmp57 = f_tmp_65;
			Boolean[] f_tmp_66 = lib.mux(__tmp57, __tmp54,__tmp50);
			Boolean[] __tmp58 = f_tmp_66;
			__tmp56.write(pos,__tmp58);
			Boolean f_tmp_67 = lib.not(__tmp50);
			Boolean __tmp59 = f_tmp_67;
			SecureArray<Boolean>[] f_tmp_68 = this.sketch;
			SecureArray<Boolean>[] __tmp60 = f_tmp_68;
			SecureArray<Boolean> f_tmp_69 = __tmp60[i];
			SecureArray<Boolean> __tmp61 = f_tmp_69;
			Boolean[] f_tmp_70 = __tmp61.read(pos);
			Boolean[] __tmp62 = f_tmp_70;
			Boolean[] f_tmp_71 = lib.sub(__tmp62,frequency);
			Boolean[] __tmp63 = f_tmp_71;
			SecureArray<Boolean>[] f_tmp_72 = this.sketch;
			SecureArray<Boolean>[] __tmp64 = f_tmp_72;
			SecureArray<Boolean> f_tmp_73 = __tmp64[i];
			SecureArray<Boolean> __tmp65 = f_tmp_73;
			Boolean[] f_tmp_74 = __tmp65.read(pos);
			Boolean[] __tmp66 = f_tmp_74;
			Boolean[] f_tmp_75 = lib.mux(__tmp66, __tmp63,__tmp59);
			Boolean[] __tmp67 = f_tmp_75;
			__tmp65.write(pos,__tmp67);
			int f_tmp_76 = 1;
			int __tmp68 = f_tmp_76;
			int f_tmp_77 = i + __tmp68;
			int __tmp69 = f_tmp_77;
			i = __tmp69;
			int f_tmp_78 = 10;
			__tmp45 = f_tmp_78;
			boolean f_tmp_79 = i < __tmp45;
			__tmp46 = f_tmp_79;
		}

	}
	public Boolean[] query(Boolean[] element) throws Exception {
		return element;
	}
}
