package network;


public class CustomizedConcurrentQueue {

	int capacity;
	int size = 0;
	byte[] data;
	public CustomizedConcurrentQueue(int capacity) {
		this.capacity = capacity;
		data = new byte[capacity];
	}
	
	public synchronized void insert (byte[] in) {
		while (in.length+size > capacity){}
		System.arraycopy(in, 0, data, size, in.length);
		size +=in.length;
		
	}
	
	public synchronized int pop(byte[] d) {
		System.arraycopy(data, 0, d, 0, size);
		int oldsize = size;
		size = 0;
		return oldsize;
	}
}
