package compiledlib.tmp;
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
public class Matrix {
	public Boolean[][][] data;

	public CompEnv<Boolean> env;
	public IntegerLib<Boolean> intLib;
	public FloatLib<Boolean> floatLib;
	private int n;

	public Matrix(CompEnv<Boolean> env, IntegerLib<Boolean> intLib, FloatLib<Boolean> floatLib, int n, Boolean[][][] data) throws Exception {
		this.env = env;
		this.intLib = intLib;
		this.floatLib = floatLib;
		this.n = n;
		this.data = data;
	}

	public Matrix multiply(Matrix B) throws Exception {
		Matrix C = new Matrix(env, intLib, floatLib, n, new Boolean[n][][]);
		for(int _j_2 = 0; _j_2 < n; ++_j_2) {
			C.data[_j_2] = new Boolean[n][];
			for(int _j_3 = 0; _j_3 < n; ++_j_3) {
				C.data[_j_2][_j_3] = floatLib.inputOfAlice(0.0);
			}
		}
		int i = 0;
		int j = 0;
		int k = 0;
		int f_tmp_0 = 0;
		int __tmp0 = f_tmp_0;
		i = __tmp0;
		boolean f_tmp_1 = i < n;
		boolean __tmp1 = f_tmp_1;
		while(__tmp1) {
			int f_tmp_2 = 0;
			int __tmp2 = f_tmp_2;
			j = __tmp2;
			boolean f_tmp_3 = j < n;
			boolean __tmp3 = f_tmp_3;
			while(__tmp3) {
				double f_tmp_4 = 0.0;
				double __tmp4 = f_tmp_4;
				Boolean[][][] f_tmp_5 = C.data;
				Boolean[][][] __tmp5 = f_tmp_5;
				Boolean[][] f_tmp_6 = __tmp5[i];
				Boolean[][] __tmp6 = f_tmp_6;
				Boolean[] f_tmp_7 = floatLib.inputOfAlice(__tmp4);
				__tmp6[j]=f_tmp_7;
				int f_tmp_8 = 0;
				int __tmp7 = f_tmp_8;
				k = __tmp7;
				boolean f_tmp_9 = k < n;
				boolean __tmp8 = f_tmp_9;
				while(__tmp8) {
					Boolean[][][] f_tmp_10 = C.data;
					Boolean[][][] __tmp9 = f_tmp_10;
					Boolean[][] f_tmp_11 = __tmp9[i];
					Boolean[][] __tmp10 = f_tmp_11;
					Boolean[] f_tmp_12 = __tmp10[j];
					Boolean[] __tmp11 = f_tmp_12;
					Boolean[][][] f_tmp_13 = this.data;
					Boolean[][][] __tmp12 = f_tmp_13;
					Boolean[][] f_tmp_14 = __tmp12[i];
					Boolean[][] __tmp13 = f_tmp_14;
					Boolean[] f_tmp_15 = __tmp13[k];
					Boolean[] __tmp14 = f_tmp_15;
					Boolean[][][] f_tmp_16 = B.data;
					Boolean[][][] __tmp15 = f_tmp_16;
					Boolean[][] f_tmp_17 = __tmp15[k];
					Boolean[][] __tmp16 = f_tmp_17;
					Boolean[] f_tmp_18 = __tmp16[j];
					Boolean[] __tmp17 = f_tmp_18;
					Boolean[] f_tmp_19 = floatLib.multiply(__tmp14,__tmp17);
					Boolean[] __tmp18 = f_tmp_19;
					Boolean[] f_tmp_20 = floatLib.add(__tmp11,__tmp18);
					Boolean[] __tmp19 = f_tmp_20;
					Boolean[][][] f_tmp_21 = C.data;
					Boolean[][][] __tmp20 = f_tmp_21;
					Boolean[][] f_tmp_22 = __tmp20[i];
					Boolean[][] __tmp21 = f_tmp_22;
					__tmp21[j]=__tmp19;
					int f_tmp_23 = 1;
					int __tmp22 = f_tmp_23;
					int f_tmp_24 = k + __tmp22;
					int __tmp23 = f_tmp_24;
					k = __tmp23;
					boolean f_tmp_25 = k < n;
					__tmp8 = f_tmp_25;
				}
				int f_tmp_26 = 1;
				int __tmp24 = f_tmp_26;
				int f_tmp_27 = j + __tmp24;
				int __tmp25 = f_tmp_27;
				j = __tmp25;
				boolean f_tmp_28 = j < n;
				__tmp3 = f_tmp_28;
			}
			int f_tmp_29 = 1;
			int __tmp26 = f_tmp_29;
			int f_tmp_30 = i + __tmp26;
			int __tmp27 = f_tmp_30;
			i = __tmp27;
			boolean f_tmp_31 = i < n;
			__tmp1 = f_tmp_31;
		}
		return C;
	}
}
