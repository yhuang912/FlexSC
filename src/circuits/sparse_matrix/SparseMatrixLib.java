package circuits.sparse_matrix;

import java.util.Arrays;

import util.ComparatorTransformer;
import util.IObjectComparator;
import circuits.SortLib;
import circuits.arithmetic.ArithmeticLib;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;

public class SparseMatrixLib<T> {
	ArithmeticLib<T> lib;
	CompEnv<T> env;
	IntegerLib<T> integerlib;
	SortLib<T> sortlib;
	
	protected class SortByDummyThenYThenX implements IObjectComparator<MatrixNode<T>, T> {
		@Override
		public T compare(MatrixNode<T> v1, MatrixNode<T> v2) throws Exception {
			T[] mergeIndex1 = v1.ilib.zeros(v1.x.length + v1.y.length + 2);
			System.arraycopy(v1.x, 0, mergeIndex1, 0, v1.x.length);
			System.arraycopy(v1.y, 0, mergeIndex1, v1.x.length, v1.y.length);
			mergeIndex1[mergeIndex1.length-2] = v1.isDummy;

			T[] mergeIndex2 = v1.ilib.zeros(v2.x.length + v2.y.length + 2);
			System.arraycopy(v2.x, 0, mergeIndex2, 0, v2.x.length);
			System.arraycopy(v2.y, 0, mergeIndex2, v2.x.length, v2.y.length);
			mergeIndex2[mergeIndex2.length-2] = v2.isDummy;

			return v1.ilib.geq(mergeIndex1, mergeIndex2);
		}
	}
	
	protected class SortByDummyThenXThenY implements IObjectComparator<MatrixNode<T>, T> {
		@Override
		public T compare(MatrixNode<T> v1, MatrixNode<T> v2) throws Exception {
			T[] mergeIndex1 = v1.ilib.zeros(v1.x.length + v1.y.length + 2);
			System.arraycopy(v1.y, 0, mergeIndex1, 0, v1.y.length);
			System.arraycopy(v1.x, 0, mergeIndex1, v1.y.length, v1.x.length);
			mergeIndex1[mergeIndex1.length-2] = v1.isDummy;

			T[] mergeIndex2 = v1.ilib.zeros(v2.x.length + v2.y.length + 2);
			System.arraycopy(v2.y, 0, mergeIndex2, 0, v2.y.length);
			System.arraycopy(v2.x, 0, mergeIndex2, v2.y.length, v2.x.length);
			mergeIndex2[mergeIndex2.length-2] = v2.isDummy;

			return v1.ilib.geq(mergeIndex1, mergeIndex2);
		}		
	}
	
	public MatrixNode<T> inputOfAlice(int x, int y, double c, boolean dummy) {
		MatrixNode<T> res = new MatrixNode<T>(lib, integerlib);
		res.x = integerlib.inputOfAlice(x);
		res.y = integerlib.inputOfAlice(y);
		res.value = lib.inputOfAlice(c);
		res.isDummy = dummy ? integerlib.SIGNAL_ONE : integerlib.SIGNAL_ZERO;
		return res;
	}
	
	public MatrixNode<T> inputOfBob(int x, int y, double c, boolean dummy) {
		MatrixNode<T> res = new MatrixNode<T>(lib, integerlib);
		res.x = integerlib.inputOfBob(x);
		res.y = integerlib.inputOfBob(y);
		res.value = lib.inputOfBob(c);
		res.isDummy = dummy ? integerlib.SIGNAL_ONE : integerlib.SIGNAL_ZERO;
		return res;
	}
	
	public double[] outputToAlice(MatrixNode<T> node) {
		double[] a = new double[4];
		a[0] = integerlib.outputToAlice(node.x);
		a[1] = integerlib.outputToAlice(node.y);
		a[2] = lib.outputToAlice(node.value);
		boolean re = env.outputToAlice(node.isDummy);
		if(re)
			a[3]  = 0;
		else a[3] = 1;
		return a;
	}
	
	
	public SparseMatrixLib(ArithmeticLib<T> lib) {
		this.lib = lib;
		this.env = lib.getEnv();
		integerlib = new IntegerLib<T>(env);
	}

	public MatrixNode<T>[] add(MatrixNode<T>[] m1, MatrixNode<T>[] m2) {
		MatrixNode<T>[] merged = merge(m1, m2);
		integerlib.sort(merged, integerlib.SIGNAL_ZERO, 
				new ComparatorTransformer<MatrixNode<T>, T>(m1[0], 
						new SortByDummyThenXThenY()).toComparator());
		
		for(int i = 0; i < merged.length-1; ++i) {
			T sameNode = sameIndex(merged[i], merged[i+1]);
			T[] newValue = lib.add(merged[i].value, merged[i+1].value);
			merged[i].value = integerlib.mux(merged[i].value, newValue, integerlib.and(integerlib.not(merged[i+1].isDummy), sameNode));
			merged[i+1].isDummy = integerlib.or(merged[i+1].isDummy, sameNode);
		}
		return Arrays.copyOf(merged, m1.length);
	}
	
	public MatrixNode<T>[] hadamard_multiplication(MatrixNode<T>[] m1, MatrixNode<T>[] m2) {
		MatrixNode<T>[] merged = merge(m1, m2);
		integerlib.sort(merged, integerlib.SIGNAL_ZERO, 
				new ComparatorTransformer<MatrixNode<T>, T>(m1[0], 
						new SortByDummyThenXThenY()).toComparator());
		
		for(int i = 0; i < merged.length-1; ++i) {
			T sameNode = sameIndex(merged[i], merged[i+1]);
			T[] newValue = lib.multiply(merged[i].value, merged[i+1].value);
			merged[i].value = integerlib.mux(merged[i].value, newValue, integerlib.and(integerlib.not(merged[i+1].isDummy), sameNode));
			merged[i].isDummy = integerlib.or(integerlib.or(merged[i].isDummy, merged[i+1].isDummy), integerlib.not(sameNode));
		}
		return Arrays.copyOf(merged, m1.length);
	}

	public MatrixNode<T>[] matrix_vector_multiplication(MatrixNode<T>[] m, MatrixNode<T>[] vector) {
		MatrixNode<T>[] vec = getMatrixNodeArray(5);
		for(int i = 0; i < 5; ++i) {
			vec[i] = new MatrixNode<T>(integerlib.ones(m[0].x.length), 
									   integerlib.toSignals(i, m[0].y.length), 
									   vector[i].value, integerlib.SIGNAL_ZERO, lib, integerlib);
		}
		
		MatrixNode<T>[] merged = merge(m, vec);
		integerlib.sort(merged, integerlib.SIGNAL_ZERO, 
				new ComparatorTransformer<MatrixNode<T>, T>(m[0], 
						new SortByDummyThenYThenX()).toComparator());
		T[] value = merged[merged.length-1].value;
		for(int i = merged.length-1; i > 0; --i) {
			T isNode = integerlib.eq(merged[i].x, integerlib.ones(merged[i].x.length));
			merged[i].value = integerlib.mux(merged[i].value, lib.multiply(value, merged[i].value), integerlib.not(isNode));
			value = integerlib.mux(value, merged[i].value, integerlib.and(isNode, integerlib.not(merged[i].isDummy)));
			merged[i].isDummy = integerlib.or(isNode, merged[i].isDummy);
		}
		return merged;
	}
	
	private MatrixNode<T>[] merge(MatrixNode<T>[] m1, MatrixNode<T>[] m2) {
		MatrixNode<T> [] res = getMatrixNodeArray(m1.length + m2.length);
		for(int i = 0; i < m1.length; ++i)
			res[i] = m1[i];
		for(int i = 0; i < m2.length; ++i)
			res[i + m1.length] = m2[i];
		return res;
	}
	
	public T sameIndex(MatrixNode<T> node, MatrixNode<T> node2) {
		return integerlib.and(integerlib.eq(node.x, node2.x), integerlib.eq(node.y, node2.y));
	}

	public MatrixNode<T> xor(MatrixNode<T> a, MatrixNode<T> b) {
		MatrixNode<T> res = new MatrixNode<>(a.alib, a.ilib);
		res.x = res.ilib.xor(a.x, b.x);
		res.y = res.ilib.xor(a.y, b.y);
		res.value = res.ilib.xor(a.value, b.value);
		res.isDummy = res.ilib.xor(a.isDummy, b.isDummy);
		return res;
	}
	
	public MatrixNode<T> mux(MatrixNode<T> a, MatrixNode<T> b, T c) {
		MatrixNode<T> res = new MatrixNode<>(a.alib, a.ilib);
		res.x = res.ilib.mux(a.x, b.x, c);
		res.y = res.ilib.mux(a.y, b.y, c);
		res.value = res.ilib.mux(a.value, b.value, c);
		res.isDummy = res.ilib.mux(a.isDummy, b.isDummy, c);
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public MatrixNode<T>[] getMatrixNodeArray(int length) {
		return new MatrixNode[length];
	}
}
