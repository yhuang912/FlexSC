package oramgc;

import gc.Signal;

public class Block {

	public Signal[] iden;
	public Signal[] pos;
	public Signal[] data;
	
	public Block(Signal[] iden, Signal[] pos, Signal[] data) {
		this.iden = iden;
		this.pos = pos;
		this.data = data;
	}

}
