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

	public Point<T> add(Point<T> a, Point<T> b) throws Exception{
		assert(a.dimension() == dimension && a.width() == width 
				&& b.dimension() == dimension && b.width() == width): "bad input in point add";
		
		Point<T> result = new Point<>(env, dimension);
		for(int i = 0; i < dimension; ++i)
			result.coordinates[i] = add(a.coordinates[i], b.coordinates[i]);
		return result;
	}
	
	public Point<T> subtract(Point<T>a, Point<T> b){
		
	}
	
	public T[] innerProduct(Point<T> a, Point<T> b){
		
	}
	
	public T[] L1Distance(Point<T> a, Point<T> b){
		
	}

	public T[] L2Distance(Point<T> a, Point<T> b){
		
	}
	
	//TODO(more to add if needed.)
}
