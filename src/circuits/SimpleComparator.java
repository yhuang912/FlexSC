package circuits;

import flexsc.CompEnv;

public class SimpleComparator<T> implements Comparator<T> {

	CompEnv<T> env;

	public SimpleComparator(CompEnv<T> env) {
		super();
		this.env = env;
	}

	@Override
	public T leq(T[] ai, T[] aj, T[] datai, T[] dataj) {
		IntegerLib<T> lib = new IntegerLib<>(env);
		return lib.leq(ai, aj);
	}

}
