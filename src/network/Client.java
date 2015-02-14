package network;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client extends Network {

	public void connect(String server, int port) throws Exception {
		socketChannel = SocketChannel.open();
		while(!socketChannel.isConnected()) {
			socketChannel.connect(new InetSocketAddress("localhost", 54321));
		}
	}
}
