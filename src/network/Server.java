package network;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

public class Server extends Network{
	
	public void listen(int port) throws Exception {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(port));
		socketChannel=serverSocketChannel.accept();
	}


}
