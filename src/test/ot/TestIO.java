		package test.ot;
		
		import java.io.InputStream;
		import java.io.OutputStream;
		import java.net.ServerSocket;
		import java.net.Socket;
		
		public class TestIO {
			static final int n = 500000;
			final static byte d  = 10;
		
			static class SenderRunnable implements Runnable {
				SenderRunnable () {}
				private ServerSocket sock;
				protected OutputStream os;
		
				public void run() {
					try {
						sock = new ServerSocket(12345);            // create socket and bind to port
						Socket clientSock = sock.accept();                   // wait for client to connect
						os = clientSock.getOutputStream();  
		
						byte[] a = new byte[10];
						for(int i = 0; i < 10; ++i)
							a[i] = d;
		
						for (int i = 0; i < n; i++) {
							os.write(a);
						}
						os.flush();
		
						sock.close(); 
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			}
		
			static class ReceiverRunnable implements Runnable {
				ReceiverRunnable () {}
				private Socket sock;
				public InputStream is;
				public void run() {
					try {
						sock = new java.net.Socket("localhost", 12345);          // create socket and connect
						is = sock.getInputStream();
		
						for (int i = 0; i < n; i++) {
							byte[] temp = new byte[10];
							is.read(temp);
							for(int j = 0; j < 10; ++j)
								if(temp[j] != d){
									System.out.println("weird!"+" "+i+" "+j+" "+temp[j]);
								}
						}
						sock.close(); 
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			}
		
		
			public static void test1Case() throws Exception {
				SenderRunnable sender = new SenderRunnable();
				ReceiverRunnable receiver = new ReceiverRunnable();
				Thread tSnd = new Thread(sender);
				Thread tRcv = new Thread(receiver);
				tSnd.start(); 
				tRcv.start(); 
				tSnd.join();
				tRcv.join();
			}
		
			public static void main(String[] args)throws Exception {
				test1Case();
				test1Case();
				test1Case();
				test1Case();
				test1Case();
				test1Case();
				test1Case();
				test1Case();
			}
		}