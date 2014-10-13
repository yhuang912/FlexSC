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

	@Override
	public void swap(GraphNode<T> node1, GraphNode<T> node2, T swap) {
		TestGraphNode<T> n1 = (TestGraphNode<T>) node1;
		TestGraphNode<T> n2 = (TestGraphNode<T>) node2;

		IntegerLib<T> lib = new IntegerLib<>(env);
		T[] s = lib.mux(n2.u, n1.u, swap);
		s = lib.xor(s, n1.u);
		T[] ki = lib.xor(n2.u, s);
		T[] kj = lib.xor(n1.u, s);
		n1.u = ki;
		n2.u = kj;
	}
}
