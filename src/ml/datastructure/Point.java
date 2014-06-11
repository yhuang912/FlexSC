package ml.datastructure;

import flexsc.CompEnv;

public class Point<T> {
	public T[][] coordinates;
	
	public Point(T[][] p) {
		this.coordinates = p;
	}
	
	public Point(CompEnv<T> env, int dimension, int width){
		coordinates = env.newTArray(dimension, width);
		for(int i = 0; i < coordinates.length; ++i)
			for(int j = 0; j< coordinates[i].length; ++j)
				coordinates[i][j] = env.ZERO();
	}

	// for efficiency
	public Point(CompEnv<T> env, int dimension){
		coordinates = env.newTArray(dimension, 0);
	}
	
	public int getDimension() {
		return coordinates.length;
	}
	
	public int width(){
		return coordinates[0].length;
	}
}
