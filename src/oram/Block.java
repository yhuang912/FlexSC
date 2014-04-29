package oram;

public class Block<T> {

	public T[] iden;
	public T[] pos;
	public T[] data;
	public T isDummy;
	
	public Block(T[] iden, T[] pos, T[] data, T isDummy) {
		this.iden = iden;
		this.pos = pos;
		this.data = data;
		this.isDummy = isDummy;
	}

}
