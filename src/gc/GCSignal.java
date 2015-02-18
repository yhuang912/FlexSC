// Copyright (C) 2013 by Yan Huang <yhuang@cs.umd.edu>
// Improved by Xiao Shaun Wang <wangxiao@cs.umd.edu> and Kartik Nayak <kartik@cs.umd.edu>

package gc;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Arrays;

import network.Server;

public class GCSignal {
	public static final int buffercap = 20000000;
	public static final int buffercap2 = 20000000;
	 static int buffersize = 0;
	static int buffersize2 = 0;
	static byte[][] buffer = new byte[buffercap][10];
	static GCSignal[] buffer2 = new GCSignal[buffercap];
	
	static {
		for(int i = 0; i < buffercap; ++i) {
			buffer2[i] = new GCSignal(false);
		}
	}
	
	
	public static final int len = 10;
	public byte[] bytes;
	public boolean v;

	public static final GCSignal ZERO = new GCSignal(new byte[len]);

	public GCSignal(byte[] b) {
		bytes = b;
	}

	public GCSignal(boolean b) {
		v = b;
	}

	public static int increment1 () {
		int size = buffersize;
		buffersize = (buffersize+1) % buffercap;
		return size;
	}
	
	public static int increment2 () {
		int size = buffersize2;
		buffersize2 = (buffersize2+1) % buffercap2;
		return size;
	}	
	public static GCSignal freshLabel(SecureRandom rnd) {
//		byte[] b = new byte[len];
		byte[] b = buffer[increment1()];
		rnd.nextBytes(b);
		GCSignal ret = buffer2[increment2()];
		ret.bytes = b;
		return ret;
	}

	public static GCSignal newInstance(byte[] bs) {
		assert (bs.length <= len) : "Losing entropy when constructing signals.";
//		byte[] b = new byte[len];
		byte[] b = buffer[increment1()];
		Arrays.fill(b, (byte) ((bs[0] < 0) ? 0xff : 0));
		int mini = len > bs.length ? bs.length : len;
		System.arraycopy(bs, 0, b, len - mini, mini);
		GCSignal ret = buffer2[increment2 ()];
		ret.bytes = b;
		return ret;
	}

//	public GCSignal(GCSignal lb) {
//		v = lb.v;
//		bytes = (lb.bytes == null) ? null : Arrays.copyOf(lb.bytes, len);
//	}

	public boolean isPublic() {
		return bytes == null;
	}

	public GCSignal xor(GCSignal lb) {
//		byte[] nb = new byte[len];
		byte[] nb = buffer[increment1()];
		for (int i = 0; i < len; i++)
			nb[i] = (byte) (bytes[i] ^ lb.bytes[i]);
//		return new GCSignal(nb);
		GCSignal ret = buffer2[increment2 ()];
		ret.bytes = nb;
		return ret;
	}

	public void setLSB() {
		bytes[0] |= 1;
	}

	public int getLSB() {
		return bytes[0] & 1;
	}

	// 'send' and 'receive' are supposed to be used only for secret signals
	public void send(OutputStream os) {
		try {
			os.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 'send' and 'receive' are supposed to be used only for secret signals
	public static GCSignal receive(InputStream ois) {
		byte[] b = buffer[increment1()];
		try {
			Server.readBytes(ois, b);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		return new GCSignal(b);
		GCSignal ret = buffer2[increment2 ()];
		ret.bytes = b;
		return ret;
	}

	@Override
	public boolean equals(Object lb) {
		if (this == lb)
			return true;
		else if (lb instanceof GCSignal)
			return Arrays.equals(bytes, ((GCSignal) lb).bytes);
		else
			return false;
	}

//	public String toHexStr() {
//		StringBuilder str = new StringBuilder();
//		for (byte b : bytes)
//			str.append(Integer.toHexString(b & 0xff));
//		return str.toString();
//	}
}
