package oakland.tmp;
import java.util.Arrays;

import util.IObjectComparator;
import flexsc.CompEnv;
import flexsc.IWritable;

public class KeyValueObject<K extends IWritable<K, T>, V extends IWritable<V, T>, T> implements IWritable<KeyValueObject<K, V, T>, T> {
	public K key;
	public V value;
	CompEnv<T> env;
	K k;
	V v;
	public T dummy;
	public KeyValueObject(K key, V value, T dummy, CompEnv<T> env, K k, V v) {
		this.key = key;
		this.value = value;
		this.env = env;
		this.k = k;
		this.v = v;
		this.dummy = dummy;
	}

	@Override
	public int numBits() {
		return key.numBits()+value.numBits()+1;
	}

	@Override
	public T[] getBits() {
		T[] res = env.newTArray(numBits());
		T[] keya = key.getBits();
		System.arraycopy(keya, 0, res, 0, keya.length);
		T[] valuea = value.getBits();
		System.arraycopy(valuea, 0, res, keya.length, valuea.length);
		res[res.length-1] = dummy;
		return res;
	}

	@Override
	public KeyValueObject<K, V, T> newObj(T[] data) throws Exception {
		T[] keya = Arrays.copyOf(data, k.numBits());
		T[] valuea = Arrays.copyOfRange(data, k.numBits(), data.length-1);
		T dummy = data[data.length-1];
		return new KeyValueObject<K, V, T>(k.newObj(keya), v.newObj(valuea), dummy, env, k, v);
	}
	
	public IObjectComparator<KeyValueObject<K, V, T>, T> DummyK(IObjectComparator<K, T> cmp){
		return null;	
	}
}