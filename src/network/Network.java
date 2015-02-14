package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class Network {
	public ArrayBlockingQueue<byte[]> queue;
	ThreadedIO threadedio;
	public InputStream is;
	protected OutputStream os;
	static int bufferSize = 655360;

	public void setUpThread() {
		queue = new ArrayBlockingQueue<byte[]>(100000);
		threadedio = new ThreadedIO(queue, os);
		new Thread(threadedio).start();
	}

	public void flush(){
		while(!queue.isEmpty()) {
		}
	}
	public byte[] readBytes(int len) {
		byte[] temp = new byte[len];
		try {
			int remain = len;
			while (0 < remain) {
				int readBytes;

				readBytes = is.read(temp, len - remain, remain);
				if (readBytes != -1) {
					remain -= readBytes;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return temp;
	}

	public byte[] readBytes() {
		byte[] lenBytes = readBytes(4);
		int len = ByteBuffer.wrap(lenBytes).getInt();
		return readBytes(len);
	}

	public void writeByte(byte[] data) {
		writeByte(ByteBuffer.allocate(4).putInt(data.length).array(), 4);
		writeByte(data, data.length);
	}

	public void writeByte(byte[] data, int length) {
		try {
//			os.write(data);
			queue.put(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeBI(Network w, BigInteger bi) {
		w.writeByte(bi.toByteArray());
}

public static BigInteger readBI(Network w) {
	byte[] rep = w.readBytes();

	return (rep == null) ? BigInteger.ZERO : new BigInteger(rep);
}

}
