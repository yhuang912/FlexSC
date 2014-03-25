package gc;

import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;

public class Signal {
	static final int len = 10;
	public byte[] bytes;
	boolean v;
	
	static final Signal ZERO = new Signal(new byte[len]);

	private Signal(byte[] b) { bytes = b; }
	
	public Signal(boolean b) { v = b; }
	public static Signal freshLabel(SecureRandom rnd) {
		byte[] b = new byte[len];
		rnd.nextBytes(b);
		return new Signal(b);
	}
	
	public static Signal newInstance(byte[] bs) {
		byte[] b = Arrays.copyOf(bs, len);
		return new Signal(b);
	}
	
	public Signal(Signal lb) { v = lb.v; bytes = (lb.bytes == null) ? null : Arrays.copyOf(lb.bytes, len); }
	
	public boolean isPublic () { return bytes == null; }
	
	Signal xor(Signal lb) {
		byte[] nb = new byte[len];
		for (int i = 0; i < len; i++)
			nb[i] = (byte) (bytes[i] ^ lb.bytes[i]);
		return new Signal(nb);
	}
	
	public void setLSB() {
		bytes[0] |= 1;
	}
	
	public boolean getLSB() {
		return (bytes[0] & 1) == 1;
	}
	
	// 'send' and 'receive' are supposed to be used only for secret signals
	public void send(OutputStream os) {
		try { os.write(bytes);	}
		catch (Exception e) { e.printStackTrace(); }
	}

	// 'send' and 'receive' are supposed to be used only for secret signals
	public static Signal receive(InputStream ois) {
		byte[] b = new byte[len];
		try { ois.read(b);	}
		catch (Exception e) { e.printStackTrace(); }
		return new Signal(b);
	}
	
	@Override
	public boolean equals(Object lb) {
		if (this == lb)
			return true;
		else if (lb instanceof Signal)
			return Arrays.equals(bytes, ((Signal) lb).bytes);
		else
			return false;
	}
	
	public String toHexStr() {
		StringBuilder str = new StringBuilder();
		for (byte b : bytes)
			str.append(Integer.toHexString(b & 0xff));
		return str.toString();
	}
}
