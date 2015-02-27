package test.harness;

import java.io.IOException;
import java.io.InputStream;

public class TestSend {

	public static int PORT = -1;
	public static int ID = -1;
	public class GenRunnable extends network.Server implements Runnable {

		public void run() {
			try {
				listen(PORT);

				byte[] data = new byte[10];
				double t1 = System.nanoTime();
				for(int i = 0; i < 100000000; ++i) {
                                        os.write(data);
//					writeByte(data, 10);
				}
//				os.flush();
				double t2 = (System.nanoTime()-t1)/1000000000.0;
				System.out.println(ID + " " + t2 + " " + 1.0 * 1000000*1000*8/(1024 * 1024 * t2));
				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public class EvaRunnable extends network.Client implements Runnable {
		public void run() {
			try {
				connect("localhost", PORT);				
			
				double t1 = System.nanoTime();
				for(int i = 0; i < 100000000; ++i) {
					readBytes(is, 10);
//					readBytes(10);
				}
				System.out.println("b"+(System.nanoTime()-t1)/1000000000.0);

				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

        public void runThreads() throws Exception {
		GenRunnable gen = new GenRunnable();
		EvaRunnable env = new EvaRunnable();
		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(env);
		tGen.start();
		Thread.sleep(5);
		tEva.start();
		tGen.join();
		tEva.join();
	}
	
        static public byte[] readBytes(InputStream is, int len) throws IOException {

    		byte[] temp = new byte[len];
    		int remain = len;
    		while (0 < remain) {
    			int readBytes = is.read(temp, len - remain, remain);
    			if (readBytes != -1) {
    				remain -= readBytes;
    			}
    		}
    		return temp;
    	}
        
	public static void main(String args[]) throws Exception {
		 TestSend test = new TestSend();
		 TestSend.PORT = Integer.parseInt(args[1]);
		 TestSend.ID = Integer.parseInt(args[2]); 
		 if(new Integer(args[0]) == 0)
			 test.new GenRunnable().run();
		 else test.new EvaRunnable().run();
	}/*
	public static void main(String[] args)throws Exception {
		GenRunnable gen = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();

		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(5);
		tEva.start();
		tGen.join();
		
	}*/
}
