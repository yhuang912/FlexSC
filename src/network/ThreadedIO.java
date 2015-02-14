package network;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;

public class ThreadedIO implements Runnable {
	public ArrayBlockingQueue<byte[]> queue;
	OutputStream os;
	public ThreadedIO(ArrayBlockingQueue<byte[]> queue, OutputStream os) {
		this.queue = queue;
		this.os = os;
	}
	public void run() {
		while(true) {
			byte[] t = queue.poll();
			while(t != null) {
				try {
					os.write(t);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
	}
}
