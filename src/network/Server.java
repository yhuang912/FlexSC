package network;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

public class Server extends Network{
	ServerSocketChannel serverSocketChannel ;
	public void listen(int port) throws Exception {
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(port));
		socketChannel=serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
	}
	
	@Override
	public void disconnect() throws Exception {
				while(socketChannel.isConnected()) {
					socketChannel.close();
				}
				serverSocketChannel.close();
				System.out.println(t1/1000000000.0);
	}


}
