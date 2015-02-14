package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

public class Nioclient {
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
		socketChannel.connect(new InetSocketAddress("localhost", 54321)).get();
//		socketChannel.configureBlocking(false);
		System.out.println("connected");

		ByteBuffer writebuffer = ByteBuffer.allocate(409600);
//		int len = 10;
		byte[] data = new byte[]{1,2,3,4,5,6,7,8,9,0};
		double t1= 0;
		for(int i = 0; i < 409600; ++i) {
			writebuffer.put(data);
			writebuffer.flip();
			double tt = System.nanoTime();
			socketChannel.write(writebuffer);
			double tmp = (System.nanoTime()- tt);
			t1+=tmp;
			writebuffer.compact();
//			Thread.sleep(0, 100);
//			System.out.println(tmp/1000000000.0);
		}
		System.out.println(t1/1000000000.0);
//		Thread.sleep(10000000);
//		System.out.println(Arrays.toString();
	}
}