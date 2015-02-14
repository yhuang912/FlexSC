package network;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

public class ThreadedIO implements Runnable {
	public ArrayBlockingQueue<byte[]> queue;
	OutputStream os;
	public ThreadedIO(ArrayBlockingQueue<byte[]> queue, OutputStream os) {
		this.queue = queue;
		this.os = os;
	}
	public void run() {
		byte[] t = null;
		while(true) {
			try {
			 t = queue.poll();
			while(t != null) {
					os.write(t);
			}
				Thread.sleep(10);
			} catch (Exception e) {
				System.out.println(Arrays.toString(t));
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
				
		}
	}
}
