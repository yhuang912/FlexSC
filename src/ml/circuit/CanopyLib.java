package ml.circuit;

import ml.datastructure.Point;
import flexsc.CompEnv;

public class CanopyLib<T> extends PointLib<T> {

	public CanopyLib(CompEnv<T> e, int dimension, int width) {
		super(e, dimension, width);
	}
	
	public Point<T>[] map(Point<T>[] points, int numberOfCanopyCenters){
		Point[] canopyCenters = new Point[numberOfCanopyCenters];
		return points;
	}
	
	public void addPointToCanopies(Point<T> point, Point<T>[] canopies){
		
	}

}
