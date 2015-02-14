package network;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadedIO implements Runnable {
	public ConcurrentLinkedQueue<byte[]> queue;
	OutputStream os;
	public ThreadedIO(ConcurrentLinkedQueue<byte[]> queue, OutputStream os) {
		this.queue = queue;
		this.os = os;
	}
	public void run() {	
		byte[] t = null;
		try {
			while(true) {
				t = queue.poll();
				while(t != null) {
					os.write(t);
					os.flush();
					t = queue.poll();
				}
				//				Thread.sleep(1);
			}
			
		} catch (Exception e) {
			System.out.println(Arrays.toString(t));
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
}
