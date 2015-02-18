

public class TestSend {

	
	public static class GenRunnable extends network.Server implements Runnable {

		public void run() {
			try {
				listen(54321);

				byte[] data = new byte[10];
				double t1 = System.nanoTime();
				for(int i = 0; i < 100000000; ++i) {
//					writeByte(data, 10);
				}
//				os.flush();
				System.out.println("a"+(System.nanoTime()-t1)/1000000000.0);
				os.flush();
				disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static class EvaRunnable extends network.Client implements Runnable {
		public void run() {
			try {
				connect("localhost", 54321);				
			
				double t1 = System.nanoTime();
				for(int i = 0; i < 100000000; ++i) {
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

	public static void main(String[] args)throws Exception {
		GenRunnable gen = new GenRunnable();
		EvaRunnable eva = new EvaRunnable();

		Thread tGen = new Thread(gen);
		Thread tEva = new Thread(eva);
		tGen.start(); Thread.sleep(5);
		tEva.start();
		tGen.join();
		
	}
}
