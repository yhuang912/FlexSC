package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class NetworkUtil {

	public static byte[] readBytes(InputStream is, int len) throws IOException
	{
		byte[] temp = new byte[len];
		int remain = len;
		while(0 < remain)
		{
			int readBytes = is.read(temp, len-remain, remain);
			if (readBytes != -1) {
				remain -= readBytes;
			}
		}
		return temp;
	}

	public static int readInt(InputStream is) throws IOException {
		byte[] lenBytes = readBytes(is, 4);
		return ByteBuffer.wrap(lenBytes).getInt();
	}

	public static void writeInt(OutputStream os, int data) throws IOException {
		os.write(ByteBuffer.allocate(4).putInt(data).array());
	}

	public static byte[] readBytes(InputStream is) throws IOException {
		byte[] lenBytes = readBytes(is, 4);
		int len = ByteBuffer.wrap(lenBytes).getInt();
		return readBytes(is, len);
	}

	public static void writeByte(OutputStream os, byte[] data) throws IOException {
		os.write(ByteBuffer.allocate(4).putInt(data.length).array());
		os.write(data);
	}

	public static Socket connect(String server, int port) throws InterruptedException {
		Socket sock;
		while(true){
			try{
				sock = new Socket(server, port);          // create socket and connect
				if(sock != null)
					break;
			} catch(IOException e){
				Thread.sleep(100);
			}
		}
		return sock;
	}
}
