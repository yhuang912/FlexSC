package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import flexsc.Flag;

public class Network {
	//	public ConcurrentLinkedQueue<byte[]> queue;
	public CustomizedConcurrentQueue2 queue;
	ThreadedIO threadedio;
	public InputStream is;
	protected OutputStream os;
	static int bufferSize = 1024*1024*10;
	static int queuesize = 1024*1024*300;
	Thread thd;
	public void setUpThread() {
		if(Flag.THREADEDIO) {
			queue = new CustomizedConcurrentQueue2(queuesize);
			threadedio = new ThreadedIO(queue, os);
			thd = new Thread(threadedio);
			thd.start();
		}
	}

	public void flush(){
		if(! Flag.THREADEDIO) {
			try {
				os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		//System.out.println("\t"+len);
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
			if(Flag.THREADEDIO)
				queue.insert(data);
			else {
				os.write(data);
			}
			//			os.write(data);
			//			queue.add(data);
			//			queue.insert(data);
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
