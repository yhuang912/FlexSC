package ml.circuit;

import ml.datastructure.Point;
import flexsc.CompEnv;

public class CanopyLib<T> extends PointLib<T> {

	static Point POINT_ZERO;
	static Point POINT_DUMMY;

	public CanopyLib(CompEnv<T> e, int dimension, int width) {
		super(e, dimension, width);
		POINT_ZERO = new Point<T>(e, dimension, width, false);
		POINT_DUMMY = new Point<T>(e, dimension, width, true);
	}
	
	public Point<T>[] map(Point<T>[] points, int numberOfCanopyCenters, int t1, int t2) {
		Point<T>[] canopyCenters = new Point[numberOfCanopyCenters];
		for(int i = 0; i < canopyCenters.length; ++i)
			canopyCenters[i] = POINT_DUMMY;
		
		T[] s0 = zeros(width);
		Point<T> s1 = POINT_ZERO;
		Point<T> s2 = POINT_ZERO;
		for(int i = 0; i < points.length; ++i)
			addPointToCanopies(canopyCenters, points[i], toSignals(t1, width), toSignals(t2, width), s0, s1, s2);
		return canopyCenters;
	}
	
	public void addPointToCanopies(Point<T>[] canopies, Point<T> point, 
			T[] t1, T[] t2, T[] s0, Point<T> s1, Point<T> s2) {
		T pointStronglyBound = SIGNAL_ZERO;
		
		for(int i = 0; i < canopies.length; ++i) {
			//may be able to be more precise here.
			T[] distanceToCanopies = L1Distance(point, canopies[i]);
			T withinT1 = leq(distanceToCanopies, t1);
			
			// either observe zero point or real point
			ConditionalCanopyObserve(s0, s1, s2, point, withinT1);
			
			T withinT2 = leq(distanceToCanopies, t2);
			pointStronglyBound = or(pointStronglyBound, withinT2);
		}
		
		ConditionalCanopyAdd(canopies, point, not(pointStronglyBound));
	}
	
	public void ConditionalCanopyObserve(T[] s0, Point<T>s1, Point<T>s2, Point<T> point,
			T shouldObserve) {
		Point<T> p = mux(POINT_ZERO, point, shouldObserve);
		s0 = add(s0, mux(toSignals(0, s0.length), toSignals(1, s0.length), shouldObserve));
		s1 = add(s1, p);
		s2 = add(s2, multiply(p, p));
	}

	public void ConditionalCanopyAdd(Point<T>[] canopies, Point<T> point,
			T condition) {
		T added = not(condition);
		for(int i = 0; i < canopies.length; ++i) {
			T emptySlot = canopies[i].isDummy;
			T shouldAdd = and(not(added), emptySlot);
			added = or(added, shouldAdd);
			canopies[i] = mux(canopies[i], point, shouldAdd);
		}
	}
	
}