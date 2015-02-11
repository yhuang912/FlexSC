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
public class SecureStorage<t__T, T extends IWritable<T,t__T>> {
	public CircuitOram<t__T> oram;

	public CompEnv<t__T> env;
	public IntegerLib<t__T> intLib;
	public FloatLib<t__T> floatLib;
	private T factoryT;
	private int m;

	public SecureStorage(CompEnv<t__T> env, int m, T factoryT, CircuitOram<t__T> oram) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<t__T>(env);
		this.floatLib = new FloatLib<t__T>(env, 24, 8);
		this.m = m;
		this.factoryT = factoryT;
		this.oram = oram;
	}

	public void add(Pointer<t__T> p, T data, t__T __isPhantom) throws Exception {
		p = new Pointer<t__T>(env, m).newObj(intLib.mux(p.getBits(), new Pointer<t__T>(env, m), __isPhantom));
		data = this.factoryT.newObj(intLib.mux(data.getBits(), factoryT.newObj(null), __isPhantom));
		t__T[] f_tmp_0 = p.index;
		t__T[] __tmp0 = f_tmp_0;
		t__T[] f_tmp_1 = p.pos;
		t__T[] __tmp1 = f_tmp_1;
		boolean f_tmp_2 = true;
		t__T __tmp3 = env.inputOfAlice(f_tmp_2);
		CircuitOram<t__T> f_tmp_3 = this.oram;
		CircuitOram<t__T> __tmp2 = f_tmp_3;
		__tmp2.conditionalPutBack(__tmp0, __tmp1, data.getBits(), __tmp3);

	}
	public T remove(Pointer<t__T> p, t__T __isPhantom) throws Exception {
		p = new Pointer<t__T>(env, m).newObj(intLib.mux(p.getBits(), new Pointer<t__T>(env, m), __isPhantom));
		t__T[] f_tmp_5 = p.index;
		t__T[] __tmp5 = f_tmp_5;
		t__T[] f_tmp_6 = p.pos;
		t__T[] __tmp6 = f_tmp_6;
		boolean f_tmp_7 = true;
		t__T __tmp8 = env.inputOfAlice(f_tmp_7);
		CircuitOram<t__T> f_tmp_8 = this.oram;
		CircuitOram<t__T> __tmp7 = f_tmp_8;
		T f_tmp_9 = factoryT.newObj(__tmp7.conditionalReadAndRemove(__tmp5, __tmp6, __tmp8));
		T __tmp9 = f_tmp_9;
		return __tmp9;

	}
}
