package network;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Network {
	public static int bufferSize = 10000000;
	public SocketChannel socketChannel;
	public ByteBuffer readbuffer = ByteBuffer.allocate(bufferSize);
	public ByteBuffer writebuffer = ByteBuffer.allocate(bufferSize);

	public byte[] readBytes(int len) {
		try {
			readbuffer.clear();
			int remain = len;
			while (0 < remain) {
				int readBytes;

				readBytes = socketChannel.read(readbuffer);

				if (readBytes != -1) {
					remain -= readBytes;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return readbuffer.array();
	}


	public byte[] readBytes( ){
		byte[] lenBytes = readBytes(4);
		int len = ByteBuffer.wrap(lenBytes).getInt();
		return readBytes(len);
	}

	public void writeByte(byte[] data) {
		try {
//			writebuffer.clear();
			ByteBuffer tmp = ByteBuffer.allocate(4+data.length).putInt(data.length);
//			writebuffer.put(tmp.array());
			tmp.put(data);
			tmp.flip();
			socketChannel.write(tmp);

			while(tmp.hasRemaining()) {
				socketChannel.write(tmp);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeByte(byte[] data) {
		try {
//			writebuffer.clear();
			ByteBuffer tmp = ByteBuffer.allocate(4+data.length).putInt(data.length);
//			writebuffer.put(tmp.array());
			tmp.put(data);
			tmp.flip();
			socketChannel.write(tmp);

			while(tmp.hasRemaining()) {
				socketChannel.write(tmp);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void flush() {
//		try {
//		while(writebuffer.hasRemaining()){
//			socketChannel.write(writebuffer);
//		}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	public void disconnect() throws Exception {
		socketChannel.close();
	}

	public void writeBI(BigInteger bi) {
		writeByte(bi.toByteArray());
	}

	public BigInteger readBI() {
		byte[] rep = readBytes();

		return (rep == null) ? BigInteger.ZERO : new BigInteger(rep);
	}
}
