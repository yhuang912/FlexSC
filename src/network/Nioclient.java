package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Nioclient {
	public static void main(String[] args) throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.connect(new InetSocketAddress("localhost", 54321));
		System.out.println("connected");
		ByteBuffer buf = ByteBuffer.allocate(48);

//		int bytesRead = socketChannel.read(buf);
		String newData = "New String to write to file..." + System.currentTimeMillis();

		buf.clear();
		buf.put(newData.getBytes());

		buf.flip();

		while(buf.hasRemaining()) {
			socketChannel.write(buf);
		}
	}
}
