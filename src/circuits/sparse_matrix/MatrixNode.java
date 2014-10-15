package circuits.sparse_matrix;

import java.util.Arrays;

import circuits.arithmetic.ArithmeticLib;
import circuits.arithmetic.IntegerLib;
import flexsc.IWritable;

public class MatrixNode<T> implements IWritable<MatrixNode<T>, T> {
	T[] x;
	T[] y;
	T[] value;
	T isDummy;
	ArithmeticLib<T> alib;
	IntegerLib<T> ilib;
	

	public MatrixNode(T[] x, T[] y, T[] value, T isDummy, ArithmeticLib<T> alib, IntegerLib<T> ilib){
		this.x = x;
		this.y = y;
		this.value = value;
		this.alib = alib;
		this.ilib = ilib;
		this.isDummy = isDummy;
	}
	
	public MatrixNode<T> newObject() {
		return new MatrixNode<T>(ilib.zeros(x.length), ilib.zeros(y.length),
				alib.publicValue(0.000001), ilib.SIGNAL_ONE, alib, ilib); 
	}
	
	public MatrixNode(ArithmeticLib<T> alib, IntegerLib<T> ilib) {
		this.alib = alib;
		this.ilib = ilib;
	}	
	
	public int numBits(){
		return x.length+y.length+value.length+1;
	}

	@Override
	public T[] getBits() {
		T[] res = ilib.env.newTArray(x.length+y.length+value.length+1);
		System.arraycopy(x, 0, res, 0, x.length);
		System.arraycopy(y, 0, res, x.length, y.length);
		System.arraycopy(value, 0, res, x.length+y.length, value.length);
		res[res.length-1] = isDummy;
		return res;
	}

	@Override
	public MatrixNode<T> newObj(T[] data) throws Exception {
		return new MatrixNode<T>(Arrays.copyOf(data, x.length),
				Arrays.copyOfRange(data, x.length, x.length + y.length),
				Arrays.copyOfRange(data, x.length + y.length, x.length + y.length+value.length),
				data[data.length-1], alib, ilib
				);
	}
}
