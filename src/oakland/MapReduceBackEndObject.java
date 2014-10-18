package oakland;

import util.ComparatorTransformer;
import util.IObjectComparator;
import util.Utils;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;
import flexsc.IWritable;

public abstract class MapReduceBackEndObject<T, K extends IWritable<K, T>, V extends IWritable<V, T>> {
	protected CompEnv<T> env;

	IntegerLib<T> lib;

	public MapReduceBackEndObject(CompEnv<T> env) {
		this.env = env;
		lib = new IntegerLib<T>(env);
	}

	abstract public KeyValueObject<K, V, T> map(T[] inputs) throws Exception;
	abstract public T eq(KeyValueObject<K, V, T> value1, KeyValueObject<K, V, T> value2) throws Exception;

	abstract public KeyValueObject<K, V, T> reduce(KeyValueObject<K, V, T> value1, KeyValueObject<K, V, T> value2) throws Exception;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public KeyValueObject<K, V, T>[] MapReduce(T[][] inputs, T[] cnt, 
			int cap, IObjectComparator<KeyValueObject<K, V, T>, T> cmp1, 
			IObjectComparator<KeyValueObject<K, V, T>, T> cmp2)
			throws Exception {
		KeyValueObject[] kv = new KeyValueObject[inputs.length];
		for (int i = 0; i < inputs.length; ++i) {
			kv[i] = map(inputs[i]);
		}
		cnt = lib.toSignals(1, 32);
		lib.sort(kv, lib.SIGNAL_ONE, new ComparatorTransformer<KeyValueObject<K, V, T>, T>(kv[0], cmp1).toComparator());

		for (int i = 1; i < kv.length; ++i) {
			KeyValueObject<K, V, T> result = reduce(kv[i - 1], kv[i]);
			T same = eq(kv[i - 1], kv[i]);
			cnt = lib.conditionalIncreament(cnt, lib.not(same));

			kv[i] = mux(kv[i], result, same);
			kv[i - 1] = mux(kv[i - 1], lib.zeros(kv[i - 1].numBits()), same);
		}

		lib.sort(kv, lib.SIGNAL_ONE, new ComparatorTransformer<KeyValueObject<K, V, T>, T>(kv[0], cmp2).toComparator());

		KeyValueObject<K, V, T>[] res = new KeyValueObject[cap];
		for (int i = 0; i < res.length; ++i) {
			res[i] = kv[i];
		}
		return res;
	}

	private KeyValueObject<K, V, T> mux(KeyValueObject<K, V, T> keyValueObject,
			KeyValueObject<K, V, T> result, T same) throws Exception {
		return result.newObj(lib.mux(keyValueObject.getBits(), result.getBits(), same));
	}
}
