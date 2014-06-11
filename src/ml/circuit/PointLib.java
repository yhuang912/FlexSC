package ml.circuit;

import ml.datastructure.Point;
import circuits.IntegerLib;
import flexsc.CompEnv;

public class PointLib<T> extends IntegerLib<T> {
	int dimension;
	int width;
	public PointLib(CompEnv<T> e, int dimension, int width) {
		super(e);
		this.dimension = dimension;
		this.width = width;
	}

	public Point<T> add(Point<T> a, Point<T> b) throws Exception {
		return addInternal(a, b, false /* cin */);
	}

	private Point<T> addInternal(Point<T> a, Point<T> b, boolean cin) throws Exception {
		assertPointCompatibility(a, b);

		Point<T> result = new Point<>(env, dimension, false);
		for(int i = 0; i < dimension; ++i)
			result.coordinates[i] = add(a.coordinates[i], b.coordinates[i], cin);
		
		return result;
	}

	private void assertPointCompatibility(Point<T> a, Point<T> b) {
		assert(a.getDimension() == dimension && a.width() == width 
				&& b.getDimension() == dimension && b.width() == width): "bad input in point add";
	}

	public Point<T> not(Point<T> a) {
		Point<T> result = new Point<>(env, dimension, false);
		for (int i = 0; i < dimension; i++) {
			result.coordinates[i] = not(a.coordinates[i]);
		}
		return result;
	}

	public Point<T> mux(Point<T> a, Point<T> b, T c) throws Exception {
		Point<T> res = new Point<>(env, dimension, false);
		for (int i = 0; i < dimension; i++) {
			res.coordinates[i] = mux(a.coordinates[i], b.coordinates[i], c);
		}
		res.isDummy = mux(a.isDummy, b.isDummy, c);
		return res;
	}

	public Point<T> subtract(Point<T>a, Point<T> b) throws Exception {
		return addInternal(a, not(b), true /* cin */);
	}
	
	public T[] innerProduct(Point<T> a, Point<T> b) throws Exception {
		assertPointCompatibility(a, b);
		T[] result = zeros(width * 2); // This should depend on the dimension as well?
		for (int i = 0; i < dimension; i++) {
			result = add(result, multiplyFull(a.coordinates[i], b.coordinates[i]));
		}
		return result;
	}
	
	public Point<T> multiply(Point<T> a, Point<T> b) throws Exception {
		assertPointCompatibility(a, b);
		Point<T> res = new Point<>(env, dimension, false);
		for (int i = 0; i < dimension; i++) {
			res.coordinates[i] = multiply(a.coordinates[i], b.coordinates[i]);
		}
		res.isDummy = SIGNAL_ZERO;
		return res;
	}
	
	/*public T[] L1Distance(Point<T> a, Point<T> b){
		
	}*/

	public T[] L2Distance(Point<T> a, Point<T> b) throws Exception{
		Point<T> diff = subtract(a, b);
		Point<T> multiply = multiply(diff, diff);
		
		T[] result = toSignals(0, a.width());
		for(int i = 0; i < dimension; ++i)
			result = add(result, multiply.coordinates[i]);
		T[] max = env.newTArray(a.width());
		for(int i = 0; i < max.length-1; ++i)
			max[i] = SIGNAL_ONE;
		max[max.length-1] = SIGNAL_ZERO;
		return mux(result, max, or(a.isDummy, b.isDummy));
	}
	
	//TODO(more to add if needed.)
}
