package network;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class Network {
	public static int bufferSize = 10000000;
	public SocketChannel socketChannel;
//	public ByteBuffer readbuffer = ByteBuffer.allocate(bufferSize);
	public ByteBuffer writebuffer = ByteBuffer.allocate(bufferSize);

	public byte[] readBytes(int len) {
//		System.out.println(socketChannel.isBlocking());
		ByteBuffer readbuffer=null;
		try {
			readbuffer = ByteBuffer.allocate(len);
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
//		System.out.println(readbuffer.);
		return readbuffer.array();
	}


	public int readInt() {
		byte[] lenBytes = readBytes(4);
		return ByteBuffer.wrap(lenBytes).getInt();
	}
	public byte[] readBytes( ){
		byte[] lenBytes = readBytes(4);
		int len = ByteBuffer.wrap(lenBytes).getInt();
		return readBytes(len);
	}

	public void writeInt(int a) {
		try {
			ByteBuffer res = ByteBuffer.allocate(4).putInt(a);
			res.flip();
			socketChannel.write(res);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeByte(byte[] data) {
		try {
			ByteBuffer writebuffer = ByteBuffer.allocate(data.length+4);
			writebuffer.clear();
			ByteBuffer tmp = ByteBuffer.allocate(4).putInt(data.length);
			writebuffer.put(tmp.array());
			writebuffer.put(data);
			writebuffer.flip();
			socketChannel.write(writebuffer);


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeByte(byte[] data, int len) {
		try {
			ByteBuffer writebuffer = ByteBuffer.allocate(len);

			writebuffer.clear();
			writebuffer = ByteBuffer.allocate(data.length);
			writebuffer.put(data);
			writebuffer.flip();
			socketChannel.write(writebuffer);

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
	
	abstract public void disconnect() throws Exception;

	public void writeBI(BigInteger bi) {
		writeByte(bi.toByteArray());
	}

	public BigInteger readBI() {
		byte[] rep = readBytes();

		return (rep == null) ? BigInteger.ZERO : new BigInteger(rep);
	}
	
	static public byte[] readBytes(InputStream is, int len) throws IOException {

		byte[] temp = new byte[len];
		int remain = len;
		while (0 < remain) {
			int readBytes = is.read(temp, len - remain, remain);
			System.out.println(remain);
			if (readBytes != -1) {
				remain -= readBytes;
			}
		}
		return temp;
	}
}
