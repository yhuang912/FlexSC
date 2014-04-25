package oramgc;

import gc.Signal;

public class Block {

	public Signal[] iden;
	public Signal[] pos;
	public Signal[] data;
	public Signal isDummy;
	
	public Block(Signal[] iden, Signal[] pos, Signal[] data, Signal isDummy) {
		this.iden = iden;
		this.pos = pos;
		this.data = data;
		this.isDummy = isDummy;
	}

}
