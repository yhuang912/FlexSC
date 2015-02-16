package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import flexsc.Flag;

public class Network {
	public CustomizedConcurrentQueue2 queue;
	ThreadedIO threadedio;
	public InputStream is;
	protected OutputStream os;
	Thread thd;
	public void setUpThread() {
		if(Flag.THREADEDIO) {
			queue = new CustomizedConcurrentQueue2(Flag.NetworkThreadedQueueSize);
			threadedio = new ThreadedIO(queue, os);
			thd = new Thread(threadedio);
			thd.start();
		}
	}

	public void flush(){
			try {
				os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeBI(BigInteger bi) {
		writeByte(bi.toByteArray());
	}

	public BigInteger readBI() {
		byte[] rep = readBytes();

		return new BigInteger(rep);
	}
	
	public void writeInt(int i) {
		writeByte(ByteBuffer.allocate(4).putInt(i).array(), 4);
	}

	public int readInt() {
		return ByteBuffer.wrap(readBytes(4)).getInt();
	}	

}
