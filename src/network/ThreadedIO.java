package network;

import java.io.OutputStream;

import flexsc.Flag;

public class ThreadedIO implements Runnable {
	public CustomizedConcurrentQueue2 queue;
	OutputStream os;
	public ThreadedIO(CustomizedConcurrentQueue2 queue, OutputStream os) {
		this.queue = queue;
		this.os = os;
	}

	byte[] res = new byte[Flag.NetworkThreadedQueueSize];
	public void run() {	
		try {			
			while(true) {
				int len  = queue.pop(res);
				if(len == -1)return;
				if(len != 0) {
//					System.out.println(len);
					os.write(res, 0, len);
					os.flush();
					Thread.sleep(2);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
}
