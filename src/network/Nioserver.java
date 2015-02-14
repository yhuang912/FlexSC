//package network;
//
//import java.util.Arrays;
//
//import org.junit.Assert;
//
//import util.Utils;
//import circuits.arithmetic.FloatLib;
//import flexsc.CompEnv;
//import flexsc.Flag;
//import flexsc.Mode;
//import flexsc.PMCompEnv;
//import flexsc.Party;
//
//public class TestFloat{
//	static public void main(String[] args) throws Exception {
//		Server s = new Server();
//		s.listen(54321);
//		System.out.println("connected");
//		System.out.println(Arrays.toString(Server.readBytes(s.is, 10)));
//		s.disconnect();
//	}
//}

package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Nioserver {
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(54321));
		AsynchronousSocketChannel socketChannel=serverSocketChannel.accept().get();
//		socketChannel.configureBlocking(false);

		System.out.println("connected");
		ByteBuffer buf = ByteBuffer.allocate(10);
		int len = 10;
		byte[] res = new byte[len];		
		//		for(int i = 0; i < 10000; ++i) {
		int i = 0;
		int bytesRead = socketChannel.read(buf).get(); //read into buffer.
		while (bytesRead != -1) {
			buf.flip();  //make buffer ready for read
			//			  System.out.println(bytesRead);
//			while(buf.hasRemaining()) {
//				System.out.print((char) ); // read 1 byte at a time
//				buf.get();
//			}
			if(buf.remaining() >= 10) {
				buf.get(res);
//				System.out.println(Arrays.toString(res));
			}
			
			buf.clear(); //make buffer ready for writing
			bytesRead = socketChannel.read(buf).get();
		}
		//			buf.flip();
		//			buf.get(res);
		//			System.out.println(Arrays.toString(res));
		//		}



		//		System.out.println(Arrays.toString();
	}
}
