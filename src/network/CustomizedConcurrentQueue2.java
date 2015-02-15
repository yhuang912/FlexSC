package network;


public class CustomizedConcurrentQueue2 {
	int capacity;
	int head = 0;
	int tail = 0;
	byte[] data;
	boolean finished = false;
	public CustomizedConcurrentQueue2(int capacity) {
		this.capacity = capacity;
		data = new byte[capacity];
	}

	public void destory() {
		finished = true;
	}

	public  void insert (byte[] in) {
		int remains = capacity - head;
		if(remains >= in.length) {
			System.arraycopy(in, 0, data, head, in.length);
			head +=in.length;//!
		}
		else {
			System.arraycopy(in, 0, data, head, remains);
			System.arraycopy(in, remains, data, 0, in.length-remains);
			head = in.length-remains;
		}
	}

	public int sizeop(int number, int op) {
		if(op == 0) {
			return (head - tail + capacity) % capacity;
		}
		else if(op == 1) {
			head +=number;
		}
		else if(op == 2){
			head = number;
		}
		else if(op == 3){
			tail = number;
		}
		return 0;
	}

	public int size() {
		return (head - tail + capacity) % capacity;
	}

	public  int pop(byte[] d) {
		int size = size();
		if(finished && size == 0) return -1;

		if(size <= 0) return 0;
		int h = head;
		if(head > tail) {
			System.arraycopy(data, tail, d, 0, head-tail);
		} else {
			System.arraycopy(data, tail, d, 0, capacity-tail);
			System.arraycopy(data, 0, d, capacity-tail, head);
		}
		tail = h;
		return size;
	}
}
