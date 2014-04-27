package oramgc;

import gc.GCSignal;

public class Block {

	public GCSignal[] iden;
	public GCSignal[] pos;
	public GCSignal[] data;
	public GCSignal isDummy;
	
	public Block(GCSignal[] iden, GCSignal[] pos, GCSignal[] data, GCSignal isDummy) {
		this.iden = iden;
		this.pos = pos;
		this.data = data;
		this.isDummy = isDummy;
	}

}
