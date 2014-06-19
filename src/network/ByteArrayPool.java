package network;

import java.util.Stack;

public class ByteArrayPool {

	Stack<byte[]> pool = new Stack<>();
	int len;
	public ByteArrayPool(int len) {
		this.len = len;
	}

	public void finalize() {
		while(!pool.empty()) {
			pool.pop();
		}
	}
	
	public byte[] get(){
		if(! pool.empty()) {
			return pool.pop();
		}
		else {
			return new byte[len];
		}
	}
	
	public void put(byte [] in) {
		pool.push(in);
	}
}
