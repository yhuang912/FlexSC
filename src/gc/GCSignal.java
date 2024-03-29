package gc;

import java.io.*;
import java.security.SecureRandom;
import java.util.Arrays;

import flexsc.Signal;

public class GCSignal extends Signal {
	static final int len = 10;
	public byte[] bytes;
	boolean v;
	
	static final GCSignal ZERO = new GCSignal(new byte[len]);

	private GCSignal(byte[] b) { bytes = b; }
	
	public GCSignal(boolean b) { v = b; }
	
	public static GCSignal freshLabel(SecureRandom rnd) {
		byte[] b = new byte[len];
		rnd.nextBytes(b);
		return new GCSignal(b);
	}
	
	public static GCSignal newInstance(byte[] bs) {
		assert (bs.length <= len) : "Losing entropy when constructing signals.";
		byte[] b = new byte[len];
		Arrays.fill(b, (byte) ((bs[0]<0)?0xff:0));
		System.arraycopy(bs, 0, b, len-Math.min(len, bs.length), Math.min(len, bs.length));
		Arrays.copyOf(bs, len);
		return new GCSignal(b);
	}
	
	public GCSignal(GCSignal lb) { v = lb.v; bytes = (lb.bytes == null) ? null : Arrays.copyOf(lb.bytes, len); }
	
	public boolean isPublic () { return bytes == null; }
	
	public GCSignal xor(GCSignal lb) {
		byte[] nb = new byte[len];
		for (int i = 0; i < len; i++)
			nb[i] = (byte) (bytes[i] ^ lb.bytes[i]);
		return new GCSignal(nb);
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
	public static GCSignal receive(InputStream ois) {
		byte[] b = new byte[len];
		try { ois.read(b);	}
		catch (Exception e) { e.printStackTrace(); }
		return new GCSignal(b);
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
	
	public String toHexStr() {
		StringBuilder str = new StringBuilder();
		for (byte b : bytes)
			str.append(Integer.toHexString(b & 0xff));
		return str.toString();
	}

	@Override
	public Signal ONE() {
		return new GCSignal(true);
	}

	@Override
	public Signal ZERO() {
		return new GCSignal(false);
	}
}
