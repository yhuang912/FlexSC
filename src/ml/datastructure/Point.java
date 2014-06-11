package ml.datastructure;

import flexsc.CompEnv;

public class Point<T> {
	public T[][] coordinates;
	public T isDummy;
	
	public Point(T[][] p, T isDummy) {
		this.coordinates = p;
		this.isDummy = isDummy;
	}
	
	public Point(CompEnv<T> env, int dimension, int width, boolean isDummy){
		coordinates = env.newTArray(dimension, width);
		for(int i = 0; i < coordinates.length; ++i)
			for(int j = 0; j< coordinates[i].length; ++j)
				coordinates[i][j] = env.ZERO();
		this.isDummy = isDummy ? env.ONE() : env.ZERO();
	}

	// for efficiency
	public Point(CompEnv<T> env, int dimension, boolean isDummy) {
		coordinates = env.newTArray(dimension, 0);
		this.isDummy = isDummy ? env.ONE() : env.ZERO();
	}
	
	public int dimension() {
		return coordinates.length;
	}
	
	public int width(){
		return coordinates[0].length;
	}
}
