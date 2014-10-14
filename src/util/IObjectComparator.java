package util;

import flexsc.IWritable;

public interface IObjectComparator<V extends IWritable<V, T>, T> {
	public T compare(V v1, V v2) throws Exception;
}
