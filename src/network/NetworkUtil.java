package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import flexsc.CompEnv;
import flexsc.Mode;
import gc.GCSignal;

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

	public static boolean readBoolean(InputStream is) throws IOException {
		int read = readInt(is);
		return read == 1;
	}

	public static void writeBoolean(OutputStream os, boolean data) throws IOException {
		int sen = data ? 1 : 0;
		writeInt(os, sen);
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

	public static <T> void send(OutputStream os, T[][] data, CompEnv<T> env) throws IOException {
		for (int i = 0; i < data.length; i++) {
			send(os, data[i], env);
		}
	}

	public static <T> T[][] read(InputStream is, int length1, int length2, CompEnv<T> env) throws IOException {
		T[][] ret = env.newTArray(length1, 1);
		for (int i = 0; i < length1; i++) {
			ret[i] = read(is, length2, env);
		}
		return ret;
	}

	public static <T> void send(OutputStream os, T[] data, CompEnv<T> env) throws IOException {
		for (int i = 0; i < data.length; i++) {
			send(os, data[i], env);
		}
	}

	public static <T> void send(OutputStream os, T data, CompEnv<T> env) throws IOException {
		Mode mode = env.getMode();
		if (mode == Mode.REAL) {
			GCSignal gcData = (GCSignal) data;
			gcData.send(os);
		} else if(mode == Mode.VERIFY) {
			Boolean vData = (Boolean) data;
			NetworkUtil.writeBoolean(os, (Boolean) data);
		} else if (mode == Mode.COUNT) {

		}
	}

	public static <T> T[] read(InputStream is, int length, CompEnv<T> env) throws IOException {
		T[] ret = env.newTArray(length);
		for (int i = 0; i < length; i++) {
			ret[i] = read(is, env);
		}
		return ret;
	}

	public static <T> T read(InputStream is, CompEnv<T> env) throws IOException {
		Mode mode = env.getMode();
		if (mode == Mode.REAL) {
			GCSignal signal = GCSignal.receive(is);
			return (T) signal;
		} else if(mode == Mode.VERIFY) {
			Boolean vData = NetworkUtil.readBoolean(is);
			return (T) vData;
		} else if (mode == Mode.COUNT) {
			return env.ZERO();
		}
		// shouldn't happen;
		return null;
	}
}
