package network;

import java.io.OutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;


public final class RWBigInteger {
	public static void writeBI(OutputStream os, BigInteger bi) {
		try {
			byte[] rep = bi.toByteArray();
			os.write(ByteBuffer.allocate(4).putInt(rep.length).array());
			os.write(rep);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	static byte[] temp = new byte[4];
	public static BigInteger readBI(InputStream is) {
		byte[] rep = null;
		int len = -1;
		try {
			is.read(temp);
			rep = new byte[ByteBuffer.wrap(temp).getInt()];
			is.read(rep);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		try{ if (len == 0) throw new Exception("unbelievable"); } catch (Exception e) {e.printStackTrace();}
		
		return (len == 0)?BigInteger.ZERO:new BigInteger(rep);
	}	
}
