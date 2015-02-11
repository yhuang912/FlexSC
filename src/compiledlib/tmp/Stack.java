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
public class Stack<t__T, T extends IWritable<T,t__T>> {
	public Pointer<t__T> root;
	public SecureStorage<t__T, StackNode<t__T, T>> store;

	public CompEnv<t__T> env;
	public IntegerLib<t__T> intLib;
	public FloatLib<t__T> floatLib;
	private T factoryT;
	private int m;

	public Stack(CompEnv<t__T> env, int m, T factoryT, SecureStorage<t__T, StackNode<t__T, T>> store) throws Exception {
		this.env = env;
		this.intLib = new IntegerLib<t__T>(env);
		this.floatLib = new FloatLib<t__T>(env, 24, 8);
		this.m = m;
		this.factoryT = factoryT;
		this.root = new Pointer<t__T>(env, m);
		this.store = store;
	}

	public void push(T operand, t__T __isPhantom) throws Exception {
		operand = this.factoryT.newObj(intLib.mux(operand.getBits(), factoryT.newObj(null), __isPhantom));
		StackNode<t__T, T> node = new StackNode<t__T, T>(env, m, factoryT);
		Pointer<t__T> f_tmp_10 = this.root;
		Pointer<t__T> __tmp10 = f_tmp_10;
		StackNode<t__T, T> f_tmp_11 = new StackNode<t__T, T>(env, m, factoryT);
		f_tmp_11.next = __tmp10;
		f_tmp_11.data = operand;
		StackNode<t__T, T> __tmp11 = f_tmp_11;
		StackNode<t__T, T> __tmp12 = new StackNode<t__T, T>(env, m, factoryT).newObj(intLib.mux(node.getBits(), __tmp11.getBits(),__isPhantom));
		node = __tmp12;

	}
	public void pop(t__T __isPhantom) throws Exception {
		Pointer<t__T> a = new Pointer<t__T>(env, m);
		Pointer<t__T> f_tmp_13 = this.root;
		Pointer<t__T> __tmp13 = f_tmp_13;
		Pointer<t__T> __tmp14 = new Pointer<t__T>(env, m).newObj(intLib.mux(a.getBits(), __tmp13.getBits(),__isPhantom));
		a = __tmp14;

	}
}
