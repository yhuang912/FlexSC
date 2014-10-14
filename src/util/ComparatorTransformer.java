package util;

import flexsc.Comparator;
import flexsc.IWritable;

public class ComparatorTransformer<V extends IWritable<V, T>, T> {
	V factoryV;
	IObjectComparator<V, T> oc;
	public ComparatorTransformer(V factoryV, IObjectComparator<V, T> oc) {
		this.factoryV = factoryV;
		this.oc = oc;
	}
	public Comparator<T> toComparator() {
		return new Comparator<T>() {
			@Override
			public T compare(T[] a, T[] b) throws Exception {
				return oc.compare(factoryV.newObj(a), factoryV.newObj(b));
			}
		};
	}
}
