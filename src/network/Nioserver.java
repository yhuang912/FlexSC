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
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class Nioserver {
	public static void main(String[] args) throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(54321));
		SocketChannel socketChannel=serverSocketChannel.accept();

		System.out.println("connected");

		
		ByteBuffer buf = ByteBuffer.allocate(48);

		int a = socketChannel.read(buf);
		
		while(a != -1) {
			a = socketChannel.read(buf);
		}
		System.out.println(Arrays.toString(buf.array()));
	}
}
