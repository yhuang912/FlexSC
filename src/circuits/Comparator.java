package circuits;

import test.parallel.GraphNode;

public interface Comparator<T> {

	public T leq(GraphNode<T> node1, GraphNode<T> node2);
}
