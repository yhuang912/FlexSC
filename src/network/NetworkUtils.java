package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class NetworkUtils {
	static public byte[] readBytes(InputStream is, int len) {

		byte[] temp = new byte[len];
		int remain = len;
		while (0 < remain) {
			int readBytes = 0;
			try {
				readBytes = is.read(temp, len - remain, remain);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (readBytes != -1) {
				remain -= readBytes;
			}
		}
		return temp;
	}

	static public byte[] readBytes(InputStream is) {
		byte[] lenBytes = readBytes(is, 4);
		int len = ByteBuffer.wrap(lenBytes).getInt();
		return readBytes(is, len);
	}

	static public void writeByte(OutputStream os, byte[] data) {
		try {
			os.write(ByteBuffer.allocate(4).putInt(data.length).array());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			os.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public void writeByte(OutputStream os, byte[] data, int length) {
		try {
			os.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
