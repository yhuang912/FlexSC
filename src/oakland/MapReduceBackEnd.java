package oakland;

import util.Utils;
import circuits.BitonicSortLib;
import flexsc.CompEnv;

public abstract class MapReduceBackEnd<T> {
	protected CompEnv<T> env;

	BitonicSortLib<T> lib;

	public MapReduceBackEnd(CompEnv<T> env) {
		this.env = env;
		lib = new BitonicSortLib<T>(env);
	}

	abstract public KeyValue<T> map(T[] inputs) throws Exception;

	abstract public T[] reduce(T[] value1, T[] value2) throws Exception;

	public KeyValue<T>[] MapReduce(T[][] inputs, T[] cnt, int cap)
			throws Exception {
		T[][] mappedKeys = env.newTArray(inputs.length, 0);
		T[][] mappedValues = env.newTArray(inputs.length, 0);
		for (int i = 0; i < inputs.length; ++i) {
			KeyValue<T> tuple = map(inputs[i]);
			mappedKeys[i] = tuple.key;
			mappedValues[i] = tuple.value;
		}
		cnt = lib.toSignals(1, 32);
		lib.sortWithPayload(mappedKeys, mappedValues, lib.SIGNAL_ONE);
		for (int i = 1; i < mappedValues.length; ++i) {
			T[] result = reduce(mappedValues[i - 1], mappedValues[i]);
			T same = lib.eq(mappedKeys[i - 1], mappedKeys[i]);
			cnt = lib.conditionalIncreament(cnt, lib.not(same));

			mappedValues[i] = lib.mux(mappedValues[i], result, same);
			mappedValues[i - 1] = lib.mux(mappedValues[i - 1],
					lib.zeros(mappedValues[i - 1].length), same);
		}

		lib.sortWithPayload(mappedValues, mappedKeys, lib.SIGNAL_ZERO);

		KeyValue<T>[] res = new KeyValue[cap];
		for (int i = 0; i < res.length; ++i) {
			res[i] = new KeyValue(mappedKeys[i], mappedValues[i]);
		}
		return res;
	}

	public KeyValue<T>[] MapReduce(T[][] inputs) throws Exception {
		T[][] mappedKeys = env.newTArray(inputs.length, 0);
		T[][] mappedValues = env.newTArray(inputs.length, 0);
		for (int i = 0; i < inputs.length; ++i) {
			KeyValue<T> tuple = map(inputs[i]);
			mappedKeys[i] = tuple.key;
			mappedValues[i] = tuple.value;
		}
		T[] cnt = lib.toSignals(1, 32);
		lib.sortWithPayload(mappedKeys, mappedValues, lib.SIGNAL_ONE);
		for (int i = 1; i < mappedValues.length; ++i) {
			T[] result = reduce(mappedValues[i - 1], mappedValues[i]);
			T same = lib.eq(mappedKeys[i - 1], mappedKeys[i]);
			cnt = lib.conditionalIncreament(cnt, lib.not(same));

			mappedValues[i] = lib.mux(mappedValues[i], result, same);
			mappedValues[i - 1] = lib.mux(mappedValues[i - 1],
					lib.zeros(mappedValues[i - 1].length), same);
		}

		lib.sortWithPayload(mappedValues, mappedKeys, lib.SIGNAL_ZERO);

		int length = Utils.toInt(lib.declassifyToBoth(cnt));
		KeyValue<T>[] res = new KeyValue[length];
		for (int i = 0; i < res.length; ++i) {
			res[i] = new KeyValue(mappedKeys[i], mappedValues[i]);
		}
		return res;
	}
}
