package oakland;

public class KeyValue<T> {
	public T[] key;
	public T[] value;

	public KeyValue(T[] key, T[] value) {
		this.key = key;
		this.value = value;
	}
}