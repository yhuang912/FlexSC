package network;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public final class RWBigInteger {
	public static void writeBI(OutputStream os, BigInteger bi) {
		try {
			NetworkUtil.writeByte(os, bi.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static BigInteger readBI(InputStream is) {
		byte[] rep = null;
		try {
			rep = NetworkUtil.readBytes(is);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return (rep == null)?BigInteger.ZERO:new BigInteger(rep);
	}	
}
