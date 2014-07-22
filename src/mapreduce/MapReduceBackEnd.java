package mapreduce;

import circuits.BitonicSortLib;
import flexsc.CompEnv;

public abstract class MapReduceBackEnd<T> {
	CompEnv<T> env;
	
	class KeyValue {
		T[] key;
		T[] value;
		public KeyValue(T[] key, T[] value){
			this.key = key;
			this.value = value;
		}
	}
	BitonicSortLib<T> lib;
	public MapReduceBackEnd(CompEnv<T> env) {
		this.env = env;
		lib = new BitonicSortLib<T>(env);
	}
	
	abstract public KeyValue map(T[] inputs) throws Exception;
	abstract public T[] reduce(T[] value1, T[] value2) throws Exception;
	
	public T[][] MapReduce(T[][] inputs) throws Exception{
		T[][] mappedKeys = env.newTArray(inputs.length, 0);
		T[][] mappedValues = env.newTArray(inputs.length, 0);
		for(int i = 0; i < inputs.length; ++i){
			KeyValue tuple = map(inputs[i]);
			mappedKeys[i] = tuple.key;
			mappedValues[i] = tuple.value;
		}
		
		lib.sortWithPayload(mappedKeys, mappedValues, lib.SIGNAL_ONE);
		for(int i = 1; i < mappedValues.length; ++i) {
			T[] result= reduce(mappedValues[i-1], mappedValues[i]);
			T same = lib.eq(mappedKeys[i-1], mappedKeys[i]);
			
			mappedValues[i] = lib.mux(mappedValues[i], result, same);
			mappedValues[i-1] = lib.mux(mappedValues[i-1], lib.zeros(mappedValues[i-1].length), same);
		}
		
		lib.sort(mappedValues, lib.SIGNAL_ZERO);
		
		return mappedValues;
	}
}
