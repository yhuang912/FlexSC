package circuits;

import test.TestSort.TestGraphNode;
import test.parallel.GraphNode;
import flexsc.CompEnv;

public class TestComparator<T> implements Comparator<T> {

	CompEnv<T> env;

	public TestComparator(CompEnv<T> env) {
		super();
		this.env = env;
	}

	@Override
	public T leq(GraphNode<T> node1, GraphNode<T> node2) {
		TestGraphNode<T> n1 = (TestGraphNode<T>) node1;
		TestGraphNode<T> n2 = (TestGraphNode<T>) node2;
		IntegerLib<T> lib = new IntegerLib<>(env);
		return lib.leq(n1.u, n2.u);
	}
}
