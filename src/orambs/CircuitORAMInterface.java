package orambs;

public interface CircuitORAMInterface<T> {
	public T[] readAndRemove(T[] scIden, boolean[] pos, boolean RandomWhenNotFound);
	public void putBack(T[] scIden, T[] scNewPos, T[] scData);
	public int getLengthOfPos();
	public int getLengthOfIndex();
	public T[] read(T[] scIden, boolean[] pos, T[] scNewPos);
	public void write(T[] scIden, boolean[] pos, T[] scNewPos, T[] scData);
	public int getLogN();
}
