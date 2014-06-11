package ml.circuit;

import com.sun.org.apache.bcel.internal.generic.NEW;

import ml.datastructure.Point;
import flexsc.CompEnv;

public class CanopyLib<T> extends PointLib<T> {

	static Point POINT_ZERO;
	public CanopyLib(CompEnv<T> e, int dimension, int width) {
		super(e, dimension, width);
		POINT_ZERO = new Point<>(e, dimension, width);
	}
	
	public Point<T>[] map(Point<T>[] points, int numberOfCanopyCenters) {
		Point[] canopyCenters = new Point[numberOfCanopyCenters];
		return points;
	}
	
	public void addPointToCanopies(Point<T>[] canopies, Point<T> point, 
			T[] t1, T[] t2) throws Exception {
		T[][] distanceToCanopies = env.newTArray(canopies.length, 0);
		for(int i = 0; i < distanceToCanopies.length; ++i)
			distanceToCanopies[i] = L1Distance(point, point);
		
		T pointStronglyBound = SIGNAL_ZERO;
		
		for(int i = 0; i < distanceToCanopies.length; ++i){
			T withinT1 = leq(distanceToCanopies[i], t1);
			// either observe zero point or real point
			ConditionalCanopyObserve(canopies, point, withinT1);
			
			T withinT2 = not(geq(distanceToCanopies[i], t2));
			pointStronglyBound = or(pointStronglyBound, withinT2);
		}
		
		ConditionalCanopyAdd(canopies, point, not(pointStronglyBound));
	}
	
	public void ConditionalCanopyObserve(Point<T>[] canopies, Point<T> point,
			T shouldObserve){
		CanopyObserve(canopies, mux(POINT_ZERO, point, shouldObserve));
	}
	
	public void CanopyObserve(Point<T>[] canopies, Point<T> point){
		
	}

	public void ConditionalCanopyAdd(Point<T>[] canopies, Point<T> point,
			T shouldAdd){
		
	}
	
}